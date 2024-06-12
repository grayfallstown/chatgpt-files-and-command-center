package net.grayfallstown.chatgptfileandcommandcenter.history;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;

@RestController
@RequestMapping("/api/git")
public class GitController {

    @Autowired
    private GitSyncService gitSyncService;

    @GetMapping("/log")
    public ResponseEntity<List<String>> getGitLog(@AuthenticationPrincipal ProjectConfig projectConfig,
            @RequestParam(required = true) String projectID) {
        try {
            List<String> logs = gitSyncService.gitlog(projectConfig);
            return ResponseEntity.ok(logs);
        } catch (GitOperationException e) {
            return ResponseEntity.status(500)
                    .body(List.of("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage()));
        }
    }
}
