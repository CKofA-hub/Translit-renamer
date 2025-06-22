package io.github.ckofa.translitrenamer;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for transliteration of strings with cyrillic characters into Latin.
 * <p>
 * Provides a method for converting strings containing cyrillic characters to their Latin equivalents.
 * </p>
 */
public final class Transliterator {

    private static final Map<Character, String> translitMap = new HashMap<>();

    static {
        String[] cyrillic = {
                "А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И", "Й",
                "К", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ф",
                "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь", "Э", "Ю", "Я",
                "а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "й",
                "к", "л", "м", "н", "о", "п", "р", "с", "т", "у", "ф",
                "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю", "я"
        };

        String[] latin = {
                "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y",
                "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F",
                "Kh", "Ts", "Ch", "Sh", "Sch", "", "Y", "", "E", "Yu", "Ya",
                "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y",
                "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f",
                "kh", "ts", "ch", "sh", "sch", "", "y", "", "e", "yu", "ya"
        };

        for(int i = 0; i < cyrillic.length; i++) {
            translitMap.put(cyrillic[i].charAt(0), latin[i]);
        }
    }

    private Transliterator() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Transliterates the specified string, replacing cyrillic characters with the corresponding Latin characters.
     * If the input string is {@code null}, an empty string is returned.
     *
     * @param text the string to be transliterated, may be {@code null}
     * @return transliterated string, or an empty string if the input was {@code null}
     */
    public static String transliterate(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for(char ch : text.toCharArray()) {
            result.append(translitMap.getOrDefault(ch, String.valueOf(ch)));
        }
        return result.toString();
    }

}
