package ru.itis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import ru.itis.config.AppConfig;

import java.nio.file.Paths;

@Slf4j
@SpringBootApplication
@Import(AppConfig.class)
public class Application {

  public static String filePath = "src/test/resources/test.csv";

  public static void main(String[] args) {
    if (args.length > 0) {
      log.info("PATH TO FILE  -  {}",args[0]);

      filePath = Paths.get(args[0]).normalize().toAbsolutePath().toString();

      log.info("Finale path to file{}",filePath);
    }

    SpringApplication.run(Application.class, args);
  }

}
