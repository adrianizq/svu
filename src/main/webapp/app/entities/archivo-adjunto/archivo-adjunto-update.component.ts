import { defineComponent, ref } from 'vue';
import ArchivoAdjuntoService from './archivo-adjunto.service';

export default defineComponent({
  name: 'ArchivoAdjuntoComponent',
  setup() {
    const archivoAdjuntoService = new ArchivoAdjuntoService();
    const file = ref<File | null>(null);
    const successMessage = ref<string | null>(null);
    const errorMessage = ref<string | null>(null);

    const handleFileChange = (event: Event) => {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files[0]) {
        file.value = input.files[0];
      }
    };

    const uploadFile = async () => {
      if (!file.value) {
        errorMessage.value = 'Por favor selecciona un archivo para subir.';
        return;
      }

      try {
        const response = await archivoAdjuntoService.uploadFile(file.value);
        successMessage.value = 'Archivo subido exitosamente.';
        errorMessage.value = null;
        console.log('Archivo subido:', response);
      } catch (error) {
        successMessage.value = null;
        errorMessage.value = 'Error al subir el archivo.';
        console.error('Error subiendo archivo:', error);
      }
    };

    const eliminarArchivo = async () => {
      if (this.archivo && confirm('¿Está seguro de eliminar este archivo?')) {
        try {
          await this.archivoService.deleteArchivo(this.archivo.id);
          this.archivo = null; // Elimina el archivo del estado
        } catch (error) {
          console.error('Error eliminando el archivo:', error);
        }
      }
    };

    return {
      file,
      successMessage,
      errorMessage,
      handleFileChange,
      uploadFile,
      t$: (key: string) => key, // Simula la función de traducción
    };
  },
});
