package ua.hudyma.util;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IdGenerator {

    private final static List<String> gsmCodesList = List.of("67", "68", "50", "96", "98", "95", "99");
    private final static SecureRandom secureRandom = new SecureRandom();
    private final static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private final static String consonants = "bcdfghjklmnpqrstvwxyz";
    private final static String vowels = "aeiou";

    /** YouTube Channel  */
    public static String generateChannelId(
    ) {
        return "UC" + generateAlphaNumericId(22);
    }

    public static int getThreadLocalRandomIndex(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
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

    public static String generateName() {
        var name = new StringBuilder();
        var capitalLetter = (consonants.charAt(secureRandom.nextInt(consonants.length()))+"").toUpperCase();
        name.append(capitalLetter);
        var nameLength = randomiseNameLength();
        nameLength = nameLength < 3 ? randomiseNameLength() : nameLength;
        var lengthModifier = nameLength % 2 == 0 ? 0 : 1;
        for (int i = 0; i < nameLength / 2 + lengthModifier; i++){
            name.append(vowels.charAt(secureRandom.nextInt(vowels.length())));
            name.append(consonants.charAt(secureRandom.nextInt(consonants.length())));
        }
        return name.toString();
    }
    private static int randomiseNameLength() {
        return secureRandom.nextInt(10);
    }

    public static Integer generateRandomStartingExperience() {
        return secureRandom.nextInt(100);
    }

//    Experience is a measurement of hero's capability.
//    Initially, every hired hero starts with a random number of experience
//    in the range of 40 to 90 points. Experience is gained as heroes explore
//    and battle. When they have enough experience points, they gain a level.
//    Gaining a level may have several effects. It does always increase one of the
//    hero's primary skills by one. Hero classes have different probabilities of
//    which primary skill is increased, but the general rule is that might heroes
//    tend to get more attack and defense while magic heroes get more power and knowledge.
//    Typically, when a hero gains a level, he or she is offered a choice of one of two
//    secondary skills. Additionally, many of the hero specialties are affected at level up.
//
//    Typical ways for heroes to gain experience is through combat or from treasure chests.
//    Additionally, Pandora's Boxes, Events and Quests can give experience. The Dungeon town
//    has a special building, the Battle Scholar Academy, which gives 1,000 experience points
//    to every visiting hero. There are also objects on the map which give experience:
//    The Learning Stone gives 1,000 experience points, the Tree of Knowledge allows a hero
//    to level-up and the Altar of Sacrifice allows to sacrifice creatures or artifacts for experience.
//
//    From winning a combat, the hero receives one experience point for each HP of the enemy
//    creatures he managed to vanquish in the battle (clones, war machines and bonus health
//    from artifacts do not count). Resurrected creatures are only counted once, except Demons
//    raised by Pit Lords. If an enemy hero is defeated or an enemy town is captured
//    during a siege, the hero receives 500 extra experience points. A hero will not receive
//    this bonus if the enemy hero escapes or surrenders. If a hero escapes or surrenders,
//    then that hero does not gain any experience, even if he/she managed to kill creatures
//    from the victorious hero. A hero from whom the enemy hero escaped receives experience
//    for the killed enemy creatures, but does not gain experience for the defeated hero.
//
//    Heroes with Learning gain extra experience.
//    Bug: after reaching level 75, a hero gets level 88 at once (13 more level-ups consecutively).
//    From now on, the level-up mechanics shows highly unstable work. The next available levels
//    are level 100, followed by level 108, level 868, level 3732, and level 5920. The final usable
//    level is level 6424. Exceeding this level will result in falling into an infinite
//    loop of level-ups. The process of leveling up from a lower available level to a higher
//    one is continuous.

}