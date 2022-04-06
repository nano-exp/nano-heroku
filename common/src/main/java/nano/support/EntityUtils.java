package nano.support;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Entity utils
 */
public abstract class EntityUtils {

    /**
     * @param sql SQL
     * @return slimmed SQL
     */
    public static @NotNull String slim(@NotNull String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }

    /**
     * @param clazz entity class
     * @return column name list
     */
    public static @NotNull List<@NotNull String> getEntityColumnNames(@NotNull Class<?> clazz) {
        return clazz.isRecord() ? getRecordEntityColumnNames(clazz) : getBeanEntityColumnNames(clazz);
    }

    private static @NotNull List<@NotNull String> getRecordEntityColumnNames(@NotNull Class<?> clazz) {
        var mappedConstructor = BeanUtils.getResolvableConstructor(clazz);
        if (mappedConstructor.getParameterCount() == 0) {
            return Collections.emptyList();
        }
        var mappedColumns = new ArrayList<String>();
        var parameterNames = BeanUtils.getParameterNames(mappedConstructor);
        for (var name : parameterNames) {
            mappedColumns.add(underscoreName(name));
        }
        return mappedColumns;
    }

    private static @NotNull List<@NotNull String> getBeanEntityColumnNames(@NotNull Class<?> clazz) {
        var mappedColumns = new ArrayList<String>();
        var pds = BeanUtils.getPropertyDescriptors(clazz);
        for (var pd : pds) {
            if (pd.getWriteMethod() == null || pd.getReadMethod() == null) {
                continue;
            }
            var underscoredName = underscoreName(pd.getName());
            mappedColumns.add(underscoredName);
        }
        return mappedColumns;
    }

    /**
     * Convert property name to underscore name
     *
     * @param name property name
     * @return underscore name
     */
    public static @NotNull String underscoreName(@Nullable String name) {
        if (!StringUtils.hasLength(name)) {
            return "";
        }
        var result = new StringBuilder();
        result.append(lowerCaseName(name.substring(0, 1)));
        for (int i = 1; i < name.length(); i++) {
            var s = name.substring(i, i + 1);
            var slc = lowerCaseName(s);
            if (!s.equals(slc)) {
                result.append("_").append(slc);
            } else {
                result.append(s);
            }
        }
        return result.toString();
    }

    private static @NotNull String lowerCaseName(@NotNull String name) {
        return name.toLowerCase(Locale.US);
    }

    /**
     * Convert underscore name to property name
     *
     * @param name underscore name
     * @return property name
     */
    public static @NotNull String propertyName(@Nullable String name) {
        var result = new StringBuilder();
        var nextIsUpper = false;
        if (name != null && name.length() > 0) {
            if (name.length() > 1 && name.charAt(1) == '_') {
                result.append(Character.toUpperCase(name.charAt(0)));
            } else {
                result.append(Character.toLowerCase(name.charAt(0)));
            }
            for (int i = 1; i < name.length(); i++) {
                var c = name.charAt(i);
                if (c == '_') {
                    nextIsUpper = true;
                } else {
                    if (nextIsUpper) {
                        result.append(Character.toUpperCase(c));
                        nextIsUpper = false;
                    } else {
                        result.append(Character.toLowerCase(c));
                    }
                }
            }
        }
        return result.toString();
    }

}
