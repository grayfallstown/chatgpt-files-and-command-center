package net.grayfallstown.chatgptfileandcommandcenter.command;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShellService {

    private static final Logger logger = LoggerFactory.getLogger(ShellService.class);

    private Map<String, Shell> availableShellByID = new HashMap<String, Shell>();

    public ShellService(@Autowired ShellConfig shellConfig) {
        for (Shell shell : shellConfig.getKnownShells()) {
            for (Path executablePath : shell.getExecutablePaths()) {
                if (executablePath.toFile().exists()) {
                    availableShellByID.put(shell.getIdentifier(), shell);
                    break;
                }
            }
        }
        logger.info("available shells: {}", getAvailableShellNames());
    }

    public List<String> getAvailableShellNames() {
        return availableShellByID.entrySet().stream().map(entry -> entry.getKey()).collect(Collectors.toList());
    }

    public Collection<Shell> getAvailableShells() {
        return availableShellByID.values();
    }

    public Shell getShell(String identifier) {
        if (!availableShellByID.containsKey(identifier)) {
            throw new ShellNotFoundException("No shell found for identifier " + identifier);
        }
        return availableShellByID.get(identifier);
    }

    public boolean isShellAvailable(String identifier) {
        return availableShellByID.containsKey(identifier);
    }

}
