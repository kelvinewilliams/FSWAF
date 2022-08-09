package dev.k77.fluentstream.WAF;

import dev.k77.fluentstream.constants.GitConstants;
import dev.k77.fluentstream.constants.MemoryStoreConstants;
import dev.k77.fluentstream.utils.AWSWAFUtils;
import dev.k77.fluentstream.utils.FileUtils;
import dev.k77.fluentstream.utils.MemoryStoreUtils;
import dev.k77.fluentstream.utils.NetUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AWSWAF extends Thread {
    private static boolean running = false;
    private final HashMap<String, String> ipListIds;

    private final AWSWAFUtils utils;


    public AWSWAF() {
        utils = new AWSWAFUtils();
        ipListIds = new HashMap<>();
        loadIpListIds();
    }

    @Override
    public void run() {
        if (running) return;
        running = true;
        try {
            processRepo();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        saveIpListIds();
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
        ArrayList<String> ipList = new ArrayList<>();
        List<String> fileIpList = Files.readAllLines(path);
        int chunk = 0;

        String listId = ipListIds.get(createIPSetName(path.toFile().getName(), chunk));
        if (listId != null) processUpdate(listId, path);

        for (String line : fileIpList) {
            if (NetUtils.isCidr(line)) ipList.add(line);
            if (NetUtils.isIpAddress(line)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(line);
                stringBuilder.append("/32");
                ipList.add(stringBuilder.toString());
            }
            if (ipList.size() == 10000) {
                String id = utils.createIPSetRequest(createIPSetName(path.toFile().getName(), chunk), ipList);
                ipListIds.put(createIPSetName(path.toFile().getName(), chunk), id);
                ipList.clear();
                chunk++;
            }
        }
        String id = utils.createIPSetRequest(createIPSetName(path.toFile().getName(), chunk), ipList);
        ipListIds.put(createIPSetName(path.toFile().getName(), chunk), id);
    }

    private void processUpdate(String listId, Path path) throws IOException {
        ArrayList<String> ipList = new ArrayList<>();
        List<String> fileIpList = Files.readAllLines(path);
        int chunk = 0;

        for (String line : fileIpList) {
            if (NetUtils.isCidr(line)) ipList.add(line);
            if (NetUtils.isIpAddress(line)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(line);
                stringBuilder.append("/32");
                ipList.add(stringBuilder.toString());
            }
            if (ipList.size() == 10000) {
                utils.updateIPSetRequest(listId, createIPSetName(path.toFile().getName(), chunk), ipList);
                ipListIds.put(createIPSetName(path.toFile().getName(), chunk), listId);
                ipList.clear();
                chunk++;
            }
        }
        utils.updateIPSetRequest(listId, createIPSetName(path.toFile().getName(), chunk), ipList);
        ipListIds.put(createIPSetName(path.toFile().getName(), chunk), listId);
    }


    private String createIPSetName(String fileName, int chunk) {
        StringBuffer sb = new StringBuffer();
        sb.append(fileName.replaceAll("\\.","_"));
        sb.append("_");
        sb.append(chunk);
        System.out.println(sb.toString());
        return sb.toString();
    }

    private void loadIpListIds() {
        MemoryStoreUtils memoryStoreUtils = null;
        try {
            memoryStoreUtils = new MemoryStoreUtils();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> ids =
                (Map<String, String>) memoryStoreUtils.get(MemoryStoreConstants.AWSWAF_IPLIST_IDS_KEY);
        ipListIds.putAll(ids);
    }
    private void saveIpListIds() {
        MemoryStoreUtils memoryStoreUtils = null;
        try {
            memoryStoreUtils = new MemoryStoreUtils();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<String, String> ids = new HashMap<>();
        ids.putAll(ipListIds);
        memoryStoreUtils.set(MemoryStoreConstants.AWSWAF_IPLIST_IDS_KEY, ids, (30*24*60*60));
    }
}
