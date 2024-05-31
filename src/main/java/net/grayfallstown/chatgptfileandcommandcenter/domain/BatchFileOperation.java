package net.grayfallstown.chatgptfileandcommandcenter.domain;

import lombok.Data;

@Data
public class BatchFileOperation {
    private String fileOperation;
    private String path;
    private String content;
    private String from;
    private String to;
}
