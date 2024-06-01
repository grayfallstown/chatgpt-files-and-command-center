package net.grayfallstown.chatgptfileandcommandcenter.git;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import net.grayfallstown.chatgptfileandcommandcenter.history.FileTracker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class FileTrackerTest {

    @InjectMocks
    private FileTracker fileTracker;

    @Mock
    private Git git;

    @Mock
    private AddCommand addCommand;

    @Mock
    private RmCommand rmCommand;

    private List<Path> tempDirs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tempDirs = new ArrayList<>();

        when(git.add()).thenReturn(addCommand);
        when(git.rm()).thenReturn(rmCommand);

        when(addCommand.addFilepattern(anyString())).thenReturn(addCommand);
        when(rmCommand.addFilepattern(anyString())).thenReturn(rmCommand);
    }

    @AfterEach
    void tearDown() {
        for (Path tempDir : tempDirs) {
            try {
                Files.walk(tempDir)
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void addFilesRecursively() throws GitAPIException, IOException {
        Path tempDir = Files.createTempDirectory("testDirectory");
        tempDirs.add(tempDir);
        File directory = tempDir.toFile();
        Files.createFile(tempDir.resolve("testFile.txt"));
        List<String> ignorePatterns = Arrays.asList("*.log");

        Repository mockRepo = mock(Repository.class);
        when(git.getRepository()).thenReturn(mockRepo);
        when(mockRepo.getWorkTree()).thenReturn(directory);

        fileTracker.addFilesRecursively(git, directory, ignorePatterns);

        verify(addCommand, times(1)).addFilepattern("testFile.txt");

        // Clean up handled by @AfterEach
    }

    @Test
    void untrackFiles() throws GitAPIException {
        List<String> untrackList = Arrays.asList("untrackedFile.txt");

        fileTracker.untrackFiles(git, untrackList);

        verify(rmCommand, times(1)).addFilepattern("untrackedFile.txt");
    }
}
