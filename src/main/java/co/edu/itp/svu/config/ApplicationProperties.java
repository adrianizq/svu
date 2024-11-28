package co.edu.itp.svu.config;

import co.edu.itp.svu.domain.ArchivoAdjunto;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Ventanilla Unica.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Upload upload = new Upload();
    private final Jasper jasper = new Jasper();

    private String filesPath;

    public String getFilesPath() {
        return this.filesPath;
    }

    public void setFilesPath(String imagePath) {
        this.filesPath = imagePath;
    }

    public Upload getUpload() {
        return this.upload;
    }

    public Jasper getJasper() {
        return this.jasper;
    }

    public static class Jasper {

        private final Source source = new Source();
        private final Image image = new Image();
        private final File file = new File();

        public Source getSource() {
            return this.source;
        }

        public Image getImage() {
            return this.image;
        }

        public File getFile() {
            return this.file;
        }

        public static class Source extends DirPath {}

        public static class Image extends DirPath {}

        public static class File extends DirPath {}
    }

    public static class Upload {

        private final Root root = new Root();

        //private final Files files = new Files();
        private final ArchivoAdjunto adjunto = new ArchivoAdjunto();

        public ArchivoAdjunto getAdjunto() {
            return adjunto;
        }

        /*public AdjuntoRetroalimentacion getAdjuntoRetroalimentacion() {
            return adjuntoRetroalimentacion;
        }*/

        //private final Files  filesRetro= new Files();
        //  private final AdjuntoRetroalimentacion adjuntoRetroalimentacion = new AdjuntoRetroalimentacion();

        private final User user = new User();

        public Root getRoot() {
            return this.root;
        }

        public User getUser() {
            return this.user;
        }

        public static class Root extends DirPath {}

        // public static class Files extends DirPath {}

        public static class ArchivoAdjunto extends DirPath {}

        //public static class AdjuntoRetroalimentacion extends DirPath {}

        public static class User extends DirPath {}
    }

    public static class DirPath {

        private String dir;

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }
}
