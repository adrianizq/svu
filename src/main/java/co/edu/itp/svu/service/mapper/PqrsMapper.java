package co.edu.itp.svu.service.mapper;

import co.edu.itp.svu.domain.ArchivoAdjunto;
import co.edu.itp.svu.domain.Oficina;
import co.edu.itp.svu.domain.Pqrs;
import co.edu.itp.svu.service.dto.ArchivoAdjuntoDTO;
import co.edu.itp.svu.service.dto.OficinaDTO;
import co.edu.itp.svu.service.dto.PqrsDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Pqrs} and its DTO {@link PqrsDTO}.
 */
/*
@Mapper(componentModel = "spring")
public interface PqrsMapper extends EntityMapper<PqrsDTO, Pqrs> {
    @Mapping(target = "oficinaResponder", source = "oficinaResponder", qualifiedByName = "oficinaId")
        @Mapping(target = "archivosAdjuntosDTO", source = "archivosAdjuntos") // ✅ Agregar esta línea

    PqrsDTO toDto(Pqrs s);

    @Named("oficinaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OficinaDTO toDtoOficinaId(Oficina oficina);
}
*/
/*
@Mapper(componentModel = "spring", uses = {ArchivoAdjuntoMapper.class})
public interface PqrsMapper extends EntityMapper<PqrsDTO, Pqrs> {
    @Mapping(target = "oficinaResponder", source = "oficinaResponder", qualifiedByName = "oficinaId")
    @Mapping(target = "archivosAdjuntosDTO", source = "archivosAdjuntos") // ✅ Agregar esta línea
    PqrsDTO toDto(Pqrs s);

    @Named("oficinaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OficinaDTO toDtoOficinaId(Oficina oficina);
}
*/
@Mapper(componentModel = "spring")
public interface PqrsMapper extends EntityMapper<PqrsDTO, Pqrs> {
    @Mapping(target = "oficinaResponder", source = "oficinaResponder", qualifiedByName = "oficinaId")
    PqrsDTO toDto(Pqrs s);

    @Named("oficinaId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OficinaDTO toDtoOficinaId(Oficina oficina);
}
