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
 * https://www.elastic.co/guide/en/ecs/current/ecs-node.html
 */
@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Node {

	private String name;

	public static Node of(final Map<String, Object> map) {
		Node.NodeBuilder node = Node.builder();

		try {
			Object name = PropertyUtils.getProperty(map, "data.resourceName");
			node.name(String.valueOf(name));
		} catch (NestedNullException nne) {
			// safely ignore - optional field is empty
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error(e.getMessage());
		}

		return node.build();
	}
}
