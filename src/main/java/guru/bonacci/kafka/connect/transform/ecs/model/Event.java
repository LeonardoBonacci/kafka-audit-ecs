package guru.bonacci.kafka.connect.transform.ecs.model;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Map;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://www.elastic.co/guide/en/ecs/current/ecs-event.html
 */
@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

	private String created;
	private String ingested; 
	
	public static Event of(final Map<String, Object> map) {
		Event.EventBuilder event = Event.builder();

		try {
			Object time = PropertyUtils.getProperty(map, "time");
			event.created(String.valueOf(time));
			
			event.ingested(Instant.now().toString());
		} catch (NestedNullException nne) {
			// safely ignore - optional field is empty
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error(e.getMessage());
		}

		return event.build();
	}
}
