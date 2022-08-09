package dev.k77.fluentstream.WAF;

import dev.k77.fluentstream.constants.GitConstants;

import dev.k77.fluentstream.constants.MemoryStoreConstants;
import dev.k77.fluentstream.utils.FileUtils;
import dev.k77.fluentstream.utils.GitUtils;
import dev.k77.fluentstream.utils.MemoryStoreUtils;
import dev.k77.fluentstream.utils.NetUtils;
import org.apache.commons.net.util.SubnetUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IPListUpdater
{
    private static boolean updateInProgress = false;
    private MemoryStoreUtils memoryStoreUtils;
    private FileUtils fileUtils;

    public static void main(String[] args) throws IOException {
        new IPListUpdater();
    }

    public IPListUpdater() throws IOException {
        if (updateInProgress) return;
        updateInProgress = true;

        memoryStoreUtils = new MemoryStoreUtils();
        fileUtils = new FileUtils();


        long lastClone = 0;
        try {
            lastClone = Long.parseLong(memoryStoreUtils.get(MemoryStoreConstants.GIT_LAST_CLONE_KEY) + "");
        } catch (NumberFormatException e) {
            lastClone = 0;
        }

        if (System.currentTimeMillis() - lastClone > 86400000) {
            try {
                if (!fileUtils.deleteDirectory(GitConstants.GIT_REPO_DIR)) return;
            } catch (IOException e) {}
            if (cloneRepo()) {
                memoryStoreUtils.set(MemoryStoreConstants.GIT_LAST_CLONE_KEY, System.currentTimeMillis() + "",
                        MemoryStoreConstants.EXPIRE_WEEK);
            } else {
                return;
            }
        }

        FSWAF fswaf = new FSWAF();
        fswaf.start();

        AWSWAF awswaf = new AWSWAF();
        awswaf.start();

    }

    private boolean cloneRepo() throws IOException {
        FileUtils fileUtils = new FileUtils();
        Path path = Paths.get(GitConstants.GIT_REPO_DIR);
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                fileUtils.deleteDirectory(path);
            } catch (NoSuchFileException e) {
                // Do nothing.
            }
        }

        GitUtils gitUtils = new GitUtils();
        return gitUtils.cloneRepo();
    }





    private static HashMap<String, String> cidrs = new HashMap<>();

    public boolean checkCidrs(String ipAddress) {
        if (cidrs == null) copyCidrSetFromMemoryStore();
        for (Map.Entry<String, String> entry : cidrs.entrySet()) {
            SubnetUtils subnetUtils = new SubnetUtils(entry.getKey());
            if (subnetUtils.getInfo().isInRange(ipAddress)) return true;
        }

        return false;
    }

    private void copyCidrSetFromMemoryStore() {
        MemoryStoreUtils memoryStoreUtils = null;
        try {
            memoryStoreUtils = new MemoryStoreUtils();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Object o = memoryStoreUtils.get(dev.k77.fluentstream.constants.MemoryStoreConstants.CIDRS_KEY);

        Map<String, String> map = new HashMap<>();
        map = (Map<String, String>) memoryStoreUtils.get(dev.k77.fluentstream.constants.MemoryStoreConstants.CIDRS_KEY);

    }


}
