package net.grayfallstown.chatgptfileandcommandcenter.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileOperationResponse {
    private String path;
    private String content; // For read operations
    private String message;
}
