package guru.bonacci.kafka.connect.transform.ecs;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;

import java.util.Collections;
import java.util.Map;

import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.storage.Converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class RecordConverter {

	private static final Converter JSON_CONVERTER;
	private static final ObjectMapper MAPPER;
	
	static {
		JSON_CONVERTER = new JsonConverter();
		JSON_CONVERTER.configure(singletonMap("schemas.enable", "false"), false);
		
		MAPPER = new ObjectMapper();
	}

	public static Map<String, Object> recordValueAsMap(SinkRecord record) {
		 // Tombstone records don't need to be converted
		if (record.value() == null) {
			return null;
		}

		return jsonStringToMap(recordValueAsString(record));
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> jsonStringToMap(String jsonString) {
		if (jsonString == null) {
			return null;
		}

		try {
			return MAPPER.readValue(jsonString, Map.class);
		} catch (JsonProcessingException e) {
			throw new DataException(format("Problems parsing json %s", jsonString), e);
		}
	}

	public static String recordValueAsString(SinkRecord record) {
		 // Tombstone records don't need to be converted
		if (record.value() == null) {
			return null;
		}

		byte[] rawJsonPayload = JSON_CONVERTER.fromConnectData(record.topic(), 
				record.valueSchema(), 
				record.value());
		return new String(rawJsonPayload, UTF_8);
	}
	
	public static Map<String, Object> pojoToMap(Object pojo) {
		if (pojo == null) {
			return Collections.emptyMap();
		}

		try {
			return MAPPER.convertValue(pojo, new TypeReference<Map<String, Object>>() {});
		} catch (IllegalArgumentException e) {
			throw new DataException(format("Problems processing %s", pojo), e);
		}
	}
}