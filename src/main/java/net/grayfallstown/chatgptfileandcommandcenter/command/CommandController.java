package net.grayfallstown.chatgptfileandcommandcenter.command;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;

@RestController
@RequestMapping("/api/{apiKey}/command")
public class CommandController {

    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    private ShellService shellService;

    @Autowired
    private CommandService commandService;

    @Operation(summary = "Execute a shell command in a given shell and with a given timeout", description = "Executes a shell command in the specified project's working directory. The output is streamed live to the client.")
    @PostMapping
    public ResponseEntity<StreamingResponseBody> executeCommand(
            @AuthenticationPrincipal ProjectConfig projectConfig,
            @Valid @RequestBody CommandRequest commandRequest) {
        if (!projectConfig.isExecuteCommands()) {
            throw new CommandsDisabledException("Command Execution disabled in project '" +
                    projectConfig.getDir() + "'");
        }
        String workingDir = projectConfig.getWorkingDir();
        String command = commandRequest.getCommand();
        String shellIdentifier = commandRequest.getShell();

        // because of streamed output, GlobalExceptionHandler cannot undo the
        // HttpStatus.OK used
        if (!shellService.isShellAvailable(shellIdentifier)) {
            String chatGPTHint = "Shell " + shellIdentifier
                    + " not available. Shells available, address them by the listed identifier: " +
                    shellService.getAvailableShellNames() + ". If you think the user wants you " +
                    "to use that shell, tell him to add it to his application.yml and restart ChatGPT "
                    + "File And Command Center";
            StreamingResponseBody stream = out -> {
                out.write(chatGPTHint.getBytes());
            };
            return new ResponseEntity<>(stream, HttpStatus.BAD_REQUEST);
        }

        int timeout = commandRequest.getTimeout();

        StreamingResponseBody stream = out -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(
                    () -> commandService.executeShellCommand(shellIdentifier, command, workingDir, timeout, out));
            try {
                future.get(timeout + 1, TimeUnit.SECONDS); // Add 1 second buffer to ensure process completion handling
            } catch (TimeoutException e) {
                future.cancel(true);
                out.write("ERROR: Command execution timed out\n".getBytes());
                logger.error("Command execution timed out");
            } catch (ExecutionException | InterruptedException e) {
                logger.error("Command execution failed", e);
            } finally {
                executor.shutdown();
            }
        };

        return new ResponseEntity<>(stream, HttpStatus.OK);
    }
}
