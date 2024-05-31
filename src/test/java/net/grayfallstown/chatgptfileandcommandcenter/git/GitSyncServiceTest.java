package net.grayfallstown.chatgptfileandcommandcenter.git;

import net.grayfallstown.chatgptfileandcommandcenter.config.ProjectConfig;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class GitSyncServiceTest {

    @Mock
    private BranchSynchronizer branchSynchronizer;

    @Mock
    private IgnoreRulesHandler ignoreRulesHandler;

    @Mock
    private FileTracker fileTracker;

    @InjectMocks
    private GitSyncService gitSyncService;

    @Mock
    private CommitCommand commitCommand;

    private ProjectConfig projectConfig;
    private List<Path> tempDirs;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        tempDirs = new ArrayList<>();
        // Set up a temporary project and working directory for testing
        Path tempProjectDir = Files.createTempDirectory("testProject");
        Path tempWorkingDir = Files.createTempDirectory("testWorkingDir");
        tempDirs.add(tempProjectDir);
        tempDirs.add(tempWorkingDir);

        // Initialize ProjectConfig with temporary directories
        projectConfig = new ProjectConfig();
        projectConfig.setDir(tempProjectDir.toString());
        projectConfig.setWorkingDir(tempWorkingDir.toString());

        // Manually inject ProjectConfig into GitSyncService
        ReflectionTestUtils.setField(gitSyncService, "projectConfig", projectConfig);
    }

    @AfterEach
    void tearDown() {
        for (Path tempDir : tempDirs) {
            try {
                if (Files.exists(tempDir)) {
                    Files.walk(tempDir)
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void createNewRepository() throws IOException {
        File historyRepoDir = new File(projectConfig.getDir(), "historyRepo");
        if (historyRepoDir.exists()) {
            historyRepoDir.delete();
        }

        gitSyncService.createNewRepository();

        verifyNoMoreInteractions(branchSynchronizer, ignoreRulesHandler, fileTracker, commitCommand);
    }

    @Test
    void addAllAndCommit() throws IOException, GitAPIException {
        String commitMessage = "Initial commit";
        String workingDir = projectConfig.getWorkingDir();

        Git mockGit = mock(Git.class);
        Repository mockRepo = mock(Repository.class);
        when(mockRepo.getWorkTree()).thenReturn(new File(workingDir));
        when(mockGit.getRepository()).thenReturn(mockRepo);
        when(mockGit.commit()).thenReturn(commitCommand);
        when(commitCommand.setMessage(anyString())).thenReturn(commitCommand);
        when(ignoreRulesHandler.loadGitIgnore(workingDir)).thenReturn(new ArrayList<>()); // Mock ignore patterns

        try (MockedStatic<Git> mockedGit = mockStatic(Git.class)) {
            mockedGit.when(() -> Git.open(new File(projectConfig.getDir() + "/historyRepo"))).thenReturn(mockGit);

            gitSyncService.addAllAndCommit(commitMessage);

            verify(ignoreRulesHandler, times(1)).loadGitIgnore(workingDir);
            verify(commitCommand, times(1)).setMessage(commitMessage);
            verifyNoMoreInteractions(ignoreRulesHandler, commitCommand);
        }
    }
}
