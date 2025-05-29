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

public class DadosProblemas extends DadosJson {

    public DadosProblemas(String nome, InputStream s3InputStream) {
        super(nome, s3InputStream);
    }

    @Override
    public String gerarNomeArquivo(String nomeAdicional) {
        return super.getNome();
    }

    @Override
    public void processoEtl(String bucket, AmazonS3 s3Client) throws IOException {
        List<Map<String, Object>> registros = super.mapper();
        List<Map<String, Object>> registroProblemas = new ArrayList<>();

        for (Map<String, Object> r : registros) {
            Object valor = r.get("qtd_alertas");

            if (valor == null || valor.toString().equalsIgnoreCase("NA")) {
                r.put("qtd_alertas", 0);
            } else if (valor instanceof String) {
                try {
                    int parsed = Integer.parseInt((String) valor);
                    r.put("qtd_alertas", parsed);
                } catch (NumberFormatException e) {
                    r.put("qtd_alertas", 0);
                }
            }

            registroProblemas.add(r);
        }


        ByteArrayOutputStream jsonOutputStream = super.writeJson(registroProblemas);
        InputStream ipt = new ByteArrayInputStream(jsonOutputStream.toByteArray());
        String nomeArq = this.gerarNomeArquivo("problemasAjustados");

        s3Client.putObject(bucket, nomeArq, ipt, null);
    }
}
