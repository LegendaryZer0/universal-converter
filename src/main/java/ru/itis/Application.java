package ru.itis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import ru.itis.config.AppConfig;

import java.nio.file.Paths;

@Slf4j
@SpringBootApplication
@Import(AppConfig.class)
public class Application implements ApplicationRunner {

  public static String filePath = "src/test/resources/test.csv";

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {
    log.info("PATH TO FILE FROM APPLICATION ARGUMENTS  -  {}", args.getSourceArgs());

    filePath = Paths.get(args.getSourceArgs()[0]).normalize().toAbsolutePath().toString();

    log.info("Finale path to file{}", filePath);
  }
  }
