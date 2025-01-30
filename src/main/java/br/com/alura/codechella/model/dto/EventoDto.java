package br.com.alura.codechella.model.dto;

import br.com.alura.codechella.model.Evento;
import br.com.alura.codechella.model.TipoEvento;

import java.time.LocalDate;

public record EventoDto(Long id,
                        TipoEvento tipo,
                        String nome,
                        LocalDate data,
                        String descricao) {

    public static EventoDto toDto(Evento evento) {
        return new EventoDto(evento.getId(), evento.getTipo(), evento.getNome(),
                evento.getData(), evento.getDescricao());
    }

    public Evento toEntity() {
        Evento evento = new Evento();
        evento.setId(this.id);
        evento.setNome(this.nome);
        evento.setTipo(this.tipo);
        evento.setData(this.data);
        evento.setDescricao(this.descricao);
        return evento;
    }

}
