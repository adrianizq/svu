package co.edu.itp.svu.domain;

import static co.edu.itp.svu.domain.ArchivoAdjuntoTestSamples.*;
import static co.edu.itp.svu.domain.PqrsTestSamples.*;
import static co.edu.itp.svu.domain.RespuestaTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.edu.itp.svu.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ArchivoAdjuntoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ArchivoAdjunto.class);
        ArchivoAdjunto archivoAdjunto1 = getArchivoAdjuntoSample1();
        ArchivoAdjunto archivoAdjunto2 = new ArchivoAdjunto();
        assertThat(archivoAdjunto1).isNotEqualTo(archivoAdjunto2);

        archivoAdjunto2.setId(archivoAdjunto1.getId());
        assertThat(archivoAdjunto1).isEqualTo(archivoAdjunto2);

        archivoAdjunto2 = getArchivoAdjuntoSample2();
        assertThat(archivoAdjunto1).isNotEqualTo(archivoAdjunto2);
    }
}
