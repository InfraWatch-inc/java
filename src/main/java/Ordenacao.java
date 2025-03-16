import com.github.javafaker.Faker;

import java.util.List;

public class Ordenacao {
    public static void ordernarUsuario(List<Usuario> list){

        for (int i = 0; i < list.size() - 1; i++) {
            Integer indMenor = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).getNome().compareTo(list.get(indMenor).getNome()) < 0){
                    indMenor = j;
                }
            }
            Usuario algs = list.get(i);
            list.set(i, list.get(indMenor));
            list.set(indMenor, algs);
        }
    }

    public static void ordernarUso(List<Maquina> list){
        Faker faker = new Faker();
        for (int i = 0; i < list.size() - 1; i++) {
            Integer indMenor = i;
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(j).getUso() < list.get(indMenor).getUso()){
                    indMenor = j;
                }
            }
            Maquina algs = list.get(i);
            list.set(i, list.get(indMenor));
            list.set(indMenor, algs); // valor de indice Menor se torna o valor de i
        }
    }
    public static int buscaBinaria(List<Maquina> list, int x){
        int inicio = 0;
        int fim = list.size() - 1;
        int meio;

        while (inicio <= fim){
            meio = (inicio + fim) / 2;

            if (x == list.get(meio).getId()){
                return meio;
            } else if (x > list.get(meio).getId()){
                inicio = meio + 1;
            } else if(x < list.get(meio).getId()){
                fim = meio - 1;
            }
        }
        return -1;
    }
}
