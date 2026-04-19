package org.demo.seniorjavatechchallenge.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para devolver la información de la consola H2
 */
@RestController
@RequestMapping("/h2-console")
@ConditionalOnProperty(name = "spring.h2.console.enabled", havingValue = "true")
public class H2ConsoleController {

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String getH2Console() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>H2 Database Console</title>
                    <style>
                        body { font-family: Arial, sans-serif; margin: 20px; }
                        .container { max-width: 600px; margin: 0 auto; }
                        h1 { color: #333; }
                        .info-box { background: #f0f0f0; padding: 15px; border-radius: 5px; margin: 10px 0; }
                        .connection { background: #e8f5e9; padding: 10px; border-left: 4px solid #4caf50; margin: 10px 0; }
                        pre { background: #f5f5f5; padding: 10px; overflow-x: auto; }
                        a { color: #1976d2; text-decoration: none; }
                        a:hover { text-decoration: underline; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>H2 Database Console</h1>
                        <div class="info-box">
                            <h2>Configuración de la base de datos:</h2>
                            <div class="connection">
                                <strong>URL:</strong> jdbc:h2:mem:seniorjtechchallenge
                            </div>
                            <div class="connection">
                                <strong>Usuario:</strong> sa
                            </div>
                            <div class="connection">
                                <strong>Contraseña:</strong> (vacía)
                            </div>
                        </div>
                        <div class="info-box">
                            <h2>Endpoints disponibles:</h2>
                            <p>
                                <strong>POST /products</strong> - Crear un nuevo producto<br>
                                <strong>POST /products/{id}/prices</strong> - Añadir precio histórico<br>
                                <strong>GET /products/{id}/prices</strong> - Obtener historial de precios<br>
                                <strong>GET /products/{id}/prices?date=YYYY-MM-DD</strong> - Obtener precio para una fecha
                            </p>
                        </div>
                        <div class="info-box">
                            <h2>Nota:</h2>
                            <p>Para una consola web completa, usa herramientas como <strong>DBeaver</strong> o <strong>pgAdmin</strong>.</p>
                        </div>
                    </div>
                </body>
                </html>
                """;
    }

    @GetMapping(value = "/*", produces = MediaType.TEXT_HTML_VALUE)
    public String getH2ConsoleWildcard() {
        return getH2Console();
    }
}

