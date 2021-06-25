package guru.bonacci.kafka.connect.transform.ecs.model;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * https://www.elastic.co/guide/en/ecs/current/ecs-base.html
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ECSEvent {

	@JsonProperty("@timestamp")
	private String timestamp;
	private User user;
	private Event event;
	private String message;

	public static ECSEvent of(final Map<String, Object> valueAsMap, final String valueAsString, final Long timestamp) {
		return ECSEvent.builder()
					.timestamp(Instant.ofEpochMilli(timestamp).toString())
					.user(User.of(valueAsMap))
					.message(valueAsString)
					.event(Event.of(valueAsMap))
					.build();
	}
}
