package infra.watch;

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
    public String gerarNomeArquivo(String nome) {
        // gerar nome
        return null;
    }

    @Override
    public List<Map<String, Object>> mapper(){
        return null;
    }

    @Override
    public ByteArrayOutputStream writeCsv(List<Map<String, Object>> records) throws IOException {return null;}
}
