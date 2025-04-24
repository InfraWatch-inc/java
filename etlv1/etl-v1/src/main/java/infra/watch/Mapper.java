package infra.watch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Mapper {

    public List<Map<String, Object>> map(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Biblioteca que mapeia dados JSON para objetos em Java

        // Converte o JSON para uma lista de mapas: cada item é uma linha do CSV
        return mapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
        //readValue para ler o conteúdo da inputStream e transformar num tipo Java
    }
}
