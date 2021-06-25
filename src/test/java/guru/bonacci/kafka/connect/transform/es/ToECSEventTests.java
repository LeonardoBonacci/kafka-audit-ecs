package guru.bonacci.kafka.connect.transform.es;

import java.util.Map;

import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import guru.bonacci.kafka.connect.transform.ecs.ToECS;

public class ToECSEventTests {

	Transformation<SinkRecord> transformer;
	FileUtils utils = new FileUtils();
	
	@BeforeEach
	public void init() {
		transformer = new ToECS<>();
	}

	@AfterEach
	public void cleanUp() {
		transformer.close();
	}
	
	@Test
	public void transformSchemaLess() throws JsonProcessingException {
		happyPath("ecs/authentication.json", "ecs/authentication.json");
	}

	private void happyPath(String inputValueFilename, String outputValueFilename) throws JsonProcessingException {
		Map<String, Object> value = utils.fileToMap(inputValueFilename);
		SinkRecord record = new SinkRecord("topic-name", 0, null, "ignored key", null, value, 0l, System.currentTimeMillis(), TimestampType.CREATE_TIME);

		SinkRecord transformedRecord = transformer.apply(record);
//		Map<String, Object> expected = utils.fileToMap(outputValueFilename);

		System.out.println(transformedRecord.value().toString());
		System.out.println(new ObjectMapper().writeValueAsString(transformedRecord.value()));
		
//		assertTrue(Maps.difference(expected, (Map<String, Object>)transformedRecord.value()).areEqual());	
    }
//
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Test
//	public void transformNestedInSchemaLess() {
//		final String topic = "some-topic";
//		final String id = " foo id ";
//		final String other = "something else";
//
//		final Map<String, Object> nested = ImmutableMap.of("id", id);		
//		final Map<String, Object> value = ImmutableMap.of("other", other, "nest", nested);		
//
//		final SinkRecord sinkRecord = new SinkRecord(topic, 1, null, null, null, value, 0);
//
//		ToECS<SinkRecord> transformer = new ToECS<>();
//		transformer.configure(ImmutableMap.of("field", "nest.id", "topic", topic));
//		SinkRecord transformedRecord = transformer.apply(sinkRecord);
//		transformer.close();
//
//		Map<String, Object> trValues = (Map)transformedRecord.value();
//		Map<String, Object> trNested = (Map)trValues.get("nest");
//		assertThat(trNested.get("id"), is(equalTo("fooid")));
//		assertThat(trValues.get("other"), is(equalTo(value.get("other"))));
//	}
//
//	@Test
//	public void tombstone() {
//		final String topic = "some-topic";
//		final Map<String, Object> key = new HashMap<>();
//		key.put("Id", 1234);
//
//		final Map<String, Object> value = null;
//
//		final SinkRecord sinkRecord = new SinkRecord(topic, 1, null, key, null, value, 0);
//
//		ToECS<SinkRecord> transformer = new ToECS<>();
//		SinkRecord transformedRecord = transformer.apply(sinkRecord);
//		transformer.close();
//
//		assertThat(transformedRecord, is(equalTo(sinkRecord)));
//	}
}