package net.grayfallstown.chatgptfileandcommandcenter.job;

import net.grayfallstown.chatgptfileandcommandcenter.git.GitSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupJob implements CommandLineRunner {

    private final GitSyncService gitSyncService;

    @Autowired
    public StartupJob(GitSyncService gitSyncService) {
        this.gitSyncService = gitSyncService;
    }

    @Override
    public void run(String... args) throws Exception {
        gitSyncService.initializeRepository();
    }
}
