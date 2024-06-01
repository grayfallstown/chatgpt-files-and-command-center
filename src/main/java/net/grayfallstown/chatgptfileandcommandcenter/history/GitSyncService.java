package net.grayfallstown.chatgptfileandcommandcenter.history;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GitSyncService {

    private Git git;

    public void initializeRepository(ProjectConfig projectConfig) throws IOException, IllegalStateException, GitAPIException {
        File repoDir = new File(projectConfig.getDir(), "historyRepo");
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            git = new Git(builder.setGitDir(new File(repoDir, ".git"))
                    .readEnvironment()
                    .findGitDir()
                    .build());
        } catch (RepositoryNotFoundException e) {
            git = Git.init().setDirectory(repoDir).call();
            git.commit().setMessage("initial commit").call();
        }
    }

    public void addAllAndCommit(String message, ProjectConfig projectConfig) throws GitAPIException, IOException {
        initializeRepository(projectConfig);
        git.add().addFilepattern(".").call();
        git.commit().setMessage(message).call();
    }

    public List<String> gitlog(ProjectConfig projectConfig) throws GitAPIException, IOException {
        initializeRepository(projectConfig);
        List<String> logs = new ArrayList<>();
        Iterable<RevCommit> commits = git.log().call();
        List<RevCommit> commitList = new ArrayList<>();
        commits.forEach(commitList::add);

        // Sort commits in chronological order (ascending)
        Collections.reverse(commitList);

        for (RevCommit commit : commitList) {
            logs.add(commit.getId().getName() + " - " + commit.getFullMessage());
        }

        return logs;
    }
}
