import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        Faker faker = new Faker();
        List<Maquina> listaMaquina = new ArrayList<>();
        List<Usuario> listaUsuario = new ArrayList<>();

        preencherListaMaquina(listaMaquina, 10);
        preencherListaUsuario(listaUsuario, 10);

        for (Maquina item : listaMaquina){
            System.out.println(item);
        }

        System.out.println();
        Ordenacao.ordernarUso(listaMaquina);

        for (Maquina item : listaMaquina){
            System.out.println(item);
        }

        System.out.println();

        for (Usuario user : listaUsuario){
            System.out.println(user);
        }

        System.out.println();
        Ordenacao.ordernarUsuario(listaUsuario);

        for (Usuario user : listaUsuario){
            System.out.println(user);
        }

    }

    public static void preencherListaMaquina(List<Maquina> lista, int qtdInteracao){
        Faker faker = new Faker();

        for (int i = 0; i < qtdInteracao; i++) {
            Maquina m1 = new Maquina(i + 1, faker.app().name(), faker.number().numberBetween(1,100));
            lista.add(m1);
        }
    }

    public static void preencherListaUsuario(List<Usuario> lista, int qtdInteracao){
        Faker faker = new Faker();

        for (int i = 0; i < qtdInteracao; i++) {
            Usuario u1 = new Usuario(faker.name().fullName(), faker.address().fullAddress(), faker.phoneNumber().cellPhone());
            lista.add(u1);
        }
    }
}
