package infra.watch;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main  implements RequestHandler<S3Event, String> {
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private static final String DESTINATION_BUCKET = "infrawatch-ouro";

    @Override
    public String handleRequest(S3Event s3Event, Context context) {
        // Receber dados bucket
        String sourceBucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
        String sourceKey = s3Event.getRecords().get(0).getS3().getObject().getKey();

        // Ler CSV
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(sourceBucket, sourceKey));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()))) {

            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

            for (CSVRecord csvRecord : csvParser) {
                System.out.println("Record Number: " + csvRecord.getRecordNumber());
                for (String field : csvRecord) {
                    System.out.print(field + "\t");
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO Coletar uma lista destes dados do CSV
        List<Dados> dados = new ArrayList<>();

        // Iniciar Modelo Linear
        ModeloLinear modelo = new ModeloLinear(dados, sourceKey);

        // Loop de modelos com variáveis dependentes
        Boolean hasPassed = modelo.testarModeloLinear();

        if(hasPassed){
            modelo.salvarDadosModelo(s3Client,sourceBucket);
            return "Modelo Salvo!";
        }

        return "Modelo Não Passou";
    }
}
