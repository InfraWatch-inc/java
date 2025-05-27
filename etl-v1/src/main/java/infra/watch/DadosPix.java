package infra.watch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.s3.AmazonS3;

public class DadosPix extends DadosJson{
    public DadosPix(String nome,InputStream s3InputStream) {
        super(nome, s3InputStream);
    }

    @Override
    public String gerarNomeArquivo(String nomeAdicional) {
        String nome = super.getNome();
        String base = nome.endsWith(".json") ? nome.replace(".json", "") : nome;
        return base + ".csv";
    }

    @Override
    public void processoEtl(String bucket, AmazonS3 s3Client) throws IOException{
        List<Map<String, Object>> registros = super.mapper();
        List<Map<String, Object>> registrosPix = new ArrayList<>();
        List<String> camposObrigatorios = Arrays.asList(
        "PAG_REGIAO", "REC_REGIAO", "PAG_IDADE", "REC_IDADE", "FORMAINICIACAO",
            "NATUREZA", "FINALIDADE", "VALOR", "QUANTIDADE"
        );

        for (Map<String, Object> registro : registros) {
            boolean valido = true;
            for (String campo : camposObrigatorios) {
                Object valor = registro.get(campo);
                if (valor != null && valor.toString().trim().equalsIgnoreCase("Nao se aplica") && valor.toString().trim().isEmpty()) {
                    registrosPix.add(registro);
                }
            }
        }

        ByteArrayOutputStream csvOutputStream = super.writeCsv(registrosPix);
        InputStream csvInputStream = new ByteArrayInputStream(csvOutputStream.toByteArray());
        String nomeCsv = this.gerarNomeArquivo(null);

        s3Client.putObject(bucket, nomeCsv, csvInputStream, null);
    }
}
