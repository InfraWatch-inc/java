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
}
