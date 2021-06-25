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
public class Service {

	private String type;
	private String name;
	private Node node;

	public static Service of(final Map<String, Object> map) {
		Service.ServiceBuilder service = Service.builder();

		try {
			Object name = PropertyUtils.getProperty(map, "data.methodName");
			service.name(String.valueOf(name));

			Object type = PropertyUtils.getProperty(map, "type");
			service.type(String.valueOf(type));

			service.node(Node.of(map));
		} catch (NestedNullException nne) {
			// safely ignore - optional field is empty
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error(e.getMessage());
		}

		return service.build();
	}
}
