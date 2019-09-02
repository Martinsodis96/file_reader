package soderstrand.martin.filereader.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.stream.Stream;

@Component
public class PackageScanner {

    @Value("${scan.folder.path}")
    private String folderPath;
    private final FileService fileService;

    @Autowired
    public PackageScanner(FileService fileService) {
        this.fileService = fileService;
    }

    public void scanFolder() {
        Path path = Paths.get(folderPath);

        try (Stream<Path> paths = Files.walk(path)) {
            paths.filter(Files::isRegularFile)
                    .filter(filePath -> filePath.toFile().getName().endsWith(".txt"))
                    .forEach(fileService::manageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void watchFolder() {
        Path path = Paths.get(folderPath);
        WatchService watchService;

        try {
            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                Path directory = (Path) key.watchable();
                Thread.sleep(100);
                key.pollEvents().stream()
                        .map(watchEvent -> directory.resolve((Path) watchEvent.context()))
                        .filter(filePath -> filePath.toFile().getName().endsWith(".txt"))
                        .forEach(fileService::manageFile);
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
