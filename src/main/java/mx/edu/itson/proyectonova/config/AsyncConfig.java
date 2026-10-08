package mx.edu.itson.proyectonova.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuración global para la ejecución asíncrona de tareas pesadas.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Constructor por defecto de la configuración asíncrona.
     */
    public AsyncConfig() {
    }

    /**
     * Configura y provee el ejecutor de hilos para las tareas asíncronas de la aplicación.
     * @return Una instancia de Executor configurada para el procesamiento masivo.
     */
    @Bean(name = "csvTaskExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Hilos base que siempre estarán vivos esperando trabajo
        executor.setCorePoolSize(4);

        // Máximo de hilos permitidos si la carga sube de golpe
        executor.setMaxPoolSize(8);

        // Capacidad de la cola de espera antes de empezar a rechazar tareas
        executor.setQueueCapacity(100);

        // Prefijo para identificar fácilmente los hilos en los logs del servidor
        executor.setThreadNamePrefix("CSV-Procesador-");

        executor.initialize();
        return executor;
    }
}