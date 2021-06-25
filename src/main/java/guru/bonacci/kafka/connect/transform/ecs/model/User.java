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
 * https://www.elastic.co/guide/en/ecs/current/ecs-user.html
 */
@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private String id;
	private String name;

	public static User of(final Map<String, Object> map) {
		User.UserBuilder user = User.builder();

		try {
			Object principal = PropertyUtils.getProperty(map, "data.authenticationInfo.principal");
			user.id(String.valueOf(principal).replace("User:", ""));

			Object identifier = PropertyUtils.getProperty(map, "data.authenticationInfo.metadata.identifier");
			user.name(String.valueOf(identifier));
		} catch (NestedNullException nne) {
			// safely ignore - optional field is empty
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error(e.getMessage());
		}

		return user.build();
	}
}
