package net.grayfallstown.chatgptfileandcommandcenter.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CommandRequest {

    @Schema(description = "Command to be executed inside a shell appropriate for the system." +
        "Can be an entire script, for example a simple python application when python is " +
        "selected as shell", required = true)
    @NotEmpty(message = "Command must not be empty")
    private String command;

    @Schema(description = "Shell to execute the command in. Check sysinfo endpoint for supported shells", required = true)
    @NotEmpty(message = "Shell must not be empty")
    private String shell;

    @Schema(description = "Timeout for the command execution in seconds", defaultValue = "120")
    @Positive(message = "Timeout must be a positive integer")
    private Integer timeout = 120;
}
