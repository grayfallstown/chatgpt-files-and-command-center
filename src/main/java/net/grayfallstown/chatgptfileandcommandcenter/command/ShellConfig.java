package net.grayfallstown.chatgptfileandcommandcenter.command;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("shellconfig")
public class ShellConfig {

    private List<Shell> knownShells;

}
