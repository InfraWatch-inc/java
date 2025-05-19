package infra.watch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DadosCaptura extends DadosJson{
    public DadosCaptura(String nome, InputStream s3InputStream) {
        super(nome,s3InputStream);
    }

    @Override
    public String gerarNomeArquivo(String servidor) {
        String nome = super.getNome();
        // Se o nome terminar com ".json", removo essa parte
        String base = nome.endsWith(".json") ? nome.replace(".json", "") : nome;

        // Junto tudo: nome original, nome do servidor
        return base + "-" + servidor + "-" + ".csv";
    }

    public void processoCaptura(DadosCaptura dadosCaptura) throws IOException {
        // Converto esse JSON para uma lista de mapas (um mapa por linha de dados)
        List<Map<String, Object>> registros = dadosCaptura.mapper();

        // Crio um mapa para separar os dados por servidor
        Map<String, List<Map<String, Object>>> registrosPorServidor = new HashMap<>();

        for(Map<String, Object> registro : registros) {
            // Aqui eu tento pegar o valor do campo "servidor" de forma segura
            String servidor;
            if(registro.containsKey("servidor") && registro.get("servidor") != null) {
                servidor = registro.get("servidor").toString();
            } else {
                servidor = "desconhecido"; // Se não tiver o campo, coloco como "desconhecido"
            }

            // Adiciono o registro à lista correspondente a esse servidor
            if(!registrosPorServidor.containsKey(servidor)) {
                registrosPorServidor.put(servidor, new ArrayList<>());
            }
            registrosPorServidor.get(servidor).add(registro);
        }

        // Para cada grupo de registros (um por servidor), gero um CSV separado
        for(Map.Entry<String, List<Map<String, Object>>> entry : registrosPorServidor.entrySet()) {
            String servidor = entry.getKey();
            List<Map<String, Object>> registrosServidor = entry.getValue();

            // Escrevo os registros desse servidor em CSV
            ByteArrayOutputStream csvOutputStream = dadosCaptura.writeCsv(registrosServidor);
            InputStream csvInputStream = new ByteArrayInputStream(csvOutputStream.toByteArray());

            // Crio o nome do arquivo CSV com o nome do servidor e a data/hora
            String nomeCsv = dadosCaptura.gerarNomeArquivo(servidor);

            // Mando esse arquivo para o bucket de destino
            s3Client.putObject(DESTINATION_BUCKET, nomeCsv, csvInputStream, null);
        }
    }


}
