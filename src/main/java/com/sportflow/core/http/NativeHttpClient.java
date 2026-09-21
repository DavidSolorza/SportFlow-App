package com.sportflow.core.http;

import com.sportflow.core.errors.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/**
 * Cliente HTTP Nativo Puro (Regla de Oro Inviolable: Cero SDKs comerciales).
 * Encapsula java.net.http.HttpClient para integración directa con endpoints REST
 * de terceros (Google, GitHub, etc.) inyectando encabezados y procesando respuestas crudas.
 */
@Component
public class NativeHttpClient {

    private static final Logger log = LoggerFactory.getLogger(NativeHttpClient.class);
    private final HttpClient httpClient;

    public NativeHttpClient() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public HttpResponse<String> get(String url, Map<String, String> headers) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET();

            if (headers != null) {
                headers.forEach(builder::header);
            }

            HttpRequest request = builder.build();
            log.debug("Enviando petición nativa HTTP GET a: {}", url);
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("Falla de I/O en petición HTTP GET a: {}", url, e);
            throw new BusinessRuleException("HTTP_CLIENT_IO_ERROR", "Error de comunicación de red al conectar con el servicio externo: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Petición HTTP interrumpida a: {}", url, e);
            throw new BusinessRuleException("HTTP_CLIENT_INTERRUPTED", "La solicitud externa fue interrumpida.");
        }
    }

    public HttpResponse<String> post(String url, String jsonBody, Map<String, String> headers) {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody != null ? jsonBody : ""));

            if (headers != null) {
                headers.forEach(builder::header);
            }

            HttpRequest request = builder.build();
            log.debug("Enviando petición nativa HTTP POST a: {}", url);
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("Falla de I/O en petición HTTP POST a: {}", url, e);
            throw new BusinessRuleException("HTTP_CLIENT_IO_ERROR", "Error de comunicación de red al conectar con el servicio externo: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Petición HTTP interrumpida a: {}", url, e);
            throw new BusinessRuleException("HTTP_CLIENT_INTERRUPTED", "La solicitud externa fue interrumpida.");
        }
    }
}
