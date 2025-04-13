package infra.watch;

public class Captura {

    private String servidor;
    private String dataHora;

    public Captura(String servidor, String dtHora) {
        this.servidor = servidor;
        this.dataHora = dataHora;
    }

    public String getServidor() {
        return servidor;
    }

    public String getDataHora() {
        return dataHora;
    }
}
