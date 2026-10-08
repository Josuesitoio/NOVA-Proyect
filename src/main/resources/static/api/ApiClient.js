const API_BASE_URL = 'http://localhost:9090';
console.log('ApiClient Nova configurado para: ' + API_BASE_URL);

const standardHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
};

const fetchApi = async (endpoint, options = {}) => {
    const url = `${API_BASE_URL}${endpoint}`;
    const config = {
        ...options,
        headers: {
            ...standardHeaders,
            ...options.headers
        }
    };
    try {
        const response = await fetch(url, config);
        const data = await response.json();

        if (!response.ok) {
            let mensajeErrorAmigable = 'Ocurrio un error inesperado al conectar con el servidor Nova.';
            const status = response.status;
            console.error('Error del Backend Java Nova. Estatus: ' + status, data);

            if (status === 404) {
                const parts = endpoint.split('/');
                const kepidSolicitado = parts[parts.length - 1];
                mensajeErrorAmigable = 'El astro solicitado (KEPID: ' + kepidSolicitado + ') no existe en el catalogo local.';
            } else if (status === 500) {
                mensajeErrorAmigable = 'El motor de Exploracion del backend colapso inesperadamente. Verifique logs de Java.';
            } else if (data && data.message) {
                mensajeErrorAmigable = data.message;
            }
            return { error: true, status: status, message: mensajeErrorAmigable };
        }
        return data;
    } catch (error) {
        console.error('Error de Red o Servidor Java Apagado. Detalle:', error.message);
        return {
            error: true,
            status: 'NETWORK_ERROR',
            message: 'Imposible conectar con el servidor local. Asegurese de que Spring Boot este encendido en el puerto 9090.'
        };
    }
};

export const NovaApi = {
    async getTelemetriaExoplaneta(kepid) {
        if (!kepid) throw new Error('ApiClient: Kepid es obligatorio.');
        console.log('ApiClient: Solicitando telemetria para KEPID: ' + kepid);
        return fetchApi(`/api/exploracion/${kepid}`, { method: 'GET' });
    },

    async subirDatasetNasa(archivoCsv) {
        const formData = new FormData();
        formData.append("file", archivoCsv);

        try {
            const response = await fetch(`${API_BASE_URL}/api/data/upload`, {
                method: 'POST',
                body: formData
                // Al usar FormData, omitimos los headers a propósito para que el navegador configure el multipart/boundary
            });
            return await response.json();
        } catch (error) {
            return { error: true, message: "Error al subir el CSV: " + error.message };
        }
    },

    async inferenciaManual(datosVectores) {
        return fetchApi(`/api/exploracion/manual`, {
            method: 'POST',
            body: JSON.stringify(datosVectores)
        });
    }
};