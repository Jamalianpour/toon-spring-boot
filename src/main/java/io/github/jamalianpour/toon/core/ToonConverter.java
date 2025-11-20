package io.github.jamalianpour.toon.core;

import io.github.jamalianpour.toon.annotation.*;
import io.github.jamalianpour.toon.exception.ToonConversionException;
import io.github.jamalianpour.toon.model.ToonConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core TOON converter implementation.
 * Handles conversion of Java objects to TOON format.
 *
 * @author Mohammad Jamalianpour
 */
@Slf4j
@Component
public class ToonConverter {

    private final ToonConfiguration configuration;

    public ToonConverter(ToonConfiguration configuration) {
        this.configuration = configuration != null ? configuration : ToonConfiguration.defaults();
    }

    public ToonConverter() {
        this(ToonConfiguration.defaults());
    }

    /**
     * Converts an object to TOON format string.
     *
     * @param obj The object to convert
     * @return TOON formatted string
     * @throws ToonConversionException if conversion fails
     */
    public String toToon(Object obj) throws ToonConversionException {
        if (obj == null) {
            return "null";
        }

        try {
            StringBuilder builder = new StringBuilder();
            convertValue(obj, builder, 0, false);
            return builder.toString();
        } catch (Exception e) {
            throw new ToonConversionException("Failed to convert object to TOON format", e);
        }
    }

    /**
     * Converts a value to TOON format.
     * @param skipFieldName If true, doesn't add field name prefix (used for nested objects)
     */
    private void convertValue(Object value, StringBuilder builder, int indentLevel, boolean skipFieldName) throws Exception {
        if (value == null) {
            builder.append("null");
            return;
        }

        Class<?> clazz = value.getClass();

        // Handle primitives and strings
        if (isPrimitive(clazz)) {
            appendPrimitive(value, builder);
        }
        // Handle arrays and collections
        else if (value instanceof Collection) {
            convertCollection((Collection<?>) value, builder, indentLevel);
        }
        else if (clazz.isArray()) {
            convertArray(value, builder, indentLevel);
        }
        // Handle maps
        else if (value instanceof Map) {
            convertMap((Map<?, ?>) value, builder, indentLevel);
        }
        // Handle objects
        else {
            convertObject(value, builder, indentLevel);
        }
    }

    /**
     * Converts a collection to TOON format.
     */
    private void convertCollection(Collection<?> collection, StringBuilder builder, int indentLevel) throws Exception {
        if (collection.isEmpty()) {
            builder.append("[]");
            return;
        }

        List<?> list = new ArrayList<>(collection);

        // Check if all elements are of the same type and are objects
        if (isUniformObjectCollection(list)) {
            convertTabularArray(list, builder, indentLevel);
        } else if (isPrimitiveCollection(list)) {
            convertInlineArray(list, builder);
        } else {
            convertListArray(list, builder, indentLevel);
        }
    }

    /**
     * Converts an array to TOON format.
     */
    private void convertArray(Object array, StringBuilder builder, int indentLevel) throws Exception {
        if (array instanceof Object[]) {
            convertCollection(Arrays.asList((Object[]) array), builder, indentLevel);
        } else {
            // Handle primitive arrays
            convertPrimitiveArray(array, builder);
        }
    }

    /**
     * Converts a uniform object collection to tabular format.
     */
    private void convertTabularArray(List<?> list, StringBuilder builder, int indentLevel) throws Exception {
        if (list.isEmpty()) return;

        Object firstItem = list.get(0);
        Class<?> itemClass = firstItem.getClass();
        List<FieldInfo> fields = getFields(itemClass);

        // Build header: [size]{field1,field2,...}:
        builder.append("[").append(list.size()).append("]{");
        builder.append(fields.stream()
                .map(f -> f.toonName)
                .collect(Collectors.joining(",")));
        builder.append("}:");

        // Add rows
        for (Object item : list) {
            builder.append("\n").append(getIndent(indentLevel + 1));
            List<String> values = new ArrayList<>();
            for (FieldInfo fieldInfo : fields) {
                Object value = fieldInfo.field.get(item);
                values.add(formatFieldValue(value, fieldInfo));
            }
            builder.append(String.join(",", values));
        }
    }

    /**
     * Converts a primitive collection to inline format.
     */
    private void convertInlineArray(List<?> list, StringBuilder builder) {
        builder.append("[").append(list.size()).append("]: ");
        builder.append(list.stream()
                .map(this::formatPrimitive)
                .collect(Collectors.joining(",")));
    }

    /**
     * Converts a mixed collection to list format.
     */
    private void convertListArray(List<?> list, StringBuilder builder, int indentLevel) throws Exception {
        builder.append("[").append(list.size()).append("]:");

        for (Object item : list) {
            builder.append("\n").append(getIndent(indentLevel + 1)).append("- ");
            convertValue(item, builder, indentLevel + 1, false);
        }
    }

    /**
     * Converts an object to TOON format.
     */
    private void convertObject(Object obj, StringBuilder builder, int indentLevel) throws Exception {
        Class<?> clazz = obj.getClass();
        List<FieldInfo> fields = getFields(clazz);

        boolean first = true;
        for (FieldInfo fieldInfo : fields) {
            Object value = fieldInfo.field.get(obj);

            // Skip null values if configured
            if (value == null && !shouldIncludeNulls(clazz)) {
                continue;
            }

            if (!first) {
                builder.append("\n");
                if (indentLevel > 0) {
                    builder.append(getIndent(indentLevel));
                }
            }
            first = false;

            // Add field name
            builder.append(fieldInfo.toonName);

            // Handle different value types
            if (value instanceof Collection || (value != null && value.getClass().isArray())) {
                // Collections and arrays format themselves with [n]: or [n]{...}:
                convertCollection(value instanceof Collection ?
                                (Collection<?>) value :
                                Arrays.asList((Object[]) value),
                        builder, indentLevel);
            } else if (value instanceof Map) {
                // Maps need special handling for nested structure
                Map<?, ?> mapValue = (Map<?, ?>) value;
                if (!mapValue.isEmpty()) {
                    builder.append(":");
                    for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                        builder.append("\n").append(getIndent(indentLevel + 1));
                        builder.append(entry.getKey()).append(": ");
                        convertValue(entry.getValue(), builder, indentLevel + 1, false);
                    }
                } else {
                    builder.append(": {}");
                }
            } else if (!isPrimitive(value == null ? Object.class : value.getClass()) && value != null) {
                // Nested objects
                builder.append(":");
                String[] lines = toToon(value).split("\n");
                for (String line : lines) {
                    builder.append("\n").append(getIndent(indentLevel + 1)).append(line);
                }
            } else {
                // Primitive values
                builder.append(": ");
                if (value == null) {
                    builder.append("null");
                } else {
                    builder.append(formatFieldValue(value, fieldInfo));
                }
            }
        }
    }

    /**
     * Converts a map to TOON format.
     */
    private void convertMap(Map<?, ?> map, StringBuilder builder, int indentLevel) throws Exception {
        if (map.isEmpty()) {
            builder.append("{}");
            return;
        }

        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                builder.append("\n");
                if (indentLevel > 0) {
                    builder.append(getIndent(indentLevel));
                }
            }
            first = false;

            String key = entry.getKey().toString();
            builder.append(key);

            Object value = entry.getValue();

            // Handle different value types
            if (value instanceof Collection || (value != null && value.getClass().isArray())) {
                // Collections format themselves
                convertCollection(value instanceof Collection ?
                                (Collection<?>) value :
                                Arrays.asList((Object[]) value),
                        builder, indentLevel);
            } else if (value instanceof Map) {
                // Nested maps
                Map<?, ?> nestedMap = (Map<?, ?>) value;
                if (!nestedMap.isEmpty()) {
                    builder.append(":");
                    for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                        builder.append("\n").append(getIndent(indentLevel + 1));
                        builder.append(nestedEntry.getKey()).append(": ");
                        convertValue(nestedEntry.getValue(), builder, indentLevel + 1, false);
                    }
                } else {
                    builder.append(": {}");
                }
            } else if (!isPrimitive(value == null ? Object.class : value.getClass()) && value != null) {
                // Nested objects
                builder.append(":");
                String[] lines = toToon(value).split("\n");
                for (String line : lines) {
                    builder.append("\n").append(getIndent(indentLevel + 1)).append(line);
                }
            } else {
                // Simple values
                builder.append(": ");
                convertValue(value, builder, indentLevel, false);
            }
        }
    }

    /**
     * Formats a field value based on field configuration.
     */
    private String formatFieldValue(Object value, FieldInfo fieldInfo) {
        if (value == null) {
            return "null";
        }

        // Apply format if specified
        if (!fieldInfo.format.isEmpty() && value instanceof Date) {
            SimpleDateFormat formatter = new SimpleDateFormat(fieldInfo.format);
            return formatter.format((Date) value);
        }

        return formatPrimitive(value);
    }

    /**
     * Formats a primitive value.
     */
    private String formatPrimitive(Object value) {
        if (value == null) return "null";
        if (value instanceof Boolean) return value.toString();
        if (value instanceof Number) return value.toString();

        String str = value.toString();
        // Quote strings that contain special characters or spaces
        if (needsQuoting(str)) {
            return "\"" + escapeString(str) + "\"";
        }
        return str;
    }

    /**
     * Appends a primitive value to the builder.
     */
    private void appendPrimitive(Object value, StringBuilder builder) {
        builder.append(formatPrimitive(value));
    }

    /**
     * Converts a primitive array.
     */
    private void convertPrimitiveArray(Object array, StringBuilder builder) {
        List<String> values = new ArrayList<>();

        if (array instanceof int[]) {
            for (int v : (int[]) array) values.add(String.valueOf(v));
        } else if (array instanceof long[]) {
            for (long v : (long[]) array) values.add(String.valueOf(v));
        } else if (array instanceof double[]) {
            for (double v : (double[]) array) values.add(String.valueOf(v));
        } else if (array instanceof float[]) {
            for (float v : (float[]) array) values.add(String.valueOf(v));
        } else if (array instanceof boolean[]) {
            for (boolean v : (boolean[]) array) values.add(String.valueOf(v));
        } else if (array instanceof char[]) {
            for (char v : (char[]) array) values.add(String.valueOf(v));
        } else if (array instanceof byte[]) {
            for (byte v : (byte[]) array) values.add(String.valueOf(v));
        } else if (array instanceof short[]) {
            for (short v : (short[]) array) values.add(String.valueOf(v));
        }

        builder.append("[").append(values.size()).append("]: ");
        builder.append(String.join(",", values));
    }

    /**
     * Gets fields from a class considering annotations.
     */
    private List<FieldInfo> getFields(Class<?> clazz) throws IllegalAccessException {
        List<FieldInfo> fieldInfos = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            // Skip ignored fields
            if (field.isAnnotationPresent(ToonIgnore.class)) {
                continue;
            }

            ToonField toonField = field.getAnnotation(ToonField.class);
            if (toonField != null && !toonField.include()) {
                continue;
            }

            FieldInfo info = new FieldInfo();
            info.field = field;
            info.toonName = toonField != null && !toonField.value().isEmpty()
                    ? toonField.value()
                    : field.getName();
            info.order = toonField != null ? toonField.order() : Integer.MAX_VALUE;
            info.format = toonField != null ? toonField.format() : "";

            fieldInfos.add(info);
        }

        // Sort by order
        fieldInfos.sort(Comparator.comparingInt(f -> f.order));

        return fieldInfos;
    }

    /**
     * Checks if a class is a primitive type.
     */
    private boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == String.class ||
                Number.class.isAssignableFrom(clazz) ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                Date.class.isAssignableFrom(clazz) ||
                clazz.isEnum();
    }

    /**
     * Checks if all elements in a collection are primitives.
     */
    private boolean isPrimitiveCollection(List<?> list) {
        if (list.isEmpty()) return true;
        return list.stream().allMatch(item ->
                item == null || isPrimitive(item.getClass()));
    }

    /**
     * Checks if all elements in a collection are uniform objects.
     */
    private boolean isUniformObjectCollection(List<?> list) {
        if (list.isEmpty() || list.size() == 1) return false;

        Class<?> firstClass = list.get(0).getClass();
        if (isPrimitive(firstClass)) return false;

        return list.stream().allMatch(item ->
                item != null && item.getClass() == firstClass);
    }

    /**
     * Checks if nulls should be included for a class.
     */
    private boolean shouldIncludeNulls(Class<?> clazz) {
        ToonSerializable annotation = clazz.getAnnotation(ToonSerializable.class);
        return annotation != null && annotation.includeNulls();
    }

    /**
     * Checks if a string needs quoting.
     */
    private boolean needsQuoting(String str) {
        return str.contains(" ") ||
                str.contains(",") ||
                str.contains(":") ||
                str.contains("[") ||
                str.contains("]") ||
                str.contains("{") ||
                str.contains("}") ||
                str.equals("true") ||
                str.equals("false") ||
                str.equals("null") ||
                str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * Escapes special characters in a string.
     */
    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Gets indentation string for a level.
     */
    private String getIndent(int level) {
        return configuration.getIndent().repeat(level);
    }

    /**
     * Helper class to store field information.
     */
    private static class FieldInfo {
        Field field;
        String toonName;
        int order;
        String format;
    }
}