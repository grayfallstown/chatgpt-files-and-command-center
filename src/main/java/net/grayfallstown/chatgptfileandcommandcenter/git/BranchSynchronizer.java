package net.grayfallstown.chatgptfileandcommandcenter.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class BranchSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(BranchSynchronizer.class);

    public void syncBranchNames(String userRepoPath, String historyRepoPath) throws IOException, GitAPIException {
        try (Git userGit = Git.open(new File(userRepoPath));
             Git historyGit = Git.open(new File(historyRepoPath))) {

            String userBranch = userGit.getRepository().getBranch();
            logger.info("User repo branch: {}", userBranch);

            historyGit.checkout().setName(userBranch).call();
            logger.info("Synchronized history repo to branch: {}", userBranch);
        }
    }
}
