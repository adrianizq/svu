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

  public async uploadFile(file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file);

    return axios.post(`${baseApiUrl}/subir`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  }

  public async deleteArchivo(nombre: string): Promise<void> {
    return axios.delete(`${baseApiUrl}/${nombre}`);
  }
}
