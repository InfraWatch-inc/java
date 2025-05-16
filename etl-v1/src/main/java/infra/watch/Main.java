package infra.watch;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main implements RequestHandler<S3Event, String>, Mapper, CsvWriter{

    // Aqui estou criando um "cliente" para conseguir acessar arquivos que estão no S3
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    // Esse é o nome do bucket para onde vou mandar os CSVs prontos
    private static final String DESTINATION_BUCKET = "infrawatch-prata";

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        // Pego o nome do bucket de origem (de onde veio o JSON)
        String sourceBucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
        // Pego o nome do arquivo (o JSON) que chegou no bucket
        String sourceKey = s3Event.getRecords().get(0).getS3().getObject().getKey();

        try {
            // Abro o arquivo JSON que veio do S3
            InputStream s3InputStream = s3Client.getObject(sourceBucket, sourceKey).getObjectContent();

            // Converto esse JSON para uma lista de mapas (um mapa por linha de dados)
            List<Map<String, Object>> registros = this.map(s3InputStream);

            // Crio um mapa para separar os dados por servidor
            Map<String, List<Map<String, Object>>> registrosPorServidor = new HashMap<>();

            for (Map<String, Object> registro : registros) {
                // Aqui eu tento pegar o valor do campo "servidor" de forma segura
                String servidor;
                if (registro.containsKey("servidor") && registro.get("servidor") != null) {
                    servidor = registro.get("servidor").toString();
                } else {
                    servidor = "desconhecido"; // Se não tiver o campo, coloco como "desconhecido"
                }

                // Adiciono o registro à lista correspondente a esse servidor
                if (!registrosPorServidor.containsKey(servidor)) {
                    registrosPorServidor.put(servidor, new ArrayList<>());
                }
                registrosPorServidor.get(servidor).add(registro);
            }

            // Para cada grupo de registros (um por servidor), gero um CSV separado
            for (Map.Entry<String, List<Map<String, Object>>> entry : registrosPorServidor.entrySet()) {
                String servidor = entry.getKey();
                List<Map<String, Object>> registrosServidor = entry.getValue();

                // Escrevo os registros desse servidor em CSV
                ByteArrayOutputStream csvOutputStream = this.writeCsv(registrosServidor);
                InputStream csvInputStream = new ByteArrayInputStream(csvOutputStream.toByteArray());

                // Crio o nome do arquivo CSV com o nome do servidor e a data/hora
                String nomeCsv = gerarNomeArquivo(sourceKey, servidor);

                // Mando esse arquivo para o bucket de destino
                s3Client.putObject(DESTINATION_BUCKET, nomeCsv, csvInputStream, null);
            }

            return "Sucesso no processamento";
        } catch (Exception e) {
            // Se der erro em qualquer parte, mostro o erro no log do Lambda
            context.getLogger().log("Erro: " + e.getMessage());
            return "Erro no processamento";
        }
    }

    // Função que gera um nome único para o arquivo CSV com data/hora atual
    private String gerarNomeArquivo(String nomeOriginal, String servidor) {
        // Se o nome terminar com ".json", removo essa parte
        String base = nomeOriginal.endsWith(".json") ? nomeOriginal.replace(".json", "") : nomeOriginal;

        // Junto tudo: nome original, nome do servidor
        return base + "-" + servidor + "-" + ".csv";
    }

    @Override
    public List<Map<String, Object>> map(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Biblioteca que mapeia dados JSON para objetos em Java

        // Converte o JSON para uma lista de mapas: cada item é uma linha do CSV
        return mapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
        //readValue para ler o conteúdo da inputStream e transformar num tipo Java
    }

    @Override
    public ByteArrayOutputStream writeCsv(List<Map<String, Object>> records) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        // Coletamos todos os cabeçalhos (chaves) únicos dos objetos
        Set<String> headers = new LinkedHashSet<>();
        for (Map<String, Object> record : records) {
            headers.addAll(record.keySet());
        }

        // Cria o CSVPrinter com os headers descobertos dinamicamente
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(';').withHeader(headers.toArray(new String[0])));

        // Para cada registro, escreve os valores dos headers na mesma ordem
        for (Map<String, Object> record : records) {
            List<String> row = new ArrayList<>();
            for (String header : headers) {
                Object value = record.get(header);
                row.add(value != null ? value.toString() : "");
            }
            csvPrinter.printRecord(row);
        }

        csvPrinter.flush();
        writer.close();

        return outputStream;
    }
}
