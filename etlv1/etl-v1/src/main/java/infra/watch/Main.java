package infra.watch;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main implements RequestHandler<S3Event, String> {

    // Aqui estou criando um "cliente" para conseguir acessar arquivos que estão no S3
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    // Esse é o nome do bucket para onde vou mandar os CSVs prontos
    private static final String DESTINATION_BUCKET = "testebucketprata";

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
            Mapper mapper = new Mapper();
            List<Map<String, Object>> registros = mapper.map(s3InputStream);

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

            // Crio o objeto que vai me ajudar a transformar os dados em CSV
            CsvWriter csvWriter = new CsvWriter();

            // Para cada grupo de registros (um por servidor), gero um CSV separado
            for (Map.Entry<String, List<Map<String, Object>>> entry : registrosPorServidor.entrySet()) {
                String servidor = entry.getKey();
                List<Map<String, Object>> registrosServidor = entry.getValue();

                // Escrevo os registros desse servidor em CSV
                ByteArrayOutputStream csvOutputStream = csvWriter.writeCsv(registrosServidor);
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
}
