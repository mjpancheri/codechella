package br.com.alura.codechella.consumer;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalTime;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        consumirSSE();
    }
    public static void consumirSSE() {
        Logger logger = Logger.getAnonymousLogger();

        WebClient client = WebClient.create("http://localhost:8080/test/sse");
        ParameterizedTypeReference<ServerSentEvent<String>> type
                = new ParameterizedTypeReference<ServerSentEvent<String>>() {
        };

        Flux<ServerSentEvent<String>> eventStream = client.get()
                .retrieve()
                .bodyToFlux(type);

        eventStream.subscribe(
                content -> logger.info("Time: " + LocalTime.now() +
                        " - event: name[" + content.event() + "], id [" + content.id() + "], content[" + content.data() + "] "),
                error -> logger.info("Error receiving SSE: " + error),
                () -> logger.info("Completed!!!"));
    }
}
