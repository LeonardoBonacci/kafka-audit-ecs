package guru.bonacci.kafka.connect.transform.es;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class FileUtils {

	ObjectMapper mapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public Map<String, Object> fileToMap(final String filename) {
		try {
			File fileIn = new File(getClass().getClassLoader().getResource(filename).getFile());
			String valueAsString = Files.asCharSource(fileIn, Charsets.UTF_8).read();
			return mapper.readValue(valueAsString, HashMap.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	 
	}
	
	public String fileToJsonString(final String filename) {
		try {
			// double conversion for input (json) validation
			return mapper.writeValueAsString(fileToJson(filename));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	 
	}

	public JsonNode fileToJson(final String filename) {
		try {
			File fileIn = new File(getClass().getClassLoader().getResource(filename).getFile());
			String asString = Files.asCharSource(fileIn, Charsets.UTF_8).read();
			System.out.println(asString);
			return mapper.readTree(asString);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	 
	}
}
