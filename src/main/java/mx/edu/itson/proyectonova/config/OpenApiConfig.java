package mx.edu.itson.proyectonova.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de la documentación interactiva de la API (Swagger UI / OpenAPI 3.0).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Proyecto NOVA API - Motor Astrofísico")
                        .version("1.0.0")
                        .description("Documentación oficial de los endpoints REST para el análisis neuronal de exoplanetas del catálogo Kepler.")
                        .contact(new Contact()
                                .name("Josué (josuesito_io)")
                                .email("cruzrodrigueznoe653@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));

    }
}