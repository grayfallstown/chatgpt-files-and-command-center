package net.grayfallstown.chatgptfileandcommandcenter.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileOperationResponse {

    @Schema(description = "File path")
    private String path;

    @Schema(description = "File content")
    private String content;

    @Schema(description = "Message showing success or error for the operation")
    private String message;
}
