package co.edu.itp.svu.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Respuesta.
 */
@Document(collection = "respuesta")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Respuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("contenido")
    private String contenido;

    @NotNull
    @Field("fecha_respuesta")
    private Instant fechaRespuesta;

    @NotNull
    @Field("estado")
    private String estado;

    @DBRef
    @Field("archivosAdjuntos")
    @JsonIgnoreProperties(value = { "pqrs", "respuesta" }, allowSetters = true)
    private Set<ArchivoAdjunto> archivosAdjuntos = new HashSet<>();

    @DBRef
    @Field("pqr")
    @JsonIgnoreProperties(value = { "archivosAdjuntos", "oficinaResponder" }, allowSetters = true)
    private Pqrs pqr;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Respuesta id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContenido() {
        return this.contenido;
    }

    public Respuesta contenido(String contenido) {
        this.setContenido(contenido);
        return this;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Instant getFechaRespuesta() {
        return this.fechaRespuesta;
    }

    public Respuesta fechaRespuesta(Instant fechaRespuesta) {
        this.setFechaRespuesta(fechaRespuesta);
        return this;
    }

    public void setFechaRespuesta(Instant fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public String getEstado() {
        return this.estado;
    }

    public Respuesta estado(String estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Set<ArchivoAdjunto> getArchivosAdjuntos() {
        return this.archivosAdjuntos;
    }

    public void setArchivosAdjuntos(Set<ArchivoAdjunto> archivoAdjuntos) {
        this.archivosAdjuntos = archivoAdjuntos;
    }

    public Respuesta archivosAdjuntos(Set<ArchivoAdjunto> archivoAdjuntos) {
        this.setArchivosAdjuntos(archivoAdjuntos);
        return this;
    }

    public Respuesta addArchivosAdjuntos(ArchivoAdjunto archivoAdjunto) {
        this.archivosAdjuntos.add(archivoAdjunto);
        //archivoAdjunto.setRespuesta(this);
        return this;
    }

    public Respuesta removeArchivosAdjuntos(ArchivoAdjunto archivoAdjunto) {
        this.archivosAdjuntos.remove(archivoAdjunto);
        //archivoAdjunto.setRespuesta(null);
        return this;
    }

    public Pqrs getPqr() {
        return this.pqr;
    }

    public void setPqr(Pqrs pqrs) {
        this.pqr = pqrs;
    }

    public Respuesta pqr(Pqrs pqrs) {
        this.setPqr(pqrs);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Respuesta)) {
            return false;
        }
        return getId() != null && getId().equals(((Respuesta) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Respuesta{" +
            "id=" + getId() +
            ", contenido='" + getContenido() + "'" +
            ", fechaRespuesta='" + getFechaRespuesta() + "'" +
            ", estado='" + getEstado() + "'" +
            "}";
    }
}
