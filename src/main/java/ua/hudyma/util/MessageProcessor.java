package ua.hudyma.util;

import lombok.extern.log4j.Log4j2;
import ua.hudyma.domain.BaseEntity;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Log4j2
public class MessageProcessor {
    public static <T> String getReturnMessage(T t, String fieldName)
            throws Exception {
        String getterName;
        String className;
        Object fieldValue;
        if (t instanceof BaseEntity && verifyFieldName(fieldName)) {
            getterName = "get" + Character.toUpperCase(fieldName.charAt(0))
                    + fieldName.substring(1);
            var getFieldMethod = t.getClass().getMethod(getterName);
            className = t.getClass().getSimpleName();
            fieldValue = getFieldMethod.invoke(t);
        } else if (t instanceof List<?> && ((List<?>) t).size() > 0) {
            className = ((List<?>) t).get(0).getClass().getSimpleName();
            fieldValue = ((List<?>) t).size();
        } else {
            className = "NA";
            fieldValue = "NA";
        }
        var msg = String.format("%s [%s] додано до бази",
                className,
                fieldValue);
        log.info(msg);
        return msg;
    }

    private static boolean verifyFieldName(String fieldName) {
        return fieldName != null && !fieldName.isEmpty();
    }

    /*public static <E extends Exception> Supplier<E> getExceptionSupplier(
            Class<?> clazz,
            Object field,
            Function<String, E> exceptionFactory) {
        return () -> exceptionFactory
                .apply(clazz.getSimpleName() + " " + field + " NOT found");
    }*/

    public static <E extends Exception> Supplier<E> getExceptionSupplier(
            Class<?> clazz,
            Object field,
            Function<String, E> exceptionFactory,
            boolean resourceShortage) {
        return resourceShortage ?
                () -> exceptionFactory
                        .apply(field.toString()) :
                () -> exceptionFactory
                        .apply(clazz.getSimpleName() + " " + field + " NOT found");
    }

    public static <E extends Exception> Supplier<E> getExceptionSupplier(
            Object field,
            Function<String, E> exceptionFactory) {
        return () -> exceptionFactory
                        .apply(field.toString());
    }

}
