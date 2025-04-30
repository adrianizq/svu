import { type Ref, computed, defineComponent, inject, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import PqrsService from './pqrs.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import OficinaService from '@/entities/oficina/oficina.service';
import { type IOficina } from '@/shared/model/oficina.model';
import { type IPqrs, Pqrs } from '@/shared/model/pqrs.model';

import ArchivoAdjuntoService from '@/entities/archivo-adjunto/archivo-adjunto.service';
import { type IArchivoAdjunto } from '@/shared/model/archivo-adjunto.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PqrsUpdate',
  setup() {
    const pqrsService = inject('pqrsService', () => new PqrsService());
    const alertService = inject('alertService', () => useAlertService(), true);
    const archivoAdjuntoService = inject('archivoAdjuntoService', () => new ArchivoAdjuntoService());
    const oficinaService = inject('oficinaService', () => new OficinaService());

    const files = ref<File[]>([]); // Lista de archivos seleccionados (temporal)
    const fileInput = ref<HTMLInputElement | null>(null); // Referencia al input de tipo file
    const archivosAdjuntosDTO = ref<IArchivoAdjunto[]>([]); // Lista de archivos subidos
    const isUploading = ref(false); // Estado de subida
    const errorMessage = ref<string | null>(null); // Mensaje de error
    const successMessage = ref<string | null>(null); // Mensaje de éxito

    const pqrs: Ref<IPqrs> = ref(new Pqrs());
    const oficinas: Ref<IOficina[]> = ref([]);
    let isSaving = ref(false);

    const input = ref(null);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'es'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    // Método para eliminar un archivo de la lista y del input de tipo file
    const removeFile = (index: number) => {
      files.value = files.value.filter((_, i) => i !== index); // Crear una nueva lista sin el archivo que se desea eliminar

      // Reiniciar el input de tipo file
      if (fileInput.value) {
        fileInput.value.value = ''; // Reiniciar el valor del input
      }
    };

    // Método para abrir el diálogo de selección de archivos
    const triggerFileInput = () => {
      if (fileInput.value) {
        fileInput.value.click(); // Simular clic en el input de tipo file
      }
    };

    // Método para manejar la selección de archivos
    const onFileChange = (event: Event) => {
      input.value = event.target as HTMLInputElement;
      if (input.value.files && input.value.files.length > 0) {
        const newFiles = Array.from(input.value.files); // Convertir FileList a array
        files.value = [...files.value, ...newFiles]; // Agregar nuevos archivos a la lista
      }
    };

    // Subir archivos al servidor
    const uploadFiles = async () => {
      isUploading.value = true;
      errorMessage.value = null;
      successMessage.value = null;

      const formData = new FormData();
      files.value.forEach(file => {
        formData.append('files', file); // 'files' debe coincidir con el nombre del parámetro en el backend
      });

      formData.append('pqrs_id', route.params.pqrsId as string);

      try {
        console.log('Iniciando subida de archivos...'); // Debug
        const uploadResponse = await archivoAdjuntoService().uploadFiles(formData);
        archivosAdjuntosDTO.value = uploadResponse; // Guardar los archivos subidos (con sus IDs)

        //console.log('Archivos subidos correctamente:', archivosAdjuntosDTO.value); // Debug
        console.log('Archivos subidos correctamente:', uploadResponse.values); // Debug
        successMessage.value = 'Archivos subidos correctamente';
      } catch (error) {
        console.error('Error subiendo archivos:', error); // Debug
        errorMessage.value = 'Error al subir archivos';
        throw error; // Relanzar el error para manejarlo en el método save
      } finally {
        isUploading.value = false;
      }
    };

    // Obtener la PQRS actual
    const retrievePqrs = async (pqrsId: string) => {
      try {
        const res = await pqrsService().find(pqrsId);
        res.fechaCreacion = new Date(res.fechaCreacion);
        res.fechaLimiteRespuesta = new Date(res.fechaLimiteRespuesta);

        // Si la PQRS ya tiene archivos adjuntos, cargarlos
        if (res.archivosAdjuntosDTO) {
          archivosAdjuntosDTO.value = res.archivosAdjuntosDTO;

          files.value = res.archivosAdjuntosDTO.map(v => ({ name: v.nombre }));
        }

        pqrs.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    // Cargar la PQRS si estamos editando
    if (route.params?.pqrsId) {
      retrievePqrs(route.params.pqrsId as string);
    }

    // Inicializar relaciones (oficinas)
    const initRelationships = () => {
      oficinaService()
        .retrieve()
        .then(res => {
          oficinas.value = res.data;
        });
    };

    initRelationships();

    // Utilidades de datos y validación
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
      fileInput,
      archivosAdjuntosDTO,
      uploadFiles,
      onFileChange,
      triggerFileInput,
      removeFile,
      isUploading,
      errorMessage,
      successMessage,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: pqrs }),
      t$,
    };
  },
  methods: {
    created(): void {},

    async save(): Promise<void> {
      try {
        // Evitar doble envío
        if (this.isSaving) {
          return; // Si ya se está guardando, no hacer nada
        }

        this.isSaving = true; // Evitar doble envío
        console.log('entro a save');

        // 1️⃣ Subir los archivos antes de guardar la PQRS
        if (this.files.length > 0) {
          await this.uploadFiles();
        }

        this.pqrs.archivosAdjuntosDTO = this.archivosAdjuntosDTO;

        // this.pqrs.archivosAdjuntosDTO.value = this.archivosAdjuntosDTO.value;

        console.log('archivosAdjuntosDTO:', this.archivosAdjuntosDTO); // Debug

        console.log('llego hasta antes de uodate o guardar la pqrs');

        // 3️⃣ Guardar la PQRS en la base de datos
        let savedPqrs;
        if (this.pqrs.id) {
          savedPqrs = await this.pqrsService().update(this.pqrs);
          this.alertService.showInfo(this.t$('ventanillaUnicaApp.pqrs.updated', { param: savedPqrs.id }));
        } else {
          savedPqrs = await this.pqrsService().create(this.pqrs);
          console.log('PQRS creada:', savedPqrs);
          this.alertService.showSuccess(this.t$('ventanillaUnicaApp.pqrs.created', { param: savedPqrs.id }).toString());
        }

        this.previousState(); // Redirigir a la lista de PQRS
      } catch (error) {
        console.error('Error guardando la PQRS:', error);
        this.alertService.showHttpError(error.response ?? 'Ocurrió un error inesperado.');
      } finally {
        this.isSaving = false;
      }
    },
  },
});
