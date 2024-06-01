package net.grayfallstown.chatgptfileandcommandcenter.git;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import net.grayfallstown.chatgptfileandcommandcenter.history.BranchSynchronizer;

import java.io.File;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class BranchSynchronizerTest {

    @InjectMocks
    private BranchSynchronizer branchSynchronizer;

    @Mock
    private Git userGit;

    @Mock
    private Git historyGit;

    @Mock
    private CheckoutCommand checkoutCommand;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(historyGit.checkout()).thenReturn(checkoutCommand);
        when(checkoutCommand.setName(anyString())).thenReturn(checkoutCommand);
    }

    @Test
    void syncBranchNames() throws IOException, GitAPIException {
        when(userGit.getRepository()).thenReturn(mock(Repository.class));
        when(historyGit.getRepository()).thenReturn(mock(Repository.class));
        when(userGit.getRepository().getBranch()).thenReturn("main");

        try (var mockedGit = mockStatic(Git.class)) {
            mockedGit.when(() -> Git.open(new File("userRepoPath"))).thenReturn(userGit);
            mockedGit.when(() -> Git.open(new File("historyRepoPath"))).thenReturn(historyGit);

            branchSynchronizer.syncBranchNames("userRepoPath", "historyRepoPath");

            verify(checkoutCommand, times(1)).setName("main");
        }
    }
}
