package dev.k77.fluentstream.utils;

import org.eclipse.jgit.api.Git;
import dev.k77.fluentstream.constants.GitConstants;

import java.io.File;

public class GitUtils {

    public GitUtils() {

    }
    public boolean cloneRepo() {
        if (GitConstants.gitOperationInProgress) return false;
        GitConstants.gitOperationInProgress = true;
        try {
            Git git = Git.cloneRepository()
                    .setURI(GitConstants.GIT_REPO_URI)
                    .setDirectory(new File(GitConstants.GIT_REPO_DIR))
                    .call();
            System.out.println("Git Repo " + GitConstants.GIT_REPO_URI + " cloned to " + GitConstants.GIT_REPO_DIR + "");
            GitConstants.gitOperationInProgress = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            GitConstants.gitOperationInProgress = false;
            return false;
        }
    }
}
