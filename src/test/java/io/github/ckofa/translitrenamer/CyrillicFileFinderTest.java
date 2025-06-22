package io.github.ckofa.translitrenamer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CyrillicFileFinderTest {

    @BeforeAll
    static void initLogPath() {
        LogConfigHelper.initLogFilePath(App.class); //Initialization of the path to the log file, for correct operation of logging
    }

    private final CyrillicFileFinder fileFinder = new CyrillicFileFinder();
    @TempDir
    File tempDir;

    @Test
    @DisplayName("Should find files with Cyrillic in the name and should ignore dir")
    void findFilesOrThrow_shouldFindCyrillicFiles() throws IOException {
        //---- Preparation ----
        // Create files in the temporary directory
        String cyrillicName1 = "документ.txt";
        String cyrillicName2 = "тестовый_файл.docx";
        File cyrillicFile1 = new File(tempDir, cyrillicName1);
        cyrillicFile1.createNewFile();
        File cyrillicFile2 = new File(tempDir, cyrillicName2);
        cyrillicFile2.createNewFile();
        File latinFile = new File(tempDir, "document.pdf");
        latinFile.createNewFile();
        // Create a folder that the method should ignore
        new File(tempDir, "папка").mkdir();

        // --- Action ---
        List<File> foundFiles  = fileFinder.findFilesOrThrow(tempDir.toString());

        // --- Check ---
        assertNotNull(foundFiles);
        assertEquals(2, foundFiles.size(), "Exactly 2 files with Cyrillic characters must be found");
        assertTrue(foundFiles.stream().anyMatch(f -> f.getName().equals(cyrillicName1)));
        assertTrue(foundFiles.stream().anyMatch(f -> f.getName().equals(cyrillicName2)));
    }

    @Test
    @DisplayName("Should return an empty list if no cyrillic files are present")
    void findFilesOrThrow_whenNoCyrillicFiles_shouldReturnEmptyList() throws IOException {
        //---- Preparation ----
        new File(tempDir, "english_only.txt").createNewFile();
        new File(tempDir, "another-file.log").createNewFile();

        // --- Action ---
        List<File> foundFiles  = fileFinder.findFilesOrThrow(tempDir.toString());

        // --- Check ---
        assertNotNull(foundFiles);
        assertTrue(foundFiles.isEmpty(), "The list should be empty since there are no cyrillic files");
    }

    @Test
    @DisplayName("Should return an empty list for an empty directory")
    void findFilesOrThrow_whenDirectoryEmpty_shouldReturnEmptyList() {
        // --- Action ---
        List<File> foundFiles  = fileFinder.findFilesOrThrow(tempDir.toString());

        // --- Check ---
        assertNotNull(foundFiles);
        assertTrue(foundFiles.isEmpty(), "The list must be empty for an empty directory");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for wrong path (file instead of folder)")
    void findFilesOrThrow_whenPathInvalid_shouldThrowException() throws IOException {
        //---- Preparation ----
        String exceptionMsg = "Path is not a valid directory";
        File notDirectory = new File(tempDir, "not_a_directory.txt");
        notDirectory.createNewFile();

        // --- Action and check ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> fileFinder.findFilesOrThrow(notDirectory.toString()));
        assertTrue(exception.getMessage().contains(exceptionMsg));
    }

    @Test
    @DisplayName("Should throw an IllegalArgumentException for a non-existent path")
    void findFilesOrThrow_whenNonExistentPath_shouldThrowException() {
        //---- Preparation ----
        String nonExistentPath = Paths.get(tempDir.getPath(), "non_existent_dir").toString();

        // --- Action and check ---
        assertThrows(IllegalArgumentException.class, () -> {
            fileFinder.findFilesOrThrow(nonExistentPath);
        });
    }

    @Test
    @DisplayName("findFiles should find files correctly in the normal case")
    void findFiles_whenInNormalCase_shouldWorkCorrectly() throws IOException {
        //---- Preparation ----
        String cyrillicName1 = "отчет.xlsx";
        String cyrillicName2 = "тестовый_файл.txt";
        new File(tempDir, cyrillicName1).createNewFile();
        new File(tempDir, cyrillicName2).createNewFile();

        // --- Action ---
        List<File> foundFiles = fileFinder.findFiles(tempDir.toString());

        // --- Check ---
        assertEquals(2, foundFiles.size());
        assertTrue(foundFiles.stream().anyMatch(f -> f.getName().equals(cyrillicName1)));
        assertTrue(foundFiles.stream().anyMatch(f -> f.getName().equals(cyrillicName2)));
    }

    @Test
    @DisplayName("findFiles should return an empty list if the path is incorrect and not throw an exception")
    void findFiles_whenInvalidPath_shouldReturnEmptyList() throws IOException {
        //---- Preparation ----
        File notADirectory = new File(tempDir, "a_file.txt");
        notADirectory.createNewFile();

        // --- Action ---
        List<File> result = fileFinder.findFiles(notADirectory.getAbsolutePath());

        // --- Check ---
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}