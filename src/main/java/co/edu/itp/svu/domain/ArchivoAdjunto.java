package co.edu.itp.svu.domain;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A ArchivoAdjunto.
 */
@Document(collection = "archivo_adjunto")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ArchivoAdjunto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("nombre")
    private String nombre;

    @NotNull
    @Field("tipo")
    private String tipo;

    @Field("url_archivo")
    private String urlArchivo;

    @NotNull
    @Field("fecha_subida")
    private Instant fechaSubida;

    public String getId() {
        return this.id;
    }

    public ArchivoAdjunto id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public ArchivoAdjunto nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return this.tipo;
    }

    public ArchivoAdjunto tipo(String tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUrlArchivo() {
        return this.urlArchivo;
    }

    public ArchivoAdjunto urlArchivo(String urlArchivo) {
        this.setUrlArchivo(urlArchivo);
        return this;
    }

    public void setUrlArchivo(String urlArchivo) {
        this.urlArchivo = urlArchivo;
    }

    public Instant getFechaSubida() {
        return this.fechaSubida;
    }

    public ArchivoAdjunto fechaSubida(Instant fechaSubida) {
        this.setFechaSubida(fechaSubida);
        return this;
    }

    public void setFechaSubida(Instant fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArchivoAdjunto)) {
            return false;
        }
        return getId() != null && getId().equals(((ArchivoAdjunto) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ArchivoAdjunto{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", tipo='" + getTipo() + "'" +
            ", urlArchivo='" + getUrlArchivo() + "'" +
            ", fechaSubida='" + getFechaSubida() + "'" +
            "}";
    }
}
