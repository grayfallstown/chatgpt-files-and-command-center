package net.grayfallstown.chatgptfileandcommandcenter.command;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/{apiKey}/command")
public class CommandController {

    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @Autowired
    private ProjectManager projectManager;

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
            Future<?> future = executor.submit(() -> {
                ProcessBuilder processBuilder = new ProcessBuilder(shell, "-c", command)
                        .directory(new File(workingDir))
                        .redirectErrorStream(true);

                try {
                    Process process = processBuilder.start();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                         PrintWriter writer = new PrintWriter(out)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.println(line);
                            writer.flush();
                            logger.info(line);
                        }
                    }
                    boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);
                    if (!finished) {
                        process.destroy();
                        out.write("ERROR: Command execution timed out\n".getBytes());
                        logger.error("Command execution timed out");
                    }
                    out.write("Command execution finished.\n".getBytes());
                    logger.info("Command execution finished");
                } catch (IOException | InterruptedException e) {
                    logger.error("Command execution failed", e);
                    try {
                        out.write(("ERROR: " + e.getMessage() + "\n").getBytes());
                    } catch (IOException ioException) {
                        logger.error("Failed to write error message to output stream", ioException);
                    }
                }
            });

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
