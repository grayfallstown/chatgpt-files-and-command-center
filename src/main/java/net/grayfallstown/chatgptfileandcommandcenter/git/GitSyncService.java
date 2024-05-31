package net.grayfallstown.chatgptfileandcommandcenter.git;

import net.grayfallstown.chatgptfileandcommandcenter.config.ProjectConfig;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class GitSyncService {
    private static final Logger logger = LoggerFactory.getLogger(GitSyncService.class);

    private final ProjectConfig projectConfig;
    private final BranchSynchronizer branchSynchronizer;
    private final IgnoreRulesHandler ignoreRulesHandler;
    private final FileTracker fileTracker;

    public GitSyncService(ProjectConfig projectConfig, BranchSynchronizer branchSynchronizer, IgnoreRulesHandler ignoreRulesHandler, FileTracker fileTracker) {
        this.projectConfig = projectConfig;
        this.branchSynchronizer = branchSynchronizer;
        this.ignoreRulesHandler = ignoreRulesHandler;
        this.fileTracker = fileTracker;
    }

    public void createNewRepository() throws IOException {
        var repoPath = projectConfig.getDir() + "/historyRepo";
        var workTreePath = projectConfig.getWorkingDir();

        logger.info("Creating new repository at {}", repoPath);
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        builder.setGitDir(new File(repoPath))
               .setWorkTree(new File(workTreePath))
               .readEnvironment()
               .findGitDir();

        Repository repository = builder.build();
        repository.create();
        logger.info("Repository created successfully at {}", repoPath);
    }

    public void addAllAndCommit(String commitMessage) throws IOException, GitAPIException {
        var repoPath = projectConfig.getDir() + "/historyRepo";

        try (Git git = Git.open(new File(repoPath))) {
            logger.info("Opening repository at {}", repoPath);
            var ignorePatterns = ignoreRulesHandler.loadGitIgnore(projectConfig.getWorkingDir());
            logger.info("Loaded ignore patterns: {}", ignorePatterns);
            fileTracker.addFilesRecursively(git, new File(git.getRepository().getWorkTree().getPath()), ignorePatterns);
            git.commit().setMessage(commitMessage).call();
            logger.info("Committed changes with message: {}", commitMessage);
        }
    }
}
