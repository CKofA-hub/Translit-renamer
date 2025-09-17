package io.github.ckofa.translitrenamer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransliterationFileRenamerTest {

    @BeforeAll
    static void initLogPath() {
        LogConfigHelper.initLogFilePath(App.class); // Initialization of the path to the log file, for correct operation of logging
    }

    // The Transliterator class is not mocked in the test because it is a simple, stateless (no state storage) utility with no external dependencies.

    @Mock
    private FileFinder mockFileFinder;

    @InjectMocks
    private TransliterationFileRenamer fileRenamer;

    @Test
    @DisplayName("Successful renaming of files found in the folder path")
    void renameFiles_whenInputFolderPath_shouldRenameFiles() {
        // ---- Preparation ----
        String folderPath = "C:/test_folder";
        String fileName1 = "первый.txt";
        String fileName2 = "второй.txt";
        Path sourcePath1 = Path.of(folderPath,fileName1);
        Path sourcePath2 = Path.of(folderPath,fileName2);

        File mockFile1 = mock(File.class);
        when(mockFile1.getName()).thenReturn(fileName1);
        when(mockFile1.toPath()).thenReturn(sourcePath1);

        File mockFile2 = mock(File.class);
        when(mockFile2.getName()).thenReturn(fileName2);
        when(mockFile2.toPath()).thenReturn(sourcePath2);

        when(mockFileFinder.findFilesOrThrow(folderPath)).thenReturn(List.of(mockFile1, mockFile2));

        //Expected result
        String expectedNewName1 = TransliteratorUtils.transliterate(fileName1);
        String expectedNewName2 = TransliteratorUtils.transliterate(fileName2);
        Path expectedNewPath1 = sourcePath1.resolveSibling(expectedNewName1);
        Path expectedNewPath2 = sourcePath2.resolveSibling(expectedNewName2);

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            // --- Action ---
            fileRenamer.renameFiles(folderPath);

            // --- Check ---
            filesMockedStatic.verify(() -> Files.move(sourcePath1, expectedNewPath1, StandardCopyOption.REPLACE_EXISTING));
            filesMockedStatic.verify(() -> Files.move(sourcePath2, expectedNewPath2, StandardCopyOption.REPLACE_EXISTING));
        }

    }

    @Test
    @DisplayName("Skip a folder if FileFinder throws an exception")
    void renameFiles_whenFileFinderThrowsException_shouldSkipFolder() {
        // ---- Preparation ----
        String folderPath = "non_existent_folder";
        when(mockFileFinder.findFilesOrThrow(folderPath)).thenThrow(new RuntimeException("Directory not found"));

        try(MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {
            // --- Action ---
            fileRenamer.renameFiles(folderPath);

            // --- Check ---
            // Check that the move method has NOT been called once
            filesMockedStatic.verifyNoInteractions();
        }
    }

    @Test
    @DisplayName("Continued operation in case of a single file renaming error")
    void renameFiles_whenMoveFailsForOneFile_shouldContinueWithOthers() {
        // ---- Preparation ----
        String folderPath = "C:/test_folder";
        String fileName1 = "ошибка.txt";
        String fileName2 = "успех_тест.txt";
        Path sourcePath1 = Path.of(folderPath,fileName1);
        Path sourcePath2 = Path.of(folderPath,fileName2);

        File errorFile = mock(File.class);
        when(errorFile.getName()).thenReturn(fileName1);
        when(errorFile.toPath()).thenReturn(sourcePath1);

        File successFile = mock(File.class);
        when(successFile.getName()).thenReturn(fileName2);
        when(successFile.toPath()).thenReturn(sourcePath2);

        // Expected result
        String expectedNewName1 = TransliteratorUtils.transliterate(fileName1);
        String expectedNewName2 = TransliteratorUtils.transliterate(fileName2);
        Path expectedNewPath1 = sourcePath1.resolveSibling(expectedNewName1);
        Path expectedNewPath2 = sourcePath2.resolveSibling(expectedNewName2);

        List<File> files = List.of(errorFile, successFile);

        try (MockedStatic<Files> filesMockedStatic = Mockito.mockStatic(Files.class)) {
            // Configure the Files.move moc to throw an IOException for the first file
            filesMockedStatic.when(() -> Files.move(eq(sourcePath1), any(Path.class), any(StandardCopyOption.class)))
                    .thenThrow(new IOException("Access Denied"));

            // --- Action ---
            fileRenamer.renameFiles(files);

            // --- Check ---
            // Check that an attempt was made to rename the FIRST (unsuccessful) file
            filesMockedStatic.verify(() -> Files.move(sourcePath1, expectedNewPath1, StandardCopyOption.REPLACE_EXISTING));
            // Check that the SECOND (successful) file has also been processed
            filesMockedStatic.verify(() -> Files.move(sourcePath2, expectedNewPath2, StandardCopyOption.REPLACE_EXISTING));
        }
    }

    @Test
    @DisplayName("Do nothing if the file list is empty")
    void renameFiles_withEmptyFileList_shouldDoNothing() {
        // ---- Preparation ----
        List<File> emptyList = Collections.emptyList();

        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            // --- Action ---
            fileRenamer.renameFiles(emptyList);

            // --- Check ---
            // Check that Files.move was not called
            mockedFiles.verifyNoInteractions();
        }
    }


}