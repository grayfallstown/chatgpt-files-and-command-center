package net.grayfallstown.chatgptfileandcommandcenter.git;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import net.grayfallstown.chatgptfileandcommandcenter.history.IgnoreRulesHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IgnoreRulesHandlerTest {

    @Test
    void loadGitIgnore() throws IOException {
        IgnoreRulesHandler ignoreRulesHandler = new IgnoreRulesHandler();

        // Create a temporary directory and .gitignore file for testing
        Path tempDir = Files.createTempDirectory("testWorkingDir");
        Path gitignorePath = tempDir.resolve(".gitignore");
        Files.writeString(gitignorePath, "*.log\n*.tmp");

        List<String> patterns = ignoreRulesHandler.loadGitIgnore(tempDir.toString());

        assertNotNull(patterns);
        assertTrue(patterns.contains("*.log"));
        assertTrue(patterns.contains("*.tmp"));

        // Clean up
        Files.delete(gitignorePath);
        Files.delete(tempDir);
    }
}