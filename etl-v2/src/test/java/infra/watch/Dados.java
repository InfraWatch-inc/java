package infra.watch;

import java.util.List;

public class Dados {
    private List<String> informacoes;
    private String chave;
    private Boolean isDependente;

    public Dados(List<String> informacoes, String chave, Boolean isDependente) {
        this.informacoes = informacoes;
        this.chave = chave;
        this.isDependente = isDependente;
    }

    public List<String> getInformacoes() {
        return informacoes;
    }

    public String getChave() {
        return chave;
    }

    public Boolean getDependente() {
        return isDependente;
    }
}
