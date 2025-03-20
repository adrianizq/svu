package co.edu.itp.svu.service.mapper;

import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.domain.Pqrs;
import co.edu.itp.svu.domain.Respuesta;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.service.dto.PqrsDTO;
import co.edu.itp.svu.service.dto.RespuestaDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ArchivoAdjuntoMapper extends EntityMapper<ArchivoAdjuntoDTO, ArchivoAdjunto> {
    // Mapeo básico de ArchivoAdjunto a ArchivoAdjuntoDTO
    ArchivoAdjuntoDTO toDto(ArchivoAdjunto s);

    // Mapeo básico de ArchivoAdjuntoDTO a ArchivoAdjunto
    ArchivoAdjunto toEntity(ArchivoAdjuntoDTO dto);
}
