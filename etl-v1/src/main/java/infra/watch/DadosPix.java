package infra.watch;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

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
        // Converto esse JSON para uma lista de mapas (um mapa por linha de dados)
        List<Map<String, Object>> registros = this.mapper();
        // TODO
    }
}
