package io.github.ckofa.translitrenamer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class TransliteratorUtilsTest {

    @Test
    @DisplayName("An empty string on the input gives an empty string on the output.")
    void transliterate_whenEmptyString() {
        String inputText = "";
        assertEquals(inputText, TransliteratorUtils.transliterate(inputText));
    }

    @Test
    @DisplayName("Strings that do not contain cyrillic characters remain unchanged")
    void transliterate_whenOnlyLatin() {
        String inputText = "Hello World 123!@#";
        assertEquals(inputText, TransliteratorUtils.transliterate(inputText));
    }

    @Test
    @DisplayName("Simple cyrillic transliteration")
    void transliterate_whenSimpleCyrillic() {
        assertEquals("Privet Mir", TransliteratorUtils.transliterate("Привет Мир"));
    }

    @Test
    @DisplayName("Tests a string containing both Cyrillic and Latin with numbers.")
    void transliterate_whenMixedContent() {
        assertEquals("Privet World 123!", TransliteratorUtils.transliterate("Привет World 123!"));
    }

    @Test
    @DisplayName("Tests transliteration correctness for upper and lower case strings")
    void transliterate_whenUpperCaseCyrillic() {
        assertEquals("PRIVET mir", TransliteratorUtils.transliterate("ПРИВЕТ мир"));
    }

    @Test
    @DisplayName("Transliteration of a string of numbers and symbols.")
    void transliterate_whenStringWithNumbersAndSymbols() {
        String inputText = "Тест123!@#Строка";
        String expectedText = "Test123!@#Stroka";
        assertEquals(expectedText, TransliteratorUtils.transliterate(inputText));
    }

    @ParameterizedTest
    @CsvSource({
            "Объём, Obem",
            "Мышь, Mysh",
            "Ёлка, Elka",
            "Елка, Elka"
    })
    @DisplayName("Testing various words with special characters")
    void transliterateSpecialSigns(String cyrillic, String expectedLatin) {
        assertEquals(expectedLatin, TransliteratorUtils.transliterate(cyrillic));
    }

    @Test
    @DisplayName("Null input should return an empty string")
    void testTransliterateNullInput() {
        assertEquals("", TransliteratorUtils.transliterate(null));
    }

    @Test
    @DisplayName("The constructor should throw an UnsupportedOperationException")
    void testPrivateConstructor() {
        //Expect InvocationTargetException, since the constructor is called via reflection
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, () -> {
            Constructor<TransliteratorUtils> constructor = TransliteratorUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });

        // Check the cause of the exception (cause)
        Throwable cause = thrown.getCause();
        assertNotNull(cause, "InvocationTargetException must have a cause");
        assertEquals(UnsupportedOperationException.class, cause.getClass());
        assertEquals("Utility class", cause.getMessage());
    }
}