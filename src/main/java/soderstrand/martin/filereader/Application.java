package soderstrand.martin.filereader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import soderstrand.martin.filereader.util.PackageScanner;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(Application.class, args);
        PackageScanner packageScanner = applicationContext.getBean(PackageScanner.class);

        packageScanner.scanFolder();
        packageScanner.watchFolder();
    }
}
