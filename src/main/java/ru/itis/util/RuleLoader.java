package ru.itis.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import ru.itis.model.Pair;
import ru.itis.reader.FileRuleReader;
import ru.itis.reader.RuleCreater;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

@Component
@Getter
@Setter
public class RuleLoader {
  private HashMap<String, ArrayList<Pair<String, BigDecimal>>> rules;
  private boolean loaded;

  private FileRuleReader fileRuleReader;
  private RuleCreater ruleCreater;

  public RuleLoader(FileRuleReader fileRuleReader, RuleCreater ruleCreater) {
    this.rules = new HashMap<>();
    this.fileRuleReader = fileRuleReader;
    this.ruleCreater = ruleCreater;
  }

  public void loadConvertingRules() {

    rules = ruleCreater.create();
    loaded = true;
  }

  public HashMap<String, ArrayList<Pair<String, BigDecimal>>> getRules() {
    return rules;
  }

  public boolean isLoaded() {
    return loaded;
  }
}
