package net.grayfallstown.chatgptfileandcommandcenter.file;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class FileOperationRequest {

    @Schema(description = "Type of file operation (write, delete, move, read, listFiles)", required = true)
    @NotEmpty(message = "File operation must not be empty")
    private String fileOperation;

    @Schema(description = "Path of the file to operate on, except for move operations")
    private String path;

    @Schema(description = "Content to write to the file (only required for write operation)")
    private String content;

    @Schema(description = "Source path for move operation")
    private String from;

    @Schema(description = "Destination path for move operation")
    private String to;

    @Schema(description = "Short commit message for the operation. Required if this is not a batch request")
    private String commitMessage;

    @Schema(description = "Flag to ignore .gitignore rules", defaultValue = "false")
    private boolean ignoreGitIgnore;

    @Schema(description = "Flag to list files recursively", defaultValue = "false")
    private boolean recursive;
}
