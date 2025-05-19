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

public class Main implements RequestHandler<S3Event, String>{

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

            if(sourceKey.contains("pix")){
                DadosPix dadosPix = new DadosPix(sourceKey, s3InputStream);
                this.processoPix(dadosPix);
            } else if(sourceKey.contains("captura")){
                DadosCaptura dadosCaptura = new DadosCaptura(sourceKey, s3InputStream);
                this.processoCaptura(dadosCaptura);
            } else {
                return "Erro no processamento";
            }

            return "Sucesso no processamento";
        } catch (Exception e) {
            // Se der erro em qualquer parte, mostro o erro no log do Lambda
            context.getLogger().log("Erro: " + e.getMessage());
            return "Erro no processamento";
        }
    }

    public void processoPix(DadosPix dadosPix){
        // Converto esse JSON para uma lista de mapas (um mapa por linha de dados)
        List<Map<String, Object>> registros = dadosPix.mapper();
        // TODO
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
