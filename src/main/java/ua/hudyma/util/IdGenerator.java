package ua.hudyma.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdGenerator {

    private final static List<String> gsmCodesList = List.of("67", "68", "50", "96", "98", "95", "99");
    private final static SecureRandom secureRandom = new SecureRandom();
    private final static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /** YouTube Channel  */
    public static String generateChannelId(
    ) {
        return "UC" + generateAlphaNumericId(22);
    }

    private static String generateAlphaNumericId(int size) {
        return secureRandom.ints(size, 0,
                        chars.length())
                .mapToObj(chars::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static Integer generateCityCode() {
        return generateRandomDigits();
    }

    public static String generateLinkedIdUserCode() {
        return generateRandomDigits(5)
                + generateRandomLowercaseLetters(1)
                + generateRandomDigits(1)
                + generateRandomLowercaseLetters(1)
                + generateRandomDigits(1);
    }

    public static String generateLinkedProfileUrl(String fullName, String userCode) {
        if (fullName == null || fullName.isEmpty()) {
            throw new IllegalArgumentException("Fullname is empty or NULL, cannot generate profile url");
        }
        if (userCode == null || userCode.isEmpty()) {
            throw new IllegalArgumentException("UserCode is empty or NULL, cannot generate profile url");
        }
        var fullnameArray = fullName.split("\\s");
        fullnameArray[1] = fullnameArray.length > 1 ? fullnameArray[1] : "";
        return "https://www.linkedin.com/in/"
                + fullnameArray[0] + "-"
                + fullnameArray[1] + "-"
                + userCode;
    }

    public static Integer generateRandomDigits() {
        return secureRandom.nextInt(100);
    }

    public static LocalDate generateIssuedOn() {
        var today = LocalDate.now();
        int daysBack = new SecureRandom().nextInt(365 * 10);
        return today.minusDays(daysBack);
    }

    public static String generatePhoneNumber() {
        return "+380" + getRandomGSMCode() + generateRandomDigits(7);
    }

    public static String generateTtn() {
        return "2045" + generateRandomDigits(10);
    }

    /** YouTube Video  */
    public static String generateVideoId() {
        return generateAlphaNumericId(11);
    }

    private static String getRandomGSMCode() {
        return gsmCodesList.get(secureRandom.nextInt(gsmCodesList.size()));
    }

    public static String generateProductCode(String catName) {
        return catName
                .substring(0, 2)
                .toUpperCase() +
                generateRandomDigits(10);
    }

    public static LocalTime generateRandomTime() {
        var random = new SecureRandom();
        int secondsInDay = 24 * 60 * 60;
        int randomSecondOfDay = random.nextInt(secondsInDay);
        return LocalTime.ofSecondOfDay(randomSecondOfDay);
    }

    public static String generateId(int letterLength, int numberLength) {
        String letters = generateRandomUppercaseLetters(letterLength);
        String numbers = generateRandomDigits(numberLength);
        return letters + numbers;
    }

    public static String generateRandomUppercaseLetters(int length) {
        return secureRandom
                .ints('A',
                        'Z' + 1)
                .limit(length)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    public static String generateRandomLowercaseLetters(int length) {
        return secureRandom
                .ints('a',
                        'z' + 1)
                .limit(length)
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    public static String generateRandomDigits(int length) {
        return secureRandom
                .ints('0',
                        '9' + 1)
                .limit(length)
                .collect(
                        StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    public static String generateVendorCode() {
        return "VE" + generateRandomDigits(10);
    }

    public static String generateBuyerCode() {
        return "BU" + generateRandomDigits(10);
    }

    public static <T extends Enum<T>> T getRandomEnum(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        int index = secureRandom.nextInt(values.length);
        return values[index];
    }
}