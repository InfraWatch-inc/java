package infra.watch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DadosCaptura extends DadosJson{
    public DadosCaptura(String nome, InputStream s3InputStream) {
        super(nome,s3InputStream);
    }

    @Override
    public String gerarNomeArquivo(String servidor) {
        String nome = super.getNome();
        // Se o nome terminar com ".json", removo essa parte
        String base = nome.endsWith(".json") ? nome.replace(".json", "") : nome;

        // Junto tudo: nome original, nome do servidor
        return base + "-" + servidor + "-" + ".csv";
    }

    @Override
    public List<Map<String, Object>> mapper() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Biblioteca que mapeia dados JSON para objetos em Java

        // Converte o JSON para uma lista de mapas: cada item é uma linha do CSV
        return mapper.readValue(super.getS3InputStream(), new TypeReference<List<Map<String, Object>>>() {});
        //readValue para ler o conteúdo da inputStream e transformar num tipo Java
    }

    @Override
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
}
