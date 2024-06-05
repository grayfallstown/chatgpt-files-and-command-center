package net.grayfallstown.chatgptfileandcommandcenter.command;

import java.nio.file.Path;
import java.util.List;

import lombok.Data;

@Data
public class Shell {

    private String identifier;

    private List<Path> executablePaths;

    private String executionTemplate;

    private String extension;

    public Path getExistingExecutablePath() {
        for (Path path : executablePaths) {
            if (path.toFile().exists()) {
                return path;
            }
        }
        throw new IllegalStateException("shell registred as available, but no executable found");
    }
    
}
