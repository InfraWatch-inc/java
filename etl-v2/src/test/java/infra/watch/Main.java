package infra.watch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class Main implements RequestHandler<S3Event, String> {
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private static final String DESTINATION_BUCKET = "infrawatch-ouro";

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        String sourceBucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
        String sourceKey = s3Event.getRecords().get(0).getS3().getObject().getKey();

        List<Dados> dados = new ArrayList<>();

        try {
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(sourceBucket, sourceKey));
            BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));

            // Leitura com cabeçalho
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build()
            );
            Map<String, Integer> headerMap = csvParser.getHeaderMap();
            List<String> headers = new ArrayList<>(headerMap.keySet());

            // Inicializar listas de colunas
            List<List<String>> colunas = new ArrayList<>();
            for(int i = 0; i < headers.size(); i++) {
                colunas.add(new ArrayList<>());
            }

            // Preencher colunas
            for (CSVRecord csvRecord : csvParser) {
                for (int i = 0; i < headers.size(); i++) {
                    colunas.get(i).add(csvRecord.get(i));
                }
            }

            // Construir objetos Dados
            for (int i = 0; i < headers.size(); i++) {
                String nomeColuna = headers.get(i);
                List<String> valores = colunas.get(i);
                boolean isDependente = nomeColuna.toLowerCase().contains("cpu") || nomeColuna.toLowerCase().contains("gpu"); // define a última como dependente
                dados.add(new Dados(valores, nomeColuna, isDependente));
            }

            csvParser.close();
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao ler o arquivo CSV.";
        }

        // Iniciar Modelo Linear
        ModeloLinear modelo = new ModeloLinear(dados, sourceKey);

        // Testar e salvar se o modelo for aprovado
        Boolean hasPassed = modelo.testarModeloLinear();

        if (hasPassed) {
            modelo.salvarDadosModelo(s3Client, DESTINATION_BUCKET);
            return "Modelo Salvo!";
        }

        return "Modelo Não Passou";
    }
}

