package net.grayfallstown.chatgptfileandcommandcenter.history;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.ignore.FastIgnoreRule;
import org.eclipse.jgit.ignore.IgnoreNode;
import org.eclipse.jgit.ignore.IgnoreNode.MatchResult;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public IgnoreNode getIgnoreNode(Path workingDir) {
        return getIgnoreNode(getGitIgnorePatterns(workingDir));
    }

    public IgnoreNode getIgnoreNode(List<String> gitIgnorePatterns) {
        IgnoreNode ignoreNode = new IgnoreNode(gitIgnorePatterns.stream()
            .map(FastIgnoreRule::new).collect(Collectors.toList()));
        return ignoreNode;
    }

    public List<String> getGitIgnorePatterns(Path workingDir) {
        List<String> patterns = new ArrayList<>();
        try (var paths = Files.walk(workingDir)) {
            paths.filter(path -> path.getFileName().toString().equals(".gitignore"))
                    .forEach(gitignore -> {
                        try {
                            patterns.addAll(Files.readAllLines(gitignore));
                        } catch (IOException e) {
                            throw new RuntimeException("Could not load gitignore patterns from file: " + gitignore, e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Could not walk the directory: " + workingDir, e);
        }
        logger.debug("ignore patterns {}", patterns);
        return patterns;
    }

    public boolean isGitIgnored(Path basePath, Path pathToCheck, IgnoreNode ignoreNode) {
        Path relativePath = basePath.relativize(pathToCheck).normalize();
        boolean isDirectory = Files.isDirectory(pathToCheck);
        
        MatchResult result = ignoreNode.isIgnored(relativePath.toString(), isDirectory);
        
        return result == MatchResult.IGNORED;
    }
}
