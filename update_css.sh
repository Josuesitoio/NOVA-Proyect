cat << 'INNER_EOF' > src/main/resources/static/styles.css
/* ==========================================================================
   Variables Globales de Tema (NASA HUD System)
   ========================================================================== */
:root {
    --nasa-blue: #0b3d91;
    --nasa-red: #fc3d21;
    --hud-cyan: #00f0ff;
    --hud-bg: rgba(10, 14, 23, 0.75); /* Oscuro translúcido para dejar ver el WebGL */
    --hud-border: rgba(0, 240, 255, 0.25);
    --text-main: #e0e6ed;
    --text-muted: #8a9bb2;
    --font-mono: 'Consolas', 'Courier New', 'Menlo', monospace;
}

/* ==========================================================================
   Reseteo Base y Contenedor Principal
   ========================================================================== */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body, html {
    width: 100%;
    height: 100%;
    overflow: hidden;
    background-color: #000000;
    font-family: var(--font-mono);
    color: var(--text-main);
    text-transform: uppercase; /* Aspecto técnico de terminal */
}

/* Lienzo tridimensional de fondo */
#novaCanvas {
    position: absolute;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    z-index: 1;
}

/* ==========================================================================
   Superposición de UI (Overlay)
   ========================================================================== */
.ui-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 10;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 2rem 3rem;
    pointer-events: none;
    overflow-y: auto; /* Fix para móviles: permitir scroll si el contenido es grande */
}

/* Ajustes para móviles */
@media (max-width: 768px) {
    .ui-overlay {
        padding: 1rem;
    }
}

/* Paneles base con estilo Glassmorphism y bordes tecnológicos */
.panel-fluido {
    background: var(--hud-bg);
    border: 1px solid var(--hud-border);
    border-left: 4px solid var(--hud-cyan); /* Acento principal a la izquierda */
    backdrop-filter: blur(10px); /* Efecto cristal sobre las estrellas */
    -webkit-backdrop-filter: blur(10px);
    padding: 1.5rem;
    pointer-events: auto;
    max-width: 420px;
    width: 100%; /* Fix para móviles */
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.8), inset 0 0 20px rgba(0, 240, 255, 0.05);
    margin-bottom: 1rem; /* Fix para móviles: separación entre paneles si se apilan */
}

/* ==========================================================================
   Cabecera (Header)
   ========================================================================== */
header h1 {
    font-size: 2rem;
    color: var(--text-main);
    margin-bottom: 0.3rem;
    letter-spacing: 4px;
    text-shadow: 0 0 15px rgba(255, 255, 255, 0.3);
}

header p {
    color: var(--hud-cyan);
    font-size: 0.85rem;
    letter-spacing: 2px;
}

/* Ajustes para móviles */
@media (max-width: 768px) {
    header h1 {
        font-size: 1.5rem;
        letter-spacing: 2px;
    }
    header p {
        font-size: 0.75rem;
    }
}

/* ==========================================================================
   Controles y Búsqueda (Input / Botón)
   ========================================================================== */
.controls h2, .telemetry-panel h2 {
    font-size: 1rem;
    color: var(--text-muted);
    border-bottom: 1px solid var(--hud-border);
    padding-bottom: 0.5rem;
    margin-bottom: 1.2rem;
    letter-spacing: 2px;
}

.search-box {
    display: flex;
    margin-bottom: 1.5rem;
}

input[type="text"] {
    background: rgba(0, 0, 0, 0.6);
    border: 1px solid var(--hud-border);
    color: var(--hud-cyan);
    padding: 0.8rem 1rem;
    flex-grow: 1;
    font-family: inherit;
    font-size: 0.9rem;
    outline: none;
    letter-spacing: 1px;
    transition: all 0.3s ease;
    width: 100%; /* Fix para móviles */
}

input[type="text"]:focus {
    background: rgba(0, 20, 40, 0.6);
    border-color: var(--hud-cyan);
    box-shadow: 0 0 15px rgba(0, 240, 255, 0.2);
}

input::placeholder {
    color: rgba(0, 240, 255, 0.3);
}

button#searchBtn {
    background: rgba(0, 240, 255, 0.1);
    color: var(--hud-cyan);
    border: 1px solid var(--hud-border);
    border-left: none;
    padding: 0 1.5rem;
    cursor: pointer;
    font-weight: bold;
    font-family: inherit;
    letter-spacing: 1px;
    transition: all 0.3s ease;
}

button#searchBtn:hover {
    background: var(--hud-cyan);
    color: #000000;
    box-shadow: 0 0 20px rgba(0, 240, 255, 0.6);
}

/* Ajustes para móviles */
@media (max-width: 768px) {
    .search-box {
        flex-direction: column;
    }
    button#searchBtn {
        border-left: 1px solid var(--hud-border);
        border-top: none;
        padding: 0.8rem;
    }
}

/* Botones de muestra rápida */
.quick-list p {
    font-size: 0.75rem;
    color: var(--text-muted);
    margin-bottom: 0.5rem;
}

.kep-link {
    background: transparent;
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: var(--text-main);
    font-size: 0.75rem;
    padding: 0.4rem 0.8rem;
    margin-right: 0.5rem;
    margin-bottom: 0.5rem; /* Fix para móviles: separación vertical */
    cursor: pointer;
    font-family: inherit;
    transition: all 0.2s;
    display: inline-block; /* Fix para móviles: evitar que se salgan del contenedor */
}

.kep-link:hover {
    border-color: var(--hud-cyan);
    background: rgba(0, 240, 255, 0.15);
    color: var(--hud-cyan);
}

/* ==========================================================================
   Panel de Telemetría Dinámica
   ========================================================================== */
.telemetry-panel {
    align-self: flex-start;
}

.data-row {
    display: flex;
    justify-content: space-between;
    font-size: 0.85rem;
    padding: 0.6rem 0;
    border-bottom: 1px dashed rgba(255, 255, 255, 0.15);
}

/* Ajustes para móviles */
@media (max-width: 768px) {
    .data-row {
        flex-direction: column;
        align-items: flex-start;
    }
    .data-row .value {
        text-align: left;
        margin-top: 0.2rem;
    }
}

.data-row:last-of-type {
    border-bottom: none;
}

.data-row strong {
    color: var(--text-muted);
    font-weight: normal;
}

.data-row .value {
    color: var(--text-main);
    font-weight: bold;
    text-align: right;
    letter-spacing: 1px;
}

/* Indicador de Red Neuronal */
.ai-flag {
    margin-top: 1.5rem;
    padding: 0.6rem;
    text-align: center;
    font-size: 0.8rem;
    letter-spacing: 2px;
    background: rgba(11, 61, 145, 0.2); /* NASA Blue */
    border: 1px solid var(--nasa-blue);
    color: #ffffff;
    text-shadow: 0 0 8px var(--nasa-blue);
}

/* Mensajes de Estado HTTP */
.status-msg {
    font-size: 0.85rem;
    color: var(--hud-cyan);
    animation: pulse 1.5s infinite;
    padding: 1rem 0;
    text-align: center;
}

.status-msg.error {
    color: var(--nasa-red);
    animation: none;
    text-align: left;
    border-left: 3px solid var(--nasa-red);
    padding: 0.5rem;
    background: rgba(252, 61, 33, 0.1);
}

@keyframes pulse {
    0% { opacity: 0.4; }
    50% { opacity: 1; }
    100% { opacity: 0.4; }
}

/* ==========================================================================
   Footer y Utilidades
   ========================================================================== */
.tecnica-footer {
    font-size: 0.75rem;
    color: rgba(255, 255, 255, 0.3);
    text-align: right;
    letter-spacing: 2px;
    pointer-events: none;
}

/* Ajustes para móviles */
@media (max-width: 768px) {
    .tecnica-footer {
        text-align: center;
        margin-top: 1rem;
    }
}

.hide {
    display: none !important;
}
INNER_EOF
