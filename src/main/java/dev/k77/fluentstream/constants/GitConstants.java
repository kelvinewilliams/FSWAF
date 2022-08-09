package dev.k77.fluentstream.constants;

import java.nio.file.FileSystems;

public class GitConstants {
    public static final String GIT_REPO_NAME = "blocklist-ipsets";
    public static final String GIT_REPO_URI = "https://github.com/firehol/blocklist-ipsets.git";
    public static final String GIT_REPO_DIR = System.getProperty("java.io.tmpdir") + FileSystems.getDefault().getSeparator() + GIT_REPO_NAME;
    public static boolean gitOperationInProgress = false;
}
