package net.grayfallstown.chatgptfileandcommandcenter.web;

import lombok.Data;

@Data
public class FileOperationRequest {
    private String fileOperation;
    private String path;
    private String content; // For write operations
    private String from; // For move operations
    private String to; // For move operations
    private String commitMessage; // Commit message for each operation
    private boolean ignoreGitIgnore; // For ListFiles operation
    private boolean recursive; // For ListFiles operation
}
