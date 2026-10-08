package mx.edu.itson.proyectonova.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface KeplerDataService {
    /**
     * Procesa un archivo CSV de la NASA y vuelca los datos en la base de datos SQLite.
     * @param archivo Archivo Multipart subido desde la interfaz web o Postman.
     * @return Mensaje de estado con el recuento de registros procesados.
     */
    String procesarArchivoCsv(MultipartFile archivo);
}