package net.grayfallstown.chatgptfileandcommandcenter.file;

import lombok.Data;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
public class BatchFileOperationRequest {
    
    @Schema(description = "Short commit message for this entire batch")
    private String commitMessage;
    
    @Schema(description = "Fileoperations to perform, commitMessage in FileOperationRequests not required")
    private List<FileOperationRequest> fileOperations;
}
