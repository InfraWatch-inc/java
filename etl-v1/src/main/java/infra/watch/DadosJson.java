package infra.watch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public abstract class DadosJson {
    private String nome;
    private InputStream s3InputStream;

    public DadosJson(String nome, InputStream s3InputStream) {
        this.nome = nome;
        this.s3InputStream = s3InputStream;
    }

    public abstract String gerarNomeArquivo(String nome);
    public abstract List<Map<String, Object>> mapper() throws IOException;
    public abstract ByteArrayOutputStream writeCsv(List<Map<String, Object>> records) throws IOException;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public InputStream getS3InputStream() {
        return s3InputStream;
    }

    public void setS3InputStream(InputStream s3InputStream) {
        this.s3InputStream = s3InputStream;
    }
}
