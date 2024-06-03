package net.grayfallstown.chatgptfileandcommandcenter.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

import java.util.List;

@Data
@Configuration
public class FileAPIConfig {

    @Value("${fileapi.recursionDepthLimit:3}")
    private int recursionDepthLimit;

    @Value("${fileapi.responseSizeLimit:8000}") // Size in characters
    private int responseSizeLimit;

    @Value("${fileapi.deepDirectories:}")
    private List<String> deepDirectories;
}
