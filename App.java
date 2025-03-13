package br.com.sptech.school;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        Faker faker = new Faker();
        List<String> listaNome = new ArrayList<>();
        List<Integer> listaUso = new ArrayList<>();

        listaNome.add(faker.app().name());
        listaNome.add(faker.app().name());
        listaNome.add(faker.app().name());
        listaNome.add(faker.app().name());
        listaNome.add(faker.app().name());

        listaUso.add(faker.number().numberBetween(0,100));
        listaUso.add(faker.number().numberBetween(0,100));
        listaUso.add(faker.number().numberBetween(0,100));
        listaUso.add(faker.number().numberBetween(0,100));
        listaUso.add(faker.number().numberBetween(0,100));

        System.out.println(listaNome);
        System.out.println(listaUso);

        Ordenacao.ordernarUso(listaUso);
        Ordenacao.ordernarNome(listaNome);

        for (Integer number : listaUso){
            System.out.println("Uso: " + number + "%");
        }
        for (String nome : listaNome){
            System.out.println("Aplicativo: " + nome);
        }

    }
}
