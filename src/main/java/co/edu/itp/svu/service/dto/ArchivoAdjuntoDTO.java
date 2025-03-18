package co.edu.itp.svu.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO for the {@link co.edu.itp.svu.domain.ArchivoAdjunto} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ArchivoAdjuntoDTO implements Serializable {

    private String id;

    @NotNull
    private String nombre;

    @NotNull
    private String tipo;

    private String urlArchivo;

    @NotNull
    private Instant fechaSubida;

    private PqrsDTO pqrs;

    private RespuestaDTO respuesta;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrlArchivo() {
        return urlArchivo;
    }

    public void setUrlArchivo(String urlArchivo) {
        this.urlArchivo = urlArchivo;
    }

    public Instant getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(Instant fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public PqrsDTO getPqrs() {
        return pqrs;
    }

    public void setPqrs(PqrsDTO pqrs) {
        this.pqrs = pqrs;
    }

    public RespuestaDTO getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(RespuestaDTO respuesta) {
        this.respuesta = respuesta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArchivoAdjuntoDTO)) {
            return false;
        }

        ArchivoAdjuntoDTO archivoAdjuntoDTO = (ArchivoAdjuntoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, archivoAdjuntoDTO.id);
    }

    /*Set<ArchivoAdjuntoDTO> archivosAdjuntos = pqrs.getArchivosAdjuntos().stream()
        .map(archivo -> new ArchivoAdjuntoDTO(archivo.getId(), archivo.getNombre(), archivo.getUrlArchivo()))
        .collect(Collectors.toSet());

resultDTO.setArchivosAdjuntos(archivosAdjuntos);*/

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ArchivoAdjuntoDTO{" +
            "id='" + getId() + "'" +
            ", nombre='" + getNombre() + "'" +
            ", tipo='" + getTipo() + "'" +
            ", urlArchivo='" + getUrlArchivo() + "'" +
            ", fechaSubida='" + getFechaSubida() + "'" +
            ", pqrs=" + getPqrs() +
            ", respuesta=" + getRespuesta() +
            "}";
    }
}
