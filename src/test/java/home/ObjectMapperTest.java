package home;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ObjectMapperTest {
    private final Logger logger = LoggerFactory.getLogger(ObjectMapperTest.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void main() throws JsonProcessingException, IOException {
        Map<String, String[]> map = new LinkedHashMap<>();
        map.put("Sep 1 - 10", new String[] {"dull", "null"});
        map.put("Sep 11 - 20", new String[] {"dream", "steam", "cream"});
        String s = objectMapper.writeValueAsString(map);

        TypeReference typeReference = new TypeReference<Map<String, String[]>>() {};
        Map<String, String[]> newMap = objectMapper.readValue(s, typeReference);
        newMap = null;
    }
}
