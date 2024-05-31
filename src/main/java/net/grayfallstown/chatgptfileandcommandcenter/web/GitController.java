package net.grayfallstown.chatgptfileandcommandcenter.web;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.grayfallstown.chatgptfileandcommandcenter.git.GitSyncService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/git")
public class GitController {

    @Autowired
    private GitSyncService gitSyncService;

    @GetMapping("/log")
    public ResponseEntity<List<String>> getGitLog() {
        try {
            List<String> logs = gitSyncService.gitlog();
            return ResponseEntity.ok(logs);
        } catch (GitAPIException | IOException e) {
            return ResponseEntity.status(500).body(List.of("ERROR: " + e.getClass().getSimpleName() + " " + e.getMessage()));
        }
    }
}
