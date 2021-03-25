package ru.itis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.itis.config.AppConfig;

import java.nio.file.Path;

@Slf4j
@SpringBootApplication
@Import(AppConfig.class)
public class Application {

  public static String filePath = "test.csv";

  public static void main(String[] args) {
    if (args.length > 0) {
      filePath = Path.of(args[0]).normalize().toAbsolutePath().toString();
    }
    SpringApplication.run(Application.class, args);
  }
}
