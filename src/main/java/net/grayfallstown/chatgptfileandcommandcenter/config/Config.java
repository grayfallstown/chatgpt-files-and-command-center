package net.grayfallstown.chatgptfileandcommandcenter.config;

import lombok.Data;

@Data
public class Config {
    private String apiKey;
    private String workingDir;
    private String systemDescription;

}
