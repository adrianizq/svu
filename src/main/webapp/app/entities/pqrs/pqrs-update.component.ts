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

import axios from 'axios'; // Para subir archivos

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

    const uploadedFiles = ref<IArchivoAdjunto[]>([]); // Lista de archivos subidos
    const isUploading = ref(false); // Estado de subida
    const errorMessage = ref<string | null>(null); // Mensaje de error
    const successMessage = ref<string | null>(null); // Mensaje de éxito

    const pqrs: Ref<IPqrs> = ref(new Pqrs());
    const oficinas: Ref<IOficina[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'es'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    // Eliminar un archivo de la lista de seleccionados
    /*
const removeFile = (index: number) => {
  console.log('Archivo a eliminar:', files.value[index].name); // Verifica que el archivo correcto se está eliminando
  files.value = files.value.filter((_, i) => i !== index); // Crear una nueva lista sin el archivo que se desea eliminar
  console.log('Lista actualizada:', files.value); // Verifica que la lista se actualizó correctamente
}; */

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

    // Subir archivos al servidor cuando se guarde la PQRS
    const uploadFiles = async () => {
      isUploading.value = true;
      errorMessage.value = null;
      successMessage.value = null;

      try {
        // Subir cada archivo individualmente
        for (const file of files.value) {
          const formData = new FormData();
          formData.append('file', file); // Agregar el archivo al FormData

          // Subir el archivo al servidor
          const response = await archivoAdjuntoService.uploadFile(formData);
          console.log('Archivo subido:', response.data);

          // Agregar el archivo subido a la lista de archivos adjuntos
          uploadedFiles.value.push(response.data);
        }

        successMessage.value = 'Archivos subidos exitosamente.';
      } catch (error) {
        errorMessage.value = 'Error al subir los archivos.';
        console.error('Error subiendo archivos:', error);
      } finally {
        isUploading.value = false;
      }
    };

    /*
    const onFileChange = (event: Event) => {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files.length > 0) {
        const newFiles = Array.from(input.files);
        files.value = [...files.value, ...newFiles];
      }
    };
*/
    // Método para manejar la selección de archivos
    const onFileChange = (event: Event) => {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files.length > 0) {
        const newFiles = Array.from(input.files); // Convertir FileList a array
        files.value = [...files.value, ...newFiles]; // Agregar nuevos archivos a la lista
      }
    };

    // Obtener la PQRS actual
    const retrievePqrs = async (pqrsId: string) => {
      try {
        const res = await pqrsService().find(pqrsId);
        res.fechaCreacion = new Date(res.fechaCreacion);
        res.fechaLimiteRespuesta = new Date(res.fechaLimiteRespuesta);

        // Si la PQRS ya tiene archivos adjuntos, cargarlos
        if (res.archivosAdjuntos) {
          uploadedFiles.value = res.archivosAdjuntos;
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
      fechaCreacion: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      fechaLimiteRespuesta: {},
      estado: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      oficinaResponder: {},
    };
    const v$ = useVuelidate(validationRules, pqrs as any);
    v$.value.$validate();

    return {
      pqrsService,
      alertService,
      pqrs,
      previousState,
      isSaving,
      currentLanguage,
      oficinas,
      files,
      fileInput,
      uploadedFiles,
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
    async save(): Promise<void> {
      /*try {
        // Subir los archivos antes de guardar la PQRS
        await this.uploadFiles();

        // Asignar los archivos subidos a la PQRS
        this.pqrs.archivosAdjuntosDTO = this.uploadedFiles;

        // Guardar la PQRS
        if (this.pqrs.id) {
          const updatedPqrs = await this.pqrsService().update(this.pqrs);
          alertService.showInfo(this.t$('ventanillaUnicaApp.pqrs.updated', { param: updatedPqrs.id }));
        } else {
          const createdPqrs = await this.pqrsService().create(this.pqrs);
          this.alertService.showSuccess(this.t$('ventanillaUnicaApp.pqrs.created', { param: createdPqrs.id }).toString());
        }

        // Redirigir a la página anterior
        this.previousState();
      } catch (error) {
        this.alertService.showHttpError(error.response);
      } */
      try {
        // Subir los archivos antes de guardar la PQRS
        await uploadFiles();

        // Asignar los archivos subidos a la PQRS
        pqrs.value.archivosAdjuntosDTO = uploadedFiles.value;

        // Guardar la PQRS
        if (pqrs.value.id) {
          const updatedPqrs = await pqrsService().update(pqrs.value);
          alertService.showInfo(t$('ventanillaUnicaApp.pqrs.updated', { param: updatedPqrs.id }));
        } else {
          const createdPqrs = await pqrsService().create(pqrs.value);
          alertService.showSuccess(t$('ventanillaUnicaApp.pqrs.created', { param: createdPqrs.id }).toString());
        }

        // Redirigir a la página anterior
        previousState();
      } catch (error) {
        // Verificar si error.response existe antes de pasarlo a showHttpError
        if (error.response) {
          alertService.showHttpError(error.response); // Usar alertService directamente, sin "this"
        } else {
          alertService.showError('Ocurrió un error inesperado.'); // Mensaje genérico
        }
      }
    },
  },
});
