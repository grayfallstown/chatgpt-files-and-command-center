package net.grayfallstown.chatgptfileandcommandcenter.domain;

import lombok.Data;

import java.util.List;

@Data
public class BatchRequest {
    private String commitMessage;
    private List<BatchFileOperation> fileOperations;
}