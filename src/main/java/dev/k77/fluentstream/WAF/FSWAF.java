package dev.k77.fluentstream.WAF;

import dev.k77.fluentstream.constants.GitConstants;
import dev.k77.fluentstream.constants.MemoryStoreConstants;
import dev.k77.fluentstream.constants.MemoryStoreConstants.*;
import dev.k77.fluentstream.utils.FileUtils;
import dev.k77.fluentstream.utils.MemoryStoreUtils;
import dev.k77.fluentstream.utils.NetUtils;
import org.apache.commons.net.util.SubnetUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FSWAF extends Thread {
    private static boolean running = false;
    private static HashMap<String, String> cidrs;

    private MemoryStoreUtils memoryStoreUtils;

    public static void main(String[] args) {
        new FSWAF();
    }
    public FSWAF() {
        cidrs = new HashMap<>();
        try {
            memoryStoreUtils = new MemoryStoreUtils();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        if (running) return;
        running = true;
        cidrs = (HashMap<String, String>) memoryStoreUtils.get(MemoryStoreConstants.CIDRS_KEY);
        try {
            processRepo();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        saveCidrs();
        running = false;
    }

    private void processRepo() throws IOException {
        FileUtils fileUtils = new FileUtils();
        Path dir = Paths.get(GitConstants.GIT_REPO_DIR);
        List<Path> dirList = fileUtils.listFiles(dir);
        for (Path path : dirList) {
            if (path.toString().contains(".git")) continue;
            processFile(path);
        }
    }
    private void processFile(Path path) throws IOException {
        System.out.println("FSWAF processing: " + path.toFile().getName());
        List<String> ipList = Files.readAllLines(path);
        for (String line : ipList) {
            if (NetUtils.isCidr(line)) processCidr(line, path.toFile().getName());
            if (NetUtils.isIpAddress(line)) processIpAddr(line, path.toFile().getName());
        }
        saveCidrs();
    }

    private void processCidr(String cidr, String fileName) {
        SubnetUtils subnetUtils = new SubnetUtils(cidr);
        subnetUtils.setInclusiveHostCount(true);
        if (subnetUtils.getInfo().getAddressCountLong() < 1024) {
            for (int i = subnetUtils.getInfo().asInteger(subnetUtils.getInfo().getLowAddress());
            i <= subnetUtils.getInfo().asInteger(subnetUtils.getInfo().getHighAddress()); i++) {
                processIpAddr(NetUtils.ntoa(i), fileName);
            }
        } else {
            cidrs.put(cidr, "N");
        }
    }

    private void processIpAddr(String ipAddr, String fileName) {
         memoryStoreUtils.set(ipAddr, fileName, MemoryStoreConstants.EXPIRE_DAY);
    }

    private void saveCidrs() {
        if (cidrs.size() > 0) memoryStoreUtils.set(MemoryStoreConstants.CIDRS_KEY, cidrs, MemoryStoreConstants.EXPIRE_WEEK);
    }
}
