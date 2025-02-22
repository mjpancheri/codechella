package br.com.alura.codechella.controller;

import br.com.alura.codechella.model.dto.EventoDto;
import br.com.alura.codechella.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService service;
    private final Sinks.Many<EventoDto> eventSink;

    public EventoController(EventoService service) {
        this.service = service;
        this.eventSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @GetMapping(value = "/categoria/{tipo}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EventoDto> obterPorTipo(@PathVariable String tipo) {
        return Flux.merge(service.obterPorTipo(tipo), eventSink.asFlux())
                .delayElements(Duration.ofSeconds(4));
    }

    @GetMapping
    public Flux<EventoDto> obterTodos() {
        return service.obterTodos();
    }

    @GetMapping(value = "/{id}")
    public Mono<EventoDto> obterPorId(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EventoDto> cadastrar(@RequestBody EventoDto dto) {
        return service.cadastrar(dto)
                .doOnSuccess(eventSink::tryEmitNext);
    }

    @PutMapping("/{id}")
    public Mono<EventoDto> alterar(@PathVariable Long id, @RequestBody EventoDto dto){
        return service.alterar(id, dto);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> excluir(@PathVariable Long id) {
        return service.excluir(id);
    }

    @GetMapping("/{id}/traduzir/{idioma}")
    public Mono<String> obterTraducao(@PathVariable Long id, @PathVariable String idioma) {
        return service.obterTraducao(id, idioma);
    }

    @GetMapping("/{id}/traduzirV2/{idioma}")
    public Mono<String> obterTraducaoV2(@PathVariable Long id, @PathVariable String idioma) {
        return service.obterTraducaoMyMemory(id, idioma);
    }
}
