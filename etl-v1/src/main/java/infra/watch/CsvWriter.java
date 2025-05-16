package infra.watch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public interface CsvWriter {
    public ByteArrayOutputStream writeCsv(List<Map<String, Object>> records) throws IOException;
}