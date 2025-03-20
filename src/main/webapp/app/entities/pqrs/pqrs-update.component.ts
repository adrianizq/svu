/*import { type Ref, computed, defineComponent, inject, ref } from 'vue';
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

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PqrsUpdate',
  setup() {
    const pqrsService = inject('pqrsService', () => new PqrsService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const pqrs: Ref<IPqrs> = ref(new Pqrs());

    const oficinaService = inject('oficinaService', () => new OficinaService());

    const oficinas: Ref<IOficina[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'es'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrievePqrs = async pqrsId => {
      try {
        const res = await pqrsService().find(pqrsId);
        res.fechaCreacion = new Date(res.fechaCreacion);
        res.fechaLimiteRespuesta = new Date(res.fechaLimiteRespuesta);
        pqrs.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.pqrsId) {
      retrievePqrs(route.params.pqrsId);
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
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: pqrs }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.pqrs.id) {
        this.pqrsService()
          .update(this.pqrs)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('ventanillaUnicaApp.pqrs.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.pqrsService()
          .create(this.pqrs)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('ventanillaUnicaApp.pqrs.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
}); */

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

import ArchivoAdjuntoService from '../archivo-adjunto/archivo-adjunto.service';
import { type IArchivoAdjunto, ArchivoAdjunto } from '@/shared/model/archivo-adjunto.model';

import axios from 'axios'; // ✅ **Nuevo: Importar axios para subir archivos**

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'PqrsUpdate',
  setup() {
    const pqrsService = inject('pqrsService', () => new PqrsService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const pqrs: Ref<IPqrs> = ref(new Pqrs());

    const oficinaService = inject('oficinaService', () => new OficinaService());

    const oficinas: Ref<IOficina[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'es'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    /*const retrievePqrs = async pqrsId => {
      try {
        const res = await pqrsService().find(pqrsId);
        res.fechaCreacion = new Date(res.fechaCreacion);
        res.fechaLimiteRespuesta = new Date(res.fechaLimiteRespuesta);
        pqrs.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };*/
    /*const retrievePqrs = async pqrsId => {
      try {
        const res = await pqrsService().find(pqrsId);
        res.fechaCreacion = new Date(res.fechaCreacion);
        res.fechaLimiteRespuesta = new Date(res.fechaLimiteRespuesta);
    
        //  Obtener los archivos adjuntos de la PQRS
        const archivosResponse = await ArchivoAdjuntoService().retrieveByPqrs(pqrsId);
        res.archivosAdjuntos = archivosResponse.data;
    
        pqrs.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };*/

    const retrievePqrs = async pqrsId => {
      try {
        const res = await pqrsService().find(pqrsId);
        res.fechaCreacion = new Date(res.fechaCreacion);
        res.fechaLimiteRespuesta = new Date(res.fechaLimiteRespuesta);

        // ✅ La lista de archivos adjuntos ya está incluida en la respuesta
        pqrs.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.pqrsId) {
      retrievePqrs(route.params.pqrsId);
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

    const archivo = ref(null);
    const isUploading = ref(false);

    // ✅ Captura el archivo seleccionado
    const onFileChange = event => {
      const input = event.target;
      if (input.files.length > 0) {
        archivo.value = input.files[0];
      }
    };

    // ✅ Subir el archivo al servidor
    const uploadFile = async () => {
      if (!archivo.value) {
        alertService.showWarning('Por favor, seleccione un archivo.');
        return;
      }

      isUploading.value = true;

      try {
        const formData = new FormData();
        formData.append('file', archivo.value);

        const response = await axios.post('/api/archivos-adjuntos', formData);

        alertService.showSuccess('Archivo subido correctamente: ' + response.data.nombreArchivo);

        // ✅ Puedes almacenar el nombre del archivo o hacer otras acciones aquí
      } catch (error) {
        alertService.showHttpError(error.response);
      } finally {
        isUploading.value = false;
      }
    };

    return {
      pqrsService,
      alertService,
      pqrs,
      previousState,
      isSaving,
      currentLanguage,
      oficinas,
      //archivosAdjuntos, // ✅ **Nuevo**
      onFileChange, // ✅ **Nuevo**
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: pqrs }),
      t$,
    };
  },
  created(): void {},
  methods: {
    async save(): Promise<void> {
      this.isSaving = true;

      try {
        // ✅ **Nuevo: Subir archivos antes de guardar la PQRS**
        if (this.archivosAdjuntos.length > 0) {
          const formData = new FormData();
          this.archivosAdjuntos.forEach(file => {
            formData.append('files', file);
          });

          const response = await axios.post('/api/archivos-adjuntos', formData);
          this.pqrs.archivosAdjuntos = response.data.map(archivo => archivo.urlArchivo);
        }

        // ✅ **Guardar PQRS con las URLs de los archivos**
        if (this.pqrs.id) {
          const updatedPqrs = await this.pqrsService().update(this.pqrs);
          this.alertService.showInfo(this.t$('ventanillaUnicaApp.pqrs.updated', { param: updatedPqrs.id }));
        } else {
          const createdPqrs = await this.pqrsService().create(this.pqrs);
          this.alertService.showSuccess(this.t$('ventanillaUnicaApp.pqrs.created', { param: createdPqrs.id }).toString());
        }

        this.previousState();
      } catch (error) {
        this.alertService.showHttpError(error.response);
      } finally {
        this.isSaving = false;
      }
    },
  },
});
