import { computed, defineComponent, inject, ref, onUnmounted, watch, type Ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import { statesPqrs } from '../../constants';
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
    const { t: t$ } = useI18n(); // Definir t$ aquí

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

    const daysForResponse = ref<number | null>(null);
    const statesPqrsRef = ref(statesPqrs);

    const isAdminReactive = ref<boolean | null>(null);

    const checkAdmin = async () => {
      if (accountService) {
        try {
          const esAdminResultado = await accountService.hasAnyAuthorityAndCheckAuth('ROLE_ADMIN');
          isAdminReactive.value = esAdminResultado;
        } catch (error) {
          console.error('Error al verificar el estado de admin:', error);
          isAdminReactive.value = false;
        }
      } else {
        isAdminReactive.value = false;
      }
    };

    const isAdmin = computed(() => {
      return !!isAdminReactive.value;
    });

    onMounted(() => {
      checkAdmin();
      if (!route.params?.pqrsId) {
        // Promise.resolve().then(() => console.log('[DEBUG CREATE MODE onMounted - after tick] isAdmin final:', isAdmin.value));
      }
    });

    const isUpdateMode = computed(() => {
      const idExists = !!pqrs.value.id;
      return idExists;
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
      [daysForResponse, () => pqrs.value.fechaCreacion],
      ([newDays, newdateCreationObj]) => {
        if (isUpdateMode.value && isAdmin.value) {
          let dateCreationBase: Date | null = null;
          if (newdateCreationObj instanceof Date) {
            dateCreationBase = newdateCreationObj;
          } else if (typeof newdateCreationObj === 'string' && newdateCreationObj) {
            dateCreationBase = new Date(newdateCreationObj);
          }

          if (newDays !== null && typeof newDays === 'number' && newDays >= 0 && dateCreationBase && !isNaN(dateCreationBase.getTime())) {
            const newDateLimit = new Date(dateCreationBase.getTime());
            newDateLimit.setDate(newDateLimit.getDate() + newDays);
            pqrs.value.fechaLimiteRespuesta = newDateLimit;
          } else if (newDays === null && pqrs.value.fechaLimiteRespuesta !== undefined) {
            // Si se borran los días
          }
        }
      },
      { deep: false },
    );

    const retrievePqrs = async (pqrsId: string) => {
      try {
        const res = await pqrsService().find(pqrsId);

        res.dateCreation = res.dateCreation ? new Date(res.dateCreation) : null;
        res.dateLimitResponse = res.dateLimitResponse ? new Date(res.dateLimitResponse) : null;

        pqrs.value = res;

        if (pqrs.value.dateLimitResponse && pqrs.value.dateCreation) {
          const fc = new Date(pqrs.value.dateCreation.getTime());
          const flr = new Date(pqrs.value.dateLimitResponse.getTime());
          fc.setHours(0, 0, 0, 0);
          flr.setHours(0, 0, 0, 0);
          const diffTime = flr.getTime() - fc.getTime();
          if (diffTime >= 0) {
            const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
            daysForResponse.value = diffDays;
          } else {
            daysForResponse.value = null;
          }
        } else {
          daysForResponse.value = null;
        }

        if (res.archivosAdjuntosDTO) {
          existingFilesInfo.value = res.archivosAdjuntosDTO;
        } else {
          existingFilesInfo.value = [];
        }
        files.value = [];
      } catch (error: any) {
        if (error && error.response) {
          alertService.showHttpError(error.response);
        } else {
          alertService.showError(t$('ventanillaUnicaApp.pqrs.errors.retrieveError'));
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
    const downloadAttachedFile = async (fileURL, fileName) => {
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
    const dateFormatService = useDateFormat({ entityRef: pqrs }); // Captura el servicio completo
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
    const { convertDateTimeFromServer } = dateFormatService;
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
      statesPqrs: statesPqrsRef,
      daysForResponse,
      updateInstantField,
    };
  },
});
