import axios from 'axios';

import { type IArchivoAdjunto } from '@/shared/model/archivo-adjunto.model';

const baseApiUrl = 'api/archivo-adjuntos';

export default class ArchivoAdjuntoService {
  private archivo: File | null = null;
  private nombrePqrs: string = '';

  public find(id: string): Promise<IArchivoAdjunto> {
    return new Promise<IArchivoAdjunto>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public retrieve(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public delete(id: string): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public create(entity: IArchivoAdjunto): Promise<IArchivoAdjunto> {
    return new Promise<IArchivoAdjunto>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public update(entity: IArchivoAdjunto): Promise<IArchivoAdjunto> {
    return new Promise<IArchivoAdjunto>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public partialUpdate(entity: IArchivoAdjunto): Promise<IArchivoAdjunto> {
    return new Promise<IArchivoAdjunto>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  // Maneja el cambio de archivo
  /* private onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.archivo = input.files[0];
    }
  }*/

  // Método para subir un archivo con el parámetro nombrePqrs

  /* public async createWithFile(): Promise<any> {
   //const formData = new FormData();
   //formData.append('archivo', archivo); // Aquí agregas el archivo

    //formData.append('nombre', nombre);
    //   formData.append('tipo', tipo);
   //     formData.append('fechaSubida', fechaSubida);
        //formData.append('pqrs', JSON.stringify(this.archivoAdjunto.pqrs));
     // Aquí agregas el parámetro nombrePqrs

      //
      try {
        // Realizamos la solicitud POST para subir el archivo
        const response = await axios.post('/api/subir-archivo', {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        });
        return response.data; // Devuelve los datos del archivo subido (como el ID, URL, etc.)
      } catch (error) {
        console.error('Error al subir el archivo:', error);
        throw new Error('Hubo un error al subir el archivo.');
      }
    
  
  }
*/

  public async uploadFile(file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);

    return axios.post(`${baseApiUrl}/subir`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }
}
