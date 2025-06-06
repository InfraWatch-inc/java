package application.infrawatch;

public class Usuario {
    private String nome;
    private String adress;
    private String email;
    private String phone;

    public Usuario(String nome,String email, String adress, String phone) {
        this.nome = nome;
        this.email = email;
        this.adress = adress;
        this.phone = phone;
    }

    public String getNome() {
        return nome;
    }

    public String getAdress() {
        return adress;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "------------------------------------------------------------------------------\n| application.infrawatch.Usuario " + " Nome: " + nome + "\n| Email: " + email + "\n| Endereço: " + adress + "\n| Telefone:" + phone + "\n------------------------------------------------------------------------------";
    }
}