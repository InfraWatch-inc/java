package infra.watch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface Mapper {
    public List<Map<String, Object>> map(InputStream inputStream) throws IOException;
}
