package mx.edu.itson.proyectonova.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing).
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Constructor por defecto de la configuración CORS.
     */
    public CorsConfig() {
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a todos los endpoints (incluyendo /api/v1/exploracion)
                .allowedOrigins("*") // En producción, cambiar "*" por "http://tudominio.com"
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600); // Cachea la respuesta de validación por 1 hora
    }
}