package ru.itis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itis.Application;
import ru.itis.reader.FileRuleReader;
import ru.itis.reader.RuleCreater;

import java.io.File;
import java.nio.charset.Charset;

@Configuration
public class AppConfig {

  @Value("${additional.filename:systemCsv}")
  private String path;

  @Bean
  public FileRuleReader fileRuleReader() {
    return new FileRuleReader();
  }

  @Bean
  public RuleCreater ruleCreater() {
    RuleCreater nodeCreater =
        new RuleCreater(
            fileRuleReader().read(new File(Application.filePath)));
    nodeCreater.setAdditionalFilePath(path);
    return nodeCreater;
  }
}
