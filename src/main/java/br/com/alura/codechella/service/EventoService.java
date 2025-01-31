package br.com.alura.codechella.service;

import br.com.alura.codechella.common.TraducaoTexto;
import br.com.alura.codechella.model.TipoEvento;
import br.com.alura.codechella.model.dto.EventoDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EventoService {

    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    public Flux<EventoDto> obterTodos() {
        return repository.findAll()
                .map(EventoDto::toDto);
    }

    public Mono<EventoDto> obterPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(EventoDto::toDto);
    }

    public Mono<EventoDto> cadastrar(EventoDto dto) {
        return repository.save(dto.toEntity())
                .map(EventoDto::toDto);
    }

    public Mono<Void> excluir(Long id) {
        return repository.findById(id)
                .flatMap(repository::delete);
    }

    public Flux<EventoDto> obterPorTipo(String tipo) {
        TipoEvento tipoEvento = TipoEvento.valueOf(tipo.toUpperCase());
        return repository.findByTipo(tipoEvento)
                .map(EventoDto::toDto);
    }

    public Mono<String> obterTraducao(Long id, String idioma) {
        return repository.findById(id)
                .flatMap(evento -> TraducaoTexto.obterTraducao(evento.getDescricao(), idioma));
    }

    public Mono<String> obterTraducaoMyMemory(Long id, String idioma) {
        return repository.findById(id)
                .flatMap(evento -> TraducaoTexto.obterTraducaoMyMemory(evento.getDescricao(), idioma));
    }
}
