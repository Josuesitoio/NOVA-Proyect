package mx.edu.itson.proyectonova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que arranca la aplicación Spring Boot.
 */
@SpringBootApplication
public class ProyectoNovaApplication {

    /**
     * Constructor por defecto de la aplicación.
     */
    public ProyectoNovaApplication() {
    }

    /**
     * Método principal que inicializa el contexto de Spring.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(ProyectoNovaApplication.class, args);
    }

}
