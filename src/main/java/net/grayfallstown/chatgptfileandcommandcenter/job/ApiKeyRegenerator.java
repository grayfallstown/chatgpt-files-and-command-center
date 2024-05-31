package net.grayfallstown.chatgptfileandcommandcenter.job;

import net.grayfallstown.chatgptfileandcommandcenter.config.ProjectConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyRegenerator implements CommandLineRunner {

    private final ProjectConfig projectConfig;

    @Autowired
    public ApiKeyRegenerator(ProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length > 0 && args[0].equals("regenerate-api-key")) {
            projectConfig.regenerateApiKey();
            System.out.println("API key regenerated and saved to apikey.txt");
        }
    }
}
