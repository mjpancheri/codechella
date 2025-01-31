package br.com.alura.codechella.common;

public record DadosTraducao(DadosResposta responseData) {

    public String getTexto() {
        return this.responseData.translatedText();
    }
}
