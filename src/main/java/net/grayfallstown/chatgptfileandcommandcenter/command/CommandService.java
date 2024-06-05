package net.grayfallstown.chatgptfileandcommandcenter.command;

import org.apache.commons.exec.*;
import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class CommandService {

    private static final Logger logger = LoggerFactory.getLogger(CommandService.class);

    @Autowired
    private ShellService shellService;

    public void executeShellCommand(String shellIdentifier, String command, String workingDir, int timeout, OutputStream out) {
        try {
            Shell shell = shellService.getShell(shellIdentifier);
            // Create a script file in ./logs/commands/ directory
            Path scriptFile = createScriptFile(shell, command);
            logger.info("Excetuting command sh: {}, cmd: {}, wd: {}, to: {}, sf: {}",
                shellIdentifier, command, workingDir, timeout, scriptFile);
            CommandLine cmdLine = new CommandLine(shell.getExistingExecutablePath().toString());
            for (String argument: shell.getExecutionTemplate().split(" ")) {
                cmdLine.addArgument(argument.replace("PATH_TO_SCRIPTFILE", scriptFile.toAbsolutePath().toString()));
            }

            // Execute the command
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(new File(workingDir));
            PipedOutputStream pipedOut = new PipedOutputStream();
            PipedInputStream pipedIn = new PipedInputStream(pipedOut);
            executor.setStreamHandler(new PumpStreamHandler(new TeeOutputStream(out, pipedOut)));
            ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout * 1000L);
            executor.setWatchdog(watchdog);

            int exitValue = executor.execute(cmdLine);

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(pipedIn))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info("commandoutput: {}", line);
                    }
                } catch (IOException e) {
                    if (!e.getMessage().contains("Write end dead")) {
                        logger.error("Failed to read command output", e);
                    }
                }
            }).start();

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

    private Path createScriptFile(Shell shell, String command) throws IOException {
        String scriptExtension = shell.getExtension();
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Path logDir = Paths.get("logs", "commands");
        Files.createDirectories(logDir);
        Path scriptFile = logDir.resolve("command_" + timestamp + "." + scriptExtension);
        Files.writeString(scriptFile, command);
        scriptFile.toFile().setExecutable(true);
        return scriptFile;
    }

}
