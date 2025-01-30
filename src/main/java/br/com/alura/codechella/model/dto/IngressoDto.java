package br.com.alura.codechella.model.dto;

import br.com.alura.codechella.model.Ingresso;
import br.com.alura.codechella.model.TipoIngresso;

public record IngressoDto(Long id,
                          Long eventoId,
                          TipoIngresso tipo,
                          Double valor,
                          int total) {

    public static IngressoDto toDto(Ingresso ingresso) {
        return new IngressoDto(ingresso.getId(), ingresso.getEventoId(),
                ingresso.getTipo(), ingresso.getValor(), ingresso.getTotal());
    }

    public Ingresso toEntity() {
        Ingresso ingresso = new Ingresso();
        ingresso.setId(this.id);
        ingresso.setEventoId(this.eventoId);
        ingresso.setTipo(this.tipo);
        ingresso.setValor(this.valor);
        ingresso.setTotal(this.total);
        return ingresso;
    }
}
