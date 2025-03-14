
public class Maquina {
    private Integer id;
    private String nome;
    private Integer uso;

    public Maquina(Integer id, String nome, Integer uso){
        this.id = id;
        this.nome = nome;
        this.uso = uso;
    }
    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getUso() {
        return uso;
    }

    @Override
    public String toString() {
        return "| Maquina " + id + " | Aplicativo= " + nome + " | Uso de GPU: " + uso + "% |";
    }
}
