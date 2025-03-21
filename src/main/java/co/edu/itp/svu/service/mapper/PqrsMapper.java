package co.edu.itp.svu.service.mapper;

import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.domain.Oficina;
import co.edu.itp.svu.domain.Pqrs;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.service.dto.OficinaDTO;
import co.edu.itp.svu.service.dto.PqrsDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/*
@Mapper(componentModel = "spring")
public interface PqrsMapper extends EntityMapper<PqrsDTO, Pqrs> {
    @Mapping(target = "oficinaResponder", source = "oficinaResponder", qualifiedByName = "oficinaId")
    PqrsDTO toDto(Pqrs s);

    @Named("oficinaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OficinaDTO toDtoOficinaId(Oficina oficina);
}
*/
@Mapper(componentModel = "spring")
public interface PqrsMapper extends EntityMapper<PqrsDTO, Pqrs> {
    @Mapping(target = "archivosAdjuntosDTO", source = "archivosAdjuntos")
    PqrsDTO toDto(Pqrs pqrs);

    @Mapping(target = "archivosAdjuntos", source = "archivosAdjuntosDTO")
    Pqrs toEntity(PqrsDTO pqrsDTO);

    default Set<ArchivoAdjuntoDTO> mapArchivosAdjuntosToDTOs(Set<ArchivoAdjunto> archivosAdjuntos) {
        if (archivosAdjuntos == null) {
            return null;
        }
        return archivosAdjuntos
            .stream()
            .map(archivoAdjunto -> {
                ArchivoAdjuntoDTO dto = new ArchivoAdjuntoDTO();
                dto.setId(archivoAdjunto.getId());
                dto.setNombre(archivoAdjunto.getNombre());
                dto.setTipo(archivoAdjunto.getTipo());
                dto.setUrlArchivo(archivoAdjunto.getUrlArchivo());
                dto.setFechaSubida(archivoAdjunto.getFechaSubida());
                return dto;
            })
            .collect(Collectors.toSet());
    }

    default Set<ArchivoAdjunto> mapDTOsToArchivosAdjuntos(Set<ArchivoAdjuntoDTO> archivosAdjuntosDTO) {
        if (archivosAdjuntosDTO == null) {
            return null;
        }
        return archivosAdjuntosDTO
            .stream()
            .map(dto -> {
                ArchivoAdjunto archivoAdjunto = new ArchivoAdjunto();
                archivoAdjunto.setId(dto.getId());
                archivoAdjunto.setNombre(dto.getNombre());
                archivoAdjunto.setTipo(dto.getTipo());
                archivoAdjunto.setUrlArchivo(dto.getUrlArchivo());
                archivoAdjunto.setFechaSubida(dto.getFechaSubida());
                return archivoAdjunto;
            })
            .collect(Collectors.toSet());
    }
}
