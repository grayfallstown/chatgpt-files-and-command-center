package net.grayfallstown.chatgptfileandcommandcenter.history;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectManager;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{apiKey}/git")
public class GitController {

    @Autowired
    private ProjectManager projectManager;

    @Autowired
    private GitSyncService gitSyncService;

    @GetMapping("/log")
    public ResponseEntity<List<String>> getGitLog(@PathVariable String apiKey) {
        ProjectConfig projectConfig = projectManager.getProjectConfig(apiKey);
        try {
            List<String> logs = gitSyncService.gitlog(projectConfig);
            return ResponseEntity.ok(logs);
        } catch (GitOperationException e) {
            return ResponseEntity.status(500).body(List.of("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage()));
        }
    }
}
