package net.grayfallstown.chatgptfileandcommandcenter.domain;

import lombok.Data;

@Data
public class MoveFileOperationRequest {
    private String from;
    private String to;
}