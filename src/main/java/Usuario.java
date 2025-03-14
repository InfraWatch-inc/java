
public class Usuario {
    private String nome;
    private String adress;
    private String phone;

    public Usuario(String nome, String adress, String phone) {
        this.nome = nome;
        this.adress = adress;
        this.phone = phone;
    }

    public String getNome() {
        return nome;
    }

    public String getAdress() {
        return adress;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "| Usuario " + " Nome: " + nome + " | Endereço: " + adress + " | Telefone:" + phone + " |";
    }
}

