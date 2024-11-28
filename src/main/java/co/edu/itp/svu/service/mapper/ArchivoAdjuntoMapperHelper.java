package co.edu.itp.svu.service.mapper;

import org.mapstruct.Named;

public class ArchivoAdjuntoMapperHelper {

    @Named("byteArrayToString")
    public static String byteArrayToString(byte[] archivo) {
        return archivo != null ? new String(archivo) : null;
    }

    @Named("stringToByteArray")
    public static byte[] stringToByteArray(String file) {
        return file != null ? file.getBytes() : null;
    }
}
