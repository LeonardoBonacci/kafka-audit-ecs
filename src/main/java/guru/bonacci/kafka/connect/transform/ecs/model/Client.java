package guru.bonacci.kafka.connect.transform.ecs.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * https://www.elastic.co/guide/en/ecs/current/ecs-service.html
 */
@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

	private String ip;

	public static Client of(final Map<String, Object> map) {
		Client.ClientBuilder client = Client.builder();
		
		try {
			Object address = PropertyUtils.getProperty(map, "data.requestMetadata.client_address");
			client.ip(String.valueOf(address).replace("/", ""));
		} catch (NestedNullException nne) {
			// safely ignore - optional field is empty
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error(e.getMessage());
		}

		return client.build();
	}

}
