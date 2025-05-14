import axios, { type AxiosResponse } from 'axios';

import { type IArchivoAdjunto } from '@/shared/model/archivo-adjunto.model';

const baseApiUrl = 'api/archivo-adjuntos';

export default class ArchivoAdjuntoService {
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

  public async uploadFile(file: File): Promise<IArchivoAdjunto> {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await axios.post(`${baseApiUrl}/subir`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      throw new Error('Error uploading the file');
    }
  }

  public uploadFiles(formData: FormData): Promise<IArchivoAdjunto[]> {
    return new Promise<IArchivoAdjunto[]>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/upload`, formData, {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        })
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public async deleteArchivo(nombre: string): Promise<void> {
    return axios.delete(`${baseApiUrl}/${nombre}`);
  }

  public async deleteMultiple(filesToDelete: string[]): Promise<void> {
    return axios.delete(`${baseApiUrl}/delete-multiple`, { data: { fileNameList: filesToDelete } });
  }

  public async downloadAttachedFile(fileIdentifier: string): Promise<{ blob: Blob }> {
    const encodedIdentifier = encodeURIComponent(fileIdentifier);
    const url = `${baseApiUrl}/download/${encodedIdentifier}`;

    const response: AxiosResponse<Blob> = await axios.get(url, {
      responseType: 'blob',
    });
    return { blob: response.data };
  }
}
