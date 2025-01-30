package br.com.alura.codechella.controller;

import br.com.alura.codechella.model.dto.CompraDto;
import br.com.alura.codechella.model.dto.IngressoDto;
import br.com.alura.codechella.service.IngressoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;

@RestController
@RequestMapping("/ingressos")
public class IngressoController {
    private final IngressoService service;
    private final Sinks.Many<IngressoDto> ingressoSink;

    public IngressoController(IngressoService service) {
        this.service = service;
        this.ingressoSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @GetMapping
    public Flux<IngressoDto> obterTodos() {
        return service.obterTodos();
    }

    @GetMapping("/{id}")
    public Mono<IngressoDto> obterPorId(@PathVariable Long id) {
        return service.obterPorId(id);
    }

    @PostMapping
    public Mono<IngressoDto> cadastrar(@RequestBody IngressoDto dto) {
        return service.cadastrar(dto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> excluir(@PathVariable Long id) {
        return service.excluir(id);

    }

    @PutMapping("/{id}")
    public Mono<IngressoDto> alterar(@PathVariable Long id, @RequestBody IngressoDto dto){
        return service.alterar(id, dto);
    }

    @PostMapping("/compra")
    public Mono<IngressoDto> comprar(@RequestBody CompraDto dto) {
        return service.comprar(dto)
                .doOnSuccess(i -> ingressoSink.tryEmitNext(i));
    }

    @GetMapping(value = "/{id}/disponivel", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<IngressoDto> totalDisponivel(@PathVariable Long id) {
        return Flux.merge(service.obterPorId(id), ingressoSink.asFlux())
                .delayElements(Duration.ofSeconds(4));
    }
}
