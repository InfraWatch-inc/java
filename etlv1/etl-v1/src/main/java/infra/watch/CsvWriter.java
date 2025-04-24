package infra.watch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CsvWriter {

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
