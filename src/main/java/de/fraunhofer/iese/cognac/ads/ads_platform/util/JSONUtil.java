package de.fraunhofer.iese.cognac.ads.ads_platform.util;

import java.lang.reflect.Field;
import java.util.Map;

public class JSONUtil {

    public static String generateJsonFormat(Class<?> clazz) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                jsonBuilder.append("    \"")
                        .append(field.getName())
                        .append("\": ");

                if (Map.class.isAssignableFrom(field.getType())) {
                    jsonBuilder.append("{ \"key\": \"value\" },\n");
                } else {
                    jsonBuilder.append("\"")
                            .append(field.getType().getSimpleName())
                            .append("\",\n");
                }
            }
            clazz = clazz.getSuperclass();
        }

        if (jsonBuilder.length() > 2) {
            jsonBuilder.setLength(jsonBuilder.length() - 2);
        }

        jsonBuilder.append("\n}");
        return jsonBuilder.toString();
    }
}
