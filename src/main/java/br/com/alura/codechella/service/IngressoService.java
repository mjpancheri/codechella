package br.com.alura.codechella.service;


import br.com.alura.codechella.model.Venda;
import br.com.alura.codechella.model.dto.CompraDto;
import br.com.alura.codechella.model.dto.IngressoDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IngressoService {

    private final IngressoRepository repository;
    private final VendaRepository vendaRepository;

    public IngressoService(IngressoRepository repository, VendaRepository vendaRepository) {
        this.repository = repository;
        this.vendaRepository = vendaRepository;
    }

    public Flux<IngressoDto> obterTodos() {
        return repository.findAll()
                .map(IngressoDto::toDto);
    }

    public Mono<IngressoDto> obterPorId(Long id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(IngressoDto::toDto);
    }

    public Mono<IngressoDto> cadastrar(IngressoDto dto) {
        return repository.save(dto.toEntity())
                .map(IngressoDto::toDto);
    }

    public Mono<Void> excluir(Long id) {
        return repository.findById(id)
                .flatMap(repository::delete);
    }

    public Mono<IngressoDto> alterar(Long id, IngressoDto dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Id do evento não encontrado.")))
                .flatMap(ingresso -> {
                    ingresso.setEventoId(dto.eventoId());
                    ingresso.setTipo(dto.tipo());
                    ingresso.setValor(dto.valor());
                    ingresso.setTotal(dto.total());
                    return repository.save(ingresso);
                })
                .map(IngressoDto::toDto);
    }

    @Transactional
    public Mono<IngressoDto> comprar(CompraDto dto) {
        return repository.findById(dto.ingressoId())
                .flatMap(ingresso -> {
                    Venda venda = new Venda();
                    venda.setIngressoId(ingresso.getId());
                    venda.setTotal(dto.total());
                    return vendaRepository.save(venda).then(Mono.defer(() -> {
                        ingresso.setTotal(ingresso.getTotal() - dto.total());
                        return repository.save(ingresso);
                    }));
                }).map(IngressoDto::toDto);
    }
}
