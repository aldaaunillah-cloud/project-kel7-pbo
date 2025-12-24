package util;

public class ValidationUtil {

    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
