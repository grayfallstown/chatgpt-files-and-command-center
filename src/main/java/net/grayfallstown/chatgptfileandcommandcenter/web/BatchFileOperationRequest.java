package net.grayfallstown.chatgptfileandcommandcenter.web;

import lombok.Data;
import java.util.List;

@Data
public class BatchFileOperationRequest {
    private String commitMessage;
    private List<FileOperationRequest> fileOperations;
}
