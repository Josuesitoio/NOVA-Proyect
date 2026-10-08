package mx.edu.itson.proyectonova.config;

//Administra el entorno de memoria nativa (C++) donde se ejecuta el modelo ONNX
import ai.onnxruntime.OrtEnvironment;
// Representa el modelo neuronal ya cargado en la memoria RAM, listo para recibir y predecir
import ai.onnxruntime.OrtSession;
//inyecta automaticamente un objeto "log" que es un logger
import lombok.extern.slf4j.Slf4j;
//Excepcion Personalizada
import mx.edu.itson.proyectonova.exceptions.OnnxModelLoadException;
//permite inyectar Variables configuradas en application.properties
import org.springframework.beans.factory.annotation.Value;
//Le indica a Spring que el objeto devuelto por el metodo debe de guardarse en
// la memoria como un singleton
import org.springframework.context.annotation.Bean;
//etiqueta la clase para que Spring sepa que aqui hay configueaciones criticas que debe leer al arrancar
import org.springframework.context.annotation.Configuration;
//permite leer archivos fisicos sin importar si la app corre en el IDE o si esta empaquetado en un jar
import org.springframework.core.io.Resource;

/**
 * Clase de configuración responsable de inicializar el motor de Inteligencia Artificial.
 *
 * Propósito arquitectónico:
 * Emplea el patrón Singleton a través del contenedor de inyección de dependencias de Spring Boot
 * para garantizar que el modelo ONNX, el cual es pesado, se cargue en la memoria RAM una única
 * vez durante el arranque del servidor, evitando saturar la memoria en cada petición HTTP.
 */
@Slf4j
@Configuration
public class OnnixConfig {
    //extrae la ruta del archivo .ONNX definifa en el aplication.properties
    @Value("${nova.ai.model-path}")
    private Resource modelResource;

    /**
     * Inicializa el entorno raiz de ONNX Runtime.
     * El parametro destroy=Method = "close"asegura que al apagar el servidor Spring Boot
     * Se libere correctamente la memoria nativa (C++) para evitar memory leaks
     *
     * @return OrtEnvironment Entono de ejecucion de ONNX
     * */

    @Bean(destroyMethod = "close")
    public OrtEnvironment ortEnvironment(){
        log.info("Inicializando entorno de memoria para ONNX Runtime...");
        return OrtEnvironment.getEnvironment();
    }

    /**
     * Carga el archivo fisico del modelo y construye la sesion de inferencia.
     * Este metodo inyecta el entorno creado previamente para compilar el modelo
     *
     * @param env El entorno de ejecucion inicializado en el bean OrtEnvironment
     * @return OrtSession Sesion activa que contiene los pesos neuronales en RAM
     * @throws OnnxModelLoadException si el archivo no existe o esta corructo, aplicando el principio "fail fast"
    */

    @Bean(destroyMethod = "close")
    public OrtSession ortSession (OrtEnvironment env){
        try{
            log.info("Buscando modelo neuronal en la ruta: {}", modelResource.getFilename());

            //Leemos el modelo directamente en un arreglo de bytes
            //Esta tecnica garantiza que funcione tanto en desarrollo local como al compilar en produccion
            byte[] modelBytes = modelResource.getInputStream().readAllBytes();

            //Aplicamos todas las optimizaciones matematicas disponibles de la libreria ONNX
            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            options.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.NO_OPT);

            //compilamos la sesion utilizando los bytes del modelo y las opciones de optimizacion
            OrtSession session = env.createSession(modelBytes, options);
            log.info("Modelo de IA Cardado en Ram Exitosamente y listo para procesar telemetrias");

            return session;

        }
        catch (Exception e){
            log.error("Error Critico: No Se Pudo cargar el modelo ONNX", e);
            //lanzamos una excepcion no comprobada (RuntimeException) para detener el arranque del sistema
            throw new OnnxModelLoadException("Error al cargar el modelo ONNX", e);
        }
    }
}
