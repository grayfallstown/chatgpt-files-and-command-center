package net.grayfallstown.chatgptfileandcommandcenter.history;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GitSyncService {
    private static final Logger logger = LoggerFactory.getLogger(GitSyncService.class);
    private Git git;

    public void initializeRepository(ProjectConfig projectConfig) {
        File repoDir = new File(projectConfig.getDir(), "historyRepo");
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        try {
            git = new Git(builder.setGitDir(new File(repoDir, ".git"))
                .readEnvironment()
                .findGitDir()
                .build());
            logger.info("Initialized repository at {}", repoDir.getAbsolutePath());
        } catch (RepositoryNotFoundException e) {
            try {
                git = Git.init().setDirectory(repoDir).call();
                git.commit().setMessage("initial commit").call();
                logger.info("Initialized new repository at {}", repoDir.getAbsolutePath());
            } catch (GitAPIException ex) {
                logger.error("Failed to initialize repository at {}", repoDir.getAbsolutePath(), ex);
                throw new GitOperationException("Failed to initialize repository", ex);
            }
        } catch (IOException e) {
            logger.error("Failed to initialize repository at {}", repoDir.getAbsolutePath(), e);
            throw new GitOperationException("Failed to initialize repository", e);
        }
    }

    public void addAllAndCommit(String message, ProjectConfig projectConfig) {
        try {
            initializeRepository(projectConfig);
            git.add().addFilepattern(".").call();
            git.commit().setMessage(message).call();
            logger.info("Committed changes with message: {}", message);
        } catch (GitAPIException e) {
            logger.error("Failed to add and commit changes with message: {}", message, e);
            throw new GitOperationException("Failed to add and commit changes", e);
        }
    }

    public List<String> gitlog(ProjectConfig projectConfig) {
        try {
            initializeRepository(projectConfig);
            List<String> logs = new ArrayList<>();
            Iterable<RevCommit> commits = git.log().call();
            List<RevCommit> commitList = new ArrayList<>();
            commits.forEach(commitList::add);

            Collections.reverse(commitList);
            for (RevCommit commit : commitList) {
                logs.add(commit.getId().getName() + " - " + commit.getFullMessage());
            }
            logger.info("Retrieved git log for project: {}", projectConfig.getDir());
            return logs;
        } catch (GitAPIException e) {
            logger.error("Failed to retrieve git log for project: {}", projectConfig.getDir(), e);
            throw new GitOperationException("Failed to retrieve git log", e);
        }
    }
}
