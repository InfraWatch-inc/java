package infra.watch;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main implements RequestHandler<S3Event, String> {

    // Criação do cliente S3 para acessar os buckets
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

    // Bucket de destino para o CSV gerado
    private static final String DESTINATION_BUCKET = "s3-infrawatch-prata";

    @Override
    public String handleRequest(S3Event s3Event, Context context) {

        // Extraímos o nome do bucket de origem e a chave do arquivo JSON
        String sourceBucket = s3Event.getRecords().get(0).getS3().getBucket().getName();
        String sourceKey = s3Event.getRecords().get(0).getS3().getObject().getKey();

        try {
            // Leitura do arquivo JSON do bucket de origem
            InputStream s3InputStream = s3Client.getObject(sourceBucket, sourceKey).getObjectContent();

            // Conversão do JSON para uma lista de objetos Stock usando o Mapper
            Mapper mapper = new Mapper();
            List<Stock> stocks = mapper.map(s3InputStream);

            // Geração do arquivo CSV a partir da lista de Stock usando o CsvWriter
            CsvWriter csvWriter = new CsvWriter();
            ByteArrayOutputStream csvOutputStream = csvWriter.writeCsv(stocks);

            // Converte o ByteArrayOutputStream para InputStream para enviar ao bucket de destino
            InputStream csvInputStream = new ByteArrayInputStream(csvOutputStream.toByteArray());

            // Envio do CSV para o bucket de destino
            s3Client.putObject(DESTINATION_BUCKET, sourceKey.replace(".json", ".csv"), csvInputStream, null);

            return "Sucesso no processamento";
        } catch (Exception e) {
            // Tratamento de erros e log do contexto em caso de exceção
            context.getLogger().log("Erro: " + e.getMessage());
            return "Erro no processamento";
        }
    }


    private void salvarCapturasAgrupadas(List<Captura> capturas) {
        Map<String, List<Captura>> infoServidores = new HashMap<String, List<Captura>>() {
        };

        for (int i = 0; i < capturas.size(); i++) {
            String servidorAtual = capturas.get(i).getServidor();
            String dataHoraAtual = capturas.get(i).getDataHora();

            List<Captura> listaCapturasAtuais = new ArrayList<>();

            for (int j = i; j >= 0; j--) {
                if (capturas.get(j).getServidor().equals(servidorAtual) &&
                        capturas.get(j).getDataHora().equals(dataHoraAtual)) {
                    listaCapturasAtuais.add(capturas.get(j));
                }
            }

            infoServidores.put(String.format("%s", dataHoraAtual), listaCapturasAtuais);
        }

    }
}