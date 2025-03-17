
public class Maquina {
    private Integer id;
    private String nome;
    private Integer usoGPU;
    private Integer usoCPU;
    private Integer usoRAM;
    private Integer usoDisco;

    public Maquina(Integer id, String nome, Integer usoGPU, Integer usoCPU, Integer usoRAM, Integer usoDisco){
        this.id = id;
        this.nome = nome;
        this.usoGPU = usoGPU;
        this.usoCPU = usoCPU;
        this.usoRAM = usoRAM;
        this.usoDisco = usoDisco;
    }
    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Integer getusoGPU() {
        return usoGPU;
    }

    public Integer getusoCPU() {
        return usoCPU;
    }

    public Integer getusoRAM() {
        return usoRAM;
    }

    public Integer getusoDisco() {
        return usoDisco;
    }

    @Override
    public String toString() {
        return "--------------------------------------------\n| Maquina " + id + " | Aplicativo = " + nome + "\n--------------------------------------------\n| Componentes: \n| Uso de GPU: " + usoGPU + "% \n| Uso de CPU: " + usoCPU + "% \n| Uso da Memória RAM: " + usoRAM + "% \n| Uso de Disco: " + usoDisco + "% \n--------------------------------------------";
    }
}