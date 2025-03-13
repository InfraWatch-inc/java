package br.com.sptech.school;

import com.github.javafaker.Faker;

import java.util.List;

public class Ordenacao {
    public static void ordernarNome(List<String> list){

        for (int i = 0; i < list.size() - 1; i++) {
            Integer indMenor = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).compareTo(list.get(indMenor)) < 0){
                    indMenor = j;
                }
            }
            String algs = list.get(i);
            list.set(i, list.get(indMenor));
            list.set(indMenor, algs);
        }
    }

    public static void ordernarUso(List<Integer> list){
        Faker faker = new Faker();
        for (int i = 0; i < list.size() - 1; i++) {
            Integer indMenor = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j) < list.get(indMenor)){
                    indMenor = j;
                }
            }
            Integer algs = list.get(i);
            list.set(i, list.get(indMenor));
            list.set(indMenor, algs); // valor de indice Menor se torna o valor de i
        }
    }
}
