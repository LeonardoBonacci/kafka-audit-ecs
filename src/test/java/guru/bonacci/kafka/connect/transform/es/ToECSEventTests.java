package guru.bonacci.kafka.connect.transform.es;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import guru.bonacci.kafka.connect.transform.ecs.RecordConverter;
import guru.bonacci.kafka.connect.transform.ecs.ToECS;

public class ToECSEventTests {

	private Transformation<SinkRecord> transformer;
	private FileUtils utils = new FileUtils();
	
	@BeforeEach
	public void init() {
		transformer = new ToECS<>();
	}

	@AfterEach
	public void cleanUp() {
		transformer.close();
	}
	
	@Test
	public void transformSchemaLess() {
		happyPath("ecs/authentication.json");
		happyPath("ecs/create-acls.json");
	}

	private void happyPath(String inputValueFilename) {
		Map<String, Object> value = utils.fileToMap(inputValueFilename);
		SinkRecord record = new SinkRecord("topic-name", 0, null, "ignored-key", null, value, 0l, System.currentTimeMillis(), TimestampType.CREATE_TIME);

		SinkRecord transformedRecord = transformer.apply(record);

		Map<String, Object> transformedAsMap = RecordConverter.recordValueAsMap(transformedRecord);
		assertNotNull(transformedAsMap.get("user"));	
		assertNotNull(transformedAsMap.get("event"));	
		assertNotNull(transformedAsMap.get("message"));	
		assertNotNull(transformedAsMap.get("@timestamp"));	
    }

	@Test
	public void shouldIgnoreStruct() {
		final String topic = "some-topic";
		final String id = " foo id ";
		final String other = "something else";
		
		Schema schema = SchemaBuilder.struct().field("id", Schema.STRING_SCHEMA).field("other", Schema.STRING_SCHEMA).build();
		Struct value = new Struct(schema).put("id", id).put("other", other);

		final SinkRecord sinkRecord = new SinkRecord(topic, 1, null, null, schema, value, 0);
		
		ToECS<SinkRecord> transformer = new ToECS<>();
		SinkRecord transformedRecord = transformer.apply(sinkRecord);

		assertEquals(transformedRecord, sinkRecord);
		transformer.close();
	}
	
	@Test
	public void shouldIgnoreTombstone() {
		final SinkRecord sinkRecord = new SinkRecord("some-topic", 1, null, "some-key", null, null, 0);

		ToECS<SinkRecord> transformer = new ToECS<>();
		SinkRecord transformedRecord = transformer.apply(sinkRecord);

		assertEquals(transformedRecord, sinkRecord);
		transformer.close();
	}
}