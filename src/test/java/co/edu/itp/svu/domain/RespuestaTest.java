package co.edu.itp.svu.domain;

import static co.edu.itp.svu.domain.ArchivoAdjuntoTestSamples.*;
import static co.edu.itp.svu.domain.PqrsTestSamples.*;
import static co.edu.itp.svu.domain.RespuestaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.itp.svu.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RespuestaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Respuesta.class);
        Respuesta respuesta1 = getRespuestaSample1();
        Respuesta respuesta2 = new Respuesta();
        assertThat(respuesta1).isNotEqualTo(respuesta2);

        respuesta2.setId(respuesta1.getId());
        assertThat(respuesta1).isEqualTo(respuesta2);

        respuesta2 = getRespuestaSample2();
        assertThat(respuesta1).isNotEqualTo(respuesta2);
    }

    @Test
    void archivosAdjuntosTest() {
        Respuesta respuesta = getRespuestaRandomSampleGenerator();
        ArchivoAdjunto archivoAdjuntoBack = getArchivoAdjuntoRandomSampleGenerator();
    }

    @Test
    void pqrTest() {
        Respuesta respuesta = getRespuestaRandomSampleGenerator();
        Pqrs pqrsBack = getPqrsRandomSampleGenerator();

        respuesta.setPqr(pqrsBack);
        assertThat(respuesta.getPqr()).isEqualTo(pqrsBack);

        respuesta.pqr(null);
        assertThat(respuesta.getPqr()).isNull();
    }
}
