package co.edu.itp.svu.service.mapper;

import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.domain.Pqrs;
import co.edu.itp.svu.domain.Respuesta;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.service.dto.PqrsDTO;
import co.edu.itp.svu.service.dto.RespuestaDTO;
import org.mapstruct.*;

/*
@Mapper(componentModel = "spring", uses = {PqrsMapper.class, RespuestaMapper.class})
public interface ArchivoAdjuntoMapper extends EntityMapper<ArchivoAdjuntoDTO, ArchivoAdjunto> {

    @Mapping(source = "pqrs.id", target = "pqrsId")
    @Mapping(source = "respuesta.id", target = "respuestaId")
    @Mapping(target = "archivo", ignore = true)
    @Mapping(source = "archivo", target = "file")
    @Mapping(source = "archivoContentType", target = "contentType")
    ArchivoAdjuntoDTO toDto(ArchivoAdjunto archivoAdjunto);

    @Mapping(source = "pqrsId", target = "pqrs")
    @Mapping(source = "respuestaId", target = "respuesta")
    @Mapping(source = "file", target = "archivo")
    @Mapping(source = "contentType", target = "archivoContentType")
    ArchivoAdjunto toEntity(ArchivoAdjuntoDTO archivoAdjuntoDTO);

    @Named("pqrsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PqrsDTO toDtoPqrsId(Pqrs pqrs);

    @Named("respuestaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RespuestaDTO toDtoRespuestaId(Respuesta respuesta);

    default ArchivoAdjunto fromId(String id) {
        if (id == null) {
            return null;
        }
        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
        archivoAdjunto.setId(id);
        return archivoAdjunto;
    }
}

 */
/*
@Mapper(componentModel = "spring", uses = {PqrsMapper.class, RespuestaMapper.class})
public interface ArchivoAdjuntoMapper extends EntityMapper<ArchivoAdjuntoDTO, ArchivoAdjunto> {

    @Mapping(source = "pqrs.id", target = "pqrsId")
    @Mapping(source = "respuesta.id", target = "respuestaId")
    @Mapping(target = "file", source = "archivo", qualifiedByName = "byteArrayToString")
    @Mapping(target = "contentType", source = "archivoContentType")
    ArchivoAdjuntoDTO toDto(ArchivoAdjunto archivoAdjunto);

    @Mapping(source = "pqrsId", target = "pqrs")
    @Mapping(source = "respuestaId", target = "respuesta")
    @Mapping(target = "archivo", source = "file", qualifiedByName = "stringToByteArray")
    @Mapping(target = "archivoContentType", source = "contentType")
    ArchivoAdjunto toEntity(ArchivoAdjuntoDTO archivoAdjuntoDTO);

    // Conversión personalizada: byte[] -> String
    @Named("byteArrayToString")
    static String byteArrayToString(byte[] archivo) {
        return archivo != null ? new String(archivo) : null;
    }

    // Conversión personalizada: String -> byte[]
    @Named("stringToByteArray")
    static byte[] stringToByteArray(String file) {
        return file != null ? file.getBytes() : null;
    }

    // Conversión parcial para PQRS
    @Named("pqrsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PqrsDTO toDtoPqrsId(Pqrs pqrs);

    // Conversión parcial para Respuesta
    @Named("respuestaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RespuestaDTO toDtoRespuestaId(Respuesta respuesta);

    default ArchivoAdjunto fromId(String id) {
        if (id == null) {
            return null;
        }
        ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
        archivoAdjunto.setId(id);
        return archivoAdjunto;
    }
}
*/

@Mapper(componentModel = "spring")
public interface ArchivoAdjuntoMapper extends EntityMapper<ArchivoAdjuntoDTO, ArchivoAdjunto> {
    @Mapping(target = "pqrs", source = "pqrs", qualifiedByName = "pqrsId")
    @Mapping(target = "respuesta", source = "respuesta", qualifiedByName = "respuestaId")
    ArchivoAdjuntoDTO toDto(ArchivoAdjunto s);

    @Named("pqrsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PqrsDTO toDtoPqrsId(Pqrs pqrs);

    @Named("respuestaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RespuestaDTO toDtoRespuestaId(Respuesta respuesta);
}
