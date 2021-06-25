package guru.bonacci.kafka.connect.transform.ecs;

import static guru.bonacci.kafka.connect.transform.ecs.RecordConverter.pojoToMap;
import static guru.bonacci.kafka.connect.transform.ecs.RecordConverter.recordValueAsMap;
import static guru.bonacci.kafka.connect.transform.ecs.RecordConverter.recordValueAsString;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.transforms.Transformation;

import guru.bonacci.kafka.connect.transform.ecs.model.ECSEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ToECS<R extends ConnectRecord<SinkRecord>> implements Transformation<SinkRecord> {

	public static final String OVERVIEW_DOC = "Wraps payload in ElasticSearch Common Schema";
	public static final ConfigDef CONFIG_DEF = new ConfigDef();

	@Override
	public void configure(Map<String, ?> props) {
	}

	@Override
	public final SinkRecord apply(SinkRecord record) {
		if (record.value() == null) {
			log.debug("Tombstone key {}", record.key());
			return record;
		}

		if (record.valueSchema() != null && !Schema.STRING_SCHEMA.equals(record.valueSchema())) {
			log.warn("No support for schema's, ignored message {}:{}", record.key(), record.value());
			return record;
		}

		final String recordValueAsString = recordValueAsString(record);
		log.info("Incoming {}:{}", record.key(), recordValueAsString);

		final Map<String, Object> recordValueAsMap = recordValueAsMap(record);
		final ECSEvent ecsEvent = ECSEvent.of(recordValueAsMap, recordValueAsString, record.timestamp());

		log.debug("ECS: {}", ecsEvent);
		final Map<String, Object> ecsEventAsMap = pojoToMap(ecsEvent);

		final String newKey = retrieveKeyFromValue(recordValueAsMap);
		log.info("Outgoing {}:{}", newKey, ecsEventAsMap);
		
		return record.newRecord(record.topic(), record.kafkaPartition(), Schema.STRING_SCHEMA, newKey,
				record.valueSchema(), ecsEventAsMap, record.timestamp());
	}

	private String retrieveKeyFromValue(final Map<String, Object> recordValue) {
		try {
			Object id = PropertyUtils.getProperty(recordValue, "id");
			return String.valueOf(id);
		} catch (NestedNullException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			// no need to fail..
			log.error(e.getMessage());
			return UUID.randomUUID().toString();
		}
	}	
	
	@Override
	public final void close() {
		log.info("Closing time..");
	}

	@Override
	public final ConfigDef config() {
		return CONFIG_DEF;
	}
}
