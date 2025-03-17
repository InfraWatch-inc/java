import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        List<Maquina> listaMaquina = new ArrayList<>();
        List<Usuario> listaUsuario = new ArrayList<>();

        preencherListaMaquina(listaMaquina, 5);
        preencherListaUsuario(listaUsuario, 5);

        System.out.println("Máquina antes da ordenação:");
        for (Maquina item : listaMaquina) {
            System.out.println(item);
        }

        int indice = Ordenacao.buscaBinaria(listaMaquina, 5);
        System.out.println("\nMáquina encontrada no índice " + indice + ": \n" + listaMaquina.get(indice));

        Ordenacao.ordernarUso(listaMaquina);

        System.out.println("\nMáquina após a ordenação:");
        for (Maquina item : listaMaquina) {
            System.out.println(item);
        }

        System.out.println("\nUsuários antes da ordenação:");
        for (Usuario user : listaUsuario) {
            System.out.println(user);
        }

        Ordenacao.ordernarUsuario(listaUsuario);

        System.out.println("\nUsuários após a ordenação:");
        for (Usuario user : listaUsuario) {
            System.out.println(user);
        }

    }

    public static void preencherListaMaquina(List<Maquina> lista, int qtdInteracao) {
        Faker faker = new Faker();

        for (int i = 0; i < qtdInteracao; i++) {
            Maquina m1 = new Maquina(i + 1, faker.app().name(), faker.number().numberBetween(1, 100), faker.number().numberBetween(1, 100), faker.number().numberBetween(1, 100), faker.number().numberBetween(1, 100));
            lista.add(m1);
        }
    }

    public static void preencherListaUsuario(List<Usuario> lista, int qtdInteracao) {
        Faker faker = new Faker();

        for (int i = 0; i < qtdInteracao; i++) {
            Usuario u1 = new Usuario(faker.name().fullName(), faker.internet().emailAddress(),faker.address().fullAddress(), faker.phoneNumber().cellPhone());
            lista.add(u1);
        }
    }
}
