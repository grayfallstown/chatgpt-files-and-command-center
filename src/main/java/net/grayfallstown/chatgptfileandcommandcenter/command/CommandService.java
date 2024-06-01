package net.grayfallstown.chatgptfileandcommandcenter.command;

import org.apache.commons.exec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service
public class CommandService {

    private static final Logger logger = LoggerFactory.getLogger(CommandService.class);

    public void executeShellCommand(String shell, String command, String workingDir, int timeout, OutputStream out) {
        try {
            // Create a script file in ./logs/commands/ directory
            Path scriptFile = createScriptFile(shell, command);

            // Create a command line to execute the script
            CommandLine cmdLine = new CommandLine(shell);
            if (shell.contains("cmd")) {
                cmdLine.addArgument("/c");
            } else {
                cmdLine.addArgument("-c");
            }
            cmdLine.addArgument(scriptFile.toAbsolutePath().toString(), false);

            // Execute the command
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(new File(workingDir));
            executor.setStreamHandler(new PumpStreamHandler(out));
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout * 1000L);
            executor.setWatchdog(watchdog);

            int exitValue = executor.execute(cmdLine);
            if (exitValue != 0) {
                out.write(("ERROR: Command exited with value " + exitValue + "\n").getBytes());
                logger.error("Command exited with value {}", exitValue);
            }
            out.write("Command execution finished.\n".getBytes());
            logger.info("Command execution finished");

            // Log the script file path
            logger.info("Script file created: {}", scriptFile.toAbsolutePath().toString());
        } catch (IOException e) {
            logger.error("Command execution failed", e);
            try {
                out.write(("ERROR: " + e.getMessage() + "\n").getBytes());
            } catch (IOException ioException) {
                logger.error("Failed to write error message to output stream", ioException);
            }
        }
    }

    private Path createScriptFile(String shell, String command) throws IOException {
        String scriptExtension = shell.contains("cmd") ? ".bat" : ".sh";
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Path logDir = Paths.get("logs", "commands");
        Files.createDirectories(logDir);
        Path scriptFile = logDir.resolve("command_" + timestamp + scriptExtension);
        Files.writeString(scriptFile, command);
        scriptFile.toFile().setExecutable(true);
        return scriptFile;
    }

    public String validateShell(String shell) {
        // Define allowed shells
        Map<String, String> allowedShells = Map.of(
            "bash", "/bin/bash",
            "sh", "/bin/sh",
            "cmd", "cmd.exe",
            "powershell", "powershell.exe"
        );

        // Determine the default shell based on the operating system
        String os = System.getProperty("os.name").toLowerCase();
        String defaultShell = os.contains("win") ? "cmd.exe" : "/bin/bash";

        // Return the validated shell or default shell if not found
        return allowedShells.getOrDefault(shell.toLowerCase(), defaultShell);
    }

    public String sanitizeCommand(String command) {
        // Keep the command as is for the script execution
        return command.replace("`", "");
    }
}
