package dev.k77.fluentstream.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {

    public String readFile(Path path) throws IOException {
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = Files.newBufferedReader(path)) {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }
    private String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br
                     = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
    public FileUtils() {

    }
    public List<Path> listFiles(Path dir) throws IOException {

        List<Path> dirList;
        try (Stream<Path> stream = Files.walk(dir)) {
            dirList = stream.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        return dirList;
    }
    public List<Path> listFiles(String dir) throws IOException {
        return listFiles(Paths.get(dir));
    }

    public boolean deleteDirectory(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        return true;
    }

    public boolean deleteDirectory(String dir) throws IOException {
        return deleteDirectory(Paths.get(dir));
    }
}
