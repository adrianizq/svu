import { type IOficina } from '@/shared/model/oficina.model';
import { type IArchivoAdjunto } from '@/shared/model/archivo-adjunto.model';

export interface IPqrs {
  id?: string;
  titulo?: string;
  descripcion?: string;
  fechaCreacion?: Date;
  fechaLimiteRespuesta?: Date | null;
  estado?: string;
  oficinaResponder?: IOficina | null;
  archivosAdjuntos?: IArchivoAdjunto[]; // Lista de archivos adjuntos
}

export class Pqrs implements IPqrs {
  constructor(
    public id?: string,
    public titulo?: string,
    public descripcion?: string,
    public fechaCreacion?: Date,
    public fechaLimiteRespuesta?: Date | null,
    public estado?: string,
    public oficinaResponder?: IOficina | null,
    public archivosAdjuntos?: IArchivoAdjunto[], // Lista de archivos adjuntos
  ) {}
}
