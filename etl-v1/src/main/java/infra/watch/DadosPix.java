package infra.watch;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
        List<Map<String, Object>> registros = super.mapper();
        List<Map<String, Object>> registrosPix = new ArrayList<>();

        for(Map<String, Object> registro : registros){
            // TODO rodar o objeto do pix e ir limpando os campos essenciais que não podem estar vazios e adicionar no registrosPix
        }

        //[
        //{
        //  "AnoMes": 202402,
        //    "PAG_PFPJ": "PF",
        //  "REC_PFPJ": "PJ",
        //"PAG_REGIAO": "SUDESTE",
        //"REC_REGIAO": "SUDESTE",
        //"PAG_IDADE": "mais de 60 anos",
        //"REC_IDADE": "Nao se aplica",
        //"FORMAINICIACAO": "QRDN",
        //"NATUREZA": "P2G",
        //"FINALIDADE": "Pix",
        //"VALOR": 86135312.46,
        //"QUANTIDADE": 129997
        //},
        //]

        // TODO preparar e enviar arquivo CSV com os campos
        ByteArrayOutputStream csvOutputStream = super.writeCsv(registrosPix);
        InputStream csvInputStream = new ByteArrayInputStream(csvOutputStream.toByteArray());
        String nomeCsv = this.gerarNomeArquivo(null);

        s3Client.putObject(bucket, nomeCsv, csvInputStream, null);
    }
}
