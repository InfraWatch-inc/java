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

            if(sourceKey.contains("pix")) {
                DadosPix dadosPix = new DadosPix(sourceKey, s3InputStream);
                dadosPix.processoEtl();
            } else if(sourceKey.contains("captura")) {
                DadosCaptura dadosCaptura = new DadosCaptura(sourceKey, s3InputStream);
                dadosCaptura.processoEtl();
            } else {
                return "Erro no processamento";
            }

            return "Sucesso no processamento";
        } catch(Exception e) {
            // Se der erro em qualquer parte, mostro o erro no log do Lambda
            context.getLogger().log("Erro: " + e.getMessage());
            return "Erro no processamento";
        }
    }
}
