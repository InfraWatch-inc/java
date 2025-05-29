package infra.watch;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class DadosJson {
    private String nome;
    private InputStream s3InputStream;

    public DadosJson(String nome, InputStream s3InputStream) {
        this.nome = nome;
        this.s3InputStream = s3InputStream;
    }

    public abstract String gerarNomeArquivo(String nome);

    public abstract void processoEtl(String bucket, AmazonS3 s3Client) throws IOException;

    public ByteArrayOutputStream writeCsv(List<Map<String, Object>> records) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        // Coletamos todos os cabeçalhos (chaves) únicos dos objetos
        Set<String> headers = new LinkedHashSet<>();
        for (Map<String, Object> record : records) {
            headers.addAll(record.keySet());
        }

        // Cria o CSVPrinter com os headers descobertos dinamicamente
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(';').withHeader(headers.toArray(new String[0])));

        // Para cada registro, escreve os valores dos headers na mesma ordem
        for (Map<String, Object> record : records) {
            List<String> row = new ArrayList<>();
            for (String header : headers) {
                Object value = record.get(header);
                row.add(value != null ? value.toString() : "");
            }
            csvPrinter.printRecord(row);
        }

        csvPrinter.flush();
        writer.close();

        return outputStream;
    }

    public ByteArrayOutputStream writeJson(List<Map<String, Object>> records) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mapper.writeValue(outputStream, records);
        return outputStream;
    }

    public List<Map<String, Object>> mapper() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(this.s3InputStream, new TypeReference<List<Map<String, Object>>>() {
        });
    }

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
