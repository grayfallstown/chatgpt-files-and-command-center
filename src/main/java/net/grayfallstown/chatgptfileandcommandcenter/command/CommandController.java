package net.grayfallstown.chatgptfileandcommandcenter.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectManager;

import java.util.concurrent.*;

@RestController
@RequestMapping("/api/{apiKey}/command")
public class CommandController {

    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    private ProjectManager projectManager;

    @Autowired
    private CommandService commandService;

    @Operation(
        summary = "Execute a shell command in a given shell and with a given timeout",
        description = "Executes a shell command in the specified project's working directory. The output is streamed live to the client."
    )
    @PostMapping
    public ResponseEntity<StreamingResponseBody> executeCommand(
        @Parameter(description = "API key for authenticating the request", required = true) @PathVariable String apiKey,
        @Valid @RequestBody CommandRequest commandRequest
    ) {
        ProjectConfig projectConfig = projectManager.getProjectConfig(apiKey);
        String workingDir = projectConfig.getWorkingDir();
        String command = commandRequest.getCommand();
        String shell = commandRequest.getShell();
        int timeout = commandRequest.getTimeout();

        StreamingResponseBody stream = out -> {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(() -> commandService.executeShellCommand(shell, command, workingDir, timeout, out));
            try {
                future.get(timeout + 1, TimeUnit.SECONDS);  // Add 1 second buffer to ensure process completion handling
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
