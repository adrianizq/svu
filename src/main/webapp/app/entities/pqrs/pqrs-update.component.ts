import { computed, defineComponent, inject, ref, onUnmounted, watch, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import { LISTA_ESTADOS_PQRS } from '../../constants';
import PqrsService from './pqrs.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import OficinaService from '@/entities/oficina/oficina.service';
import { type IOficina } from '@/shared/model/oficina.model';
import { type IPqrs, Pqrs } from '@/shared/model/pqrs.model';

import ArchivoAdjuntoService from '@/entities/archivo-adjunto/archivo-adjunto.service';
import { type IArchivoAdjunto } from '@/shared/model/archivo-adjunto.model';
import type AccountService from '@/account/account.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PqrsUpdate',
  setup() {
    const pqrsService = inject('pqrsService', () => new PqrsService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const archivoAdjuntoService = inject('archivoAdjuntoService', () => new ArchivoAdjuntoService());
    const accountService = inject<AccountService>('accountService');
    const oficinaService = inject('oficinaService', () => new OficinaService());

    const files = ref<File[]>([]);
    const existingFilesInfo: Ref<IArchivoAdjunto[]> = ref([]);
    const filesToDelete: Ref<string[]> = ref([]);
    const fileInput = ref<HTMLInputElement | null>(null);
    const archivosAdjuntosDTO = ref<IArchivoAdjunto[]>([]);
    const isUploading = ref(false);
    const errorMessage = ref<string | null>(null);
    const successMessage = ref<string | null>(null);

    const pqrs: Ref<IPqrs> = ref(new Pqrs());
    const oficinas: Ref<IOficina[]> = ref([]);
    const isSaving = ref(false);

    const input: Ref<HTMLInputElement | null> = ref(null);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'es'), true);

    const route = useRoute();
    const router = useRouter();

    const diasParaRespuesta = ref<number | null>(null);
    const listaEstadosPqrs = ref(LISTA_ESTADOS_PQRS);

    // inicio del codigo
    const isUpdateMode = computed(() => {
      return !!pqrs.value.id;
    });

    const isAdmin = computed(() => {
      if (accountService) {
        const adminCheck = accountService.hasAnyAuthorityAndCheckAuth('ROLE_ADMIN');
        return adminCheck;
      }
      return false;
    });

    const updateInstantField = (field: keyof IPqrs, event: Event) => {
      const target = event.target as HTMLInputElement;
      if (target.value) {
        pqrs.value[field] = new Date(target.value);
      } else {
        pqrs.value[field] = null;
      }
    };

    watch(
      [diasParaRespuesta, () => pqrs.value.fechaCreacion],
      ([newDias, newFechaCreacion]) => {
        if (isUpdateMode.value && isAdmin.value) {
          // Solo calcular si es admin en modo edición
          if (newDias !== null && newDias >= 0 && newFechaCreacion instanceof Date) {
            const fechaCreacionObj = new Date(newFechaCreacion);
            const nuevaFechaLimite = new Date(fechaCreacionObj);
            nuevaFechaLimite.setDate(fechaCreacionObj.getDate() + newDias);

            // Asignar al campo fechaLimiteRespuesta del objeto pqrs
            // Es importante que v$.fechaLimiteRespuesta.$model se actualice,
            // lo cual sucede si está enlazado a pqrs.value.fechaLimiteRespuesta
            pqrs.value.fechaLimiteRespuesta = nuevaFechaLimite;
            console.log('[DEBUG] Nueva fechaLimiteRespuesta calculada:', pqrs.value.fechaLimiteRespuesta);
          } else if (newDias === null && pqrs.value.fechaLimiteRespuesta !== undefined) {
            // Si los días se borran, podríamos querer limpiar la fecha límite o dejarla como está.
            // Por ahora, la limpiamos si el usuario borra los días.
            // pqrs.value.fechaLimiteRespuesta = null; // Opcional: limpiar si los días se borran
          }
        }
      },
      { immediate: false },
    ); // immediate: false para que no se ejecute al inicio antes de que todo esté cargado, a menos que lo necesites.

    // ... retrievePqrs (asegúrate que inicializa pqrs.value.fechaCreacion como Date) ...
    const retrievePqrs = async (pqrsId: string) => {
      try {
        const res = await pqrsService().find(pqrsId);

        // Asegurar que las fechas sean objetos Date o null
        res.fechaCreacion = res.fechaCreacion ? new Date(res.fechaCreacion) : null;
        res.fechaLimiteRespuesta = res.fechaLimiteRespuesta ? new Date(res.fechaLimiteRespuesta) : null;

        // Pre-calcular y mostrar los días si ambas fechas existen al cargar
        if (res.fechaLimiteRespuesta && res.fechaCreacion) {
          const diffTime = Math.abs(res.fechaLimiteRespuesta.getTime() - res.fechaCreacion.getTime());
          const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
          diasParaRespuesta.value = diffDays; // Asegúrate que 'diasParaRespuesta' esté definido como ref
        } else {
          diasParaRespuesta.value = null; // Resetear si no hay datos para calcular
        }

        const retrievePqrs = async (pqrsId: string) => {
          try {
            const res = await pqrsService().find(pqrsId);

            // Es CRUCIAL que fechaCreacion sea un objeto Date aquí para el watch y el cálculo
            res.fechaCreacion = res.fechaCreacion ? new Date(res.fechaCreacion) : null;
            res.fechaLimiteRespuesta = res.fechaLimiteRespuesta ? new Date(res.fechaLimiteRespuesta) : null;

            pqrs.value = res; // Asignar primero para que fechaCreacion esté disponible

            // Luego, si quieres pre-rellenar diasParaRespuesta al cargar:
            if (pqrs.value.fechaLimiteRespuesta && pqrs.value.fechaCreacion) {
              // Cálculo de diferencia de días solo en la PARTE ENTERA del día
              // Para evitar problemas con horas, normalizamos ambas fechas al inicio del día
              const fc = new Date(
                pqrs.value.fechaCreacion.getFullYear(),
                pqrs.value.fechaCreacion.getMonth(),
                pqrs.value.fechaCreacion.getDate(),
              );
              const flr = new Date(
                pqrs.value.fechaLimiteRespuesta.getFullYear(),
                pqrs.value.fechaLimiteRespuesta.getMonth(),
                pqrs.value.fechaLimiteRespuesta.getDate(),
              );

              const diffTime = flr.getTime() - fc.getTime(); // Diferencia en milisegundos
              if (diffTime >= 0) {
                // Solo si la fecha límite es igual o posterior
                const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24)); // Redondear al día más cercano
                diasParaRespuesta.value = diffDays;
              } else {
                diasParaRespuesta.value = null; // Fechas inconsistentes
              }
            } else {
              diasParaRespuesta.value = null;
            }

            // ... manejo de adjuntos, etc. ...
          } catch (error: any) {
            // ...
          }
        };

        // Manejo de archivos adjuntos (de tu código original)
        if (res.archivosAdjuntosDTO) {
          existingFilesInfo.value = res.archivosAdjuntosDTO; // Asegúrate que 'existingFilesInfo' esté definido como ref
        } else {
          existingFilesInfo.value = [];
        }
        files.value = []; // Asegúrate que 'files' esté definido como ref

        // Asignar el objeto PQRS
        pqrs.value = res;
      } catch (error: any) {
        // Tu manejo de errores consistente
        if (error && error.response) {
          alertService.showHttpError(error.response);
        } else {
          // Puedes personalizar este mensaje si lo deseas
          alertService.showError(t$('ventanillaUnicaApp.pqrs.errors.retrieveError')); // Usando i18n
          console.error('Error retrieving PQRS data:', error);
        }
      }
    };

    const previousState = () => {
      filesToDelete.value = [];
      router.go(-1);
    };

    const removeFile = (index: number) => {
      files.value = files.value.filter((_, i) => i !== index);

      if (fileInput.value) {
        fileInput.value.value = '';
      }
    };

    const removeExistingFile = (index: number) => {
      if (existingFilesInfo.value && Array.isArray(existingFilesInfo.value) && existingFilesInfo.value[index]) {
        const fileToRemove = existingFilesInfo.value[index];

        if (fileToRemove.urlArchivo && !filesToDelete.value.includes(fileToRemove.urlArchivo)) {
          filesToDelete.value.push(fileToRemove.urlArchivo);
        }

        existingFilesInfo.value = existingFilesInfo.value.filter((_, currentIndex) => currentIndex !== index);
      }
    };

    const triggerFileInput = () => {
      if (fileInput.value) {
        fileInput.value.click();
      }
    };

    const onFileChange = (event: Event) => {
      const target = event.target as HTMLInputElement;
      if (target && target.files && target.files.length > 0) {
        input.value = target;
        const newFiles = Array.from(target.files);
        files.value = [...files.value, ...newFiles];
        target.value = '';
      }
    };

    const uploadFiles = async () => {
      isUploading.value = true;
      errorMessage.value = null;
      successMessage.value = null;

      const formData = new FormData();
      files.value.forEach(file => {
        formData.append('files', file);
      });

      try {
        const uploadResponse = await archivoAdjuntoService().uploadFiles(formData);
        archivosAdjuntosDTO.value = uploadResponse;

        successMessage.value = 'Archivos subidos correctamente';
      } catch (error) {
        errorMessage.value = 'Error al subir archivos';
        throw error;
      } finally {
        isUploading.value = false;
      }
    };

    const downloadAttachedFile = async (fileURL: string | null | undefined, fileName: string | null | undefined) => {
      if (!fileURL && !fileName) {
        return;
      }
      try {
        if (fileURL && fileName) {
          const { blob } = await archivoAdjuntoService().downloadAttachedFile(fileURL);
          const link = document.createElement('a');
          const url = window.URL.createObjectURL(blob);
          link.href = url;
          link.setAttribute('download', fileName);
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
        }
      } catch (error: any) {
        alertService.showHttpError(error.response ?? 'Ocurrió un error inesperado.');
      }
    };

    const save = async (): Promise<void> => {
      try {
        if (isSaving.value) {
          return;
        }

        isSaving.value = true;
        if (filesToDelete.value.length > 0) {
          await archivoAdjuntoService().deleteMultiple(filesToDelete.value);
          filesToDelete.value = [];
        }

        if (files.value.length > 0) {
          await uploadFiles();
          if (pqrs.value.archivosAdjuntosDTO) {
            pqrs.value.archivosAdjuntosDTO = pqrs.value.archivosAdjuntosDTO?.concat(archivosAdjuntosDTO.value);
          } else {
            pqrs.value.archivosAdjuntosDTO = archivosAdjuntosDTO.value;
          }
        }

        let savedPqrs;
        if (pqrs.value.id) {
          savedPqrs = await pqrsService().update(pqrs.value);
          alertService.showInfo(t$('ventanillaUnicaApp.pqrs.updated', { param: savedPqrs.id }));
        } else {
          savedPqrs = await pqrsService().create(pqrs.value);
          alertService.showSuccess(t$('ventanillaUnicaApp.pqrs.created', { param: savedPqrs.id }).toString());
        }

        previousState();
      } catch (error: any) {
        alertService.showHttpError(error.response ?? 'Ocurrió un error inesperado.');
      } finally {
        isSaving.value = false;
      }
    };

    onUnmounted(() => {
      filesToDelete.value = [];
    });

    if (route.params?.pqrsId) {
      retrievePqrs(route.params.pqrsId as string);
    }

    const initRelationships = () => {
      oficinaService()
        .retrieve()
        .then(res => {
          oficinas.value = res.data;
        });
    };

    initRelationships();

    const dataUtils = useDataUtils();
    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      titulo: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      descripcion: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      fechaCreacion: {},
      fechaLimiteRespuesta: {},
      estado: {},
      oficinaResponder: {},
    };
    const v$ = useVuelidate(validationRules, pqrs as any);
    v$.value.$validate();

    return {
      input,
      pqrsService,
      alertService,
      pqrs,
      previousState,
      isSaving,
      currentLanguage,
      oficinas,
      files,
      existingFilesInfo,
      downloadAttachedFile,
      fileInput,
      archivosAdjuntosDTO,
      uploadFiles,
      onFileChange,
      triggerFileInput,
      removeFile,
      removeExistingFile,
      save,
      isUploading,
      errorMessage,
      successMessage,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: pqrs }),
      t$,
      isUpdateMode,
      isAdmin,
      listaEstadosPqrs,
      diasParaRespuesta,
      updateInstantField,
    };
  },
});
