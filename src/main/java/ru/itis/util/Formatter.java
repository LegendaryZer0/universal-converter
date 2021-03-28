package ru.itis.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itis.model.Pair;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class Formatter {

  private final HashMap<String, ArrayList<Pair<String, BigDecimal>>> info;
  private Pair<BigDecimal, String> ansFound = null;

  public Formatter(HashMap<String, ArrayList<Pair<String, BigDecimal>>> info) {
    this.info = info;
  }

  @Autowired
  public Formatter(RuleLoader ruleLoader) throws IOException {
    if (!ruleLoader.isLoaded()) ruleLoader.loadConvertingRules();
    info = ruleLoader.getRules();
  }
  //
  private Pair<BigDecimal, String> getPopAnsFound() {
    Pair<BigDecimal, String> temp = ansFound;
    ansFound = null;
    return temp;
  }

  public void findInRuleDFS(
      String from, List<String> variants, HashMap<String, Boolean> usedFroms, BigDecimal ans) {

    usedFroms.put(from, true);
    for (String temp : variants) {
      if (temp.equals(from)) {
        ansFound = new Pair<>(ans, from);
        return;
      }
    }

    if (!info.containsKey(from)) {
      return;
    }
    ArrayList<Pair<String, BigDecimal>> pairs = info.get(from);

    for (Pair<String, BigDecimal> pair : pairs) {

      if (!usedFroms.containsKey(pair.getKey()) && ansFound == null) {

        findInRuleDFS(pair.getKey(), variants, usedFroms, ans.multiply(pair.getValue()));
      }
    }
  }

  private Pair<BigDecimal, Pair<List<String>, List<String>>> reduceFraction(List<String> numDenum) {
    if (numDenum.size() == 0) throw new IllegalArgumentException("Can't convert");

    BigDecimal coef = new BigDecimal(1);
    List<String> numerators = argsOfExpression(numDenum.get(0));

    if (numDenum.size() == 2) {
      List<String> denumerators = argsOfExpression(numDenum.get(1));
      Iterator<String> it = denumerators.listIterator();
      while (it.hasNext()) {
        String now = it.next();
        try {
          coef = coef.multiply(BigDecimal.valueOf(1 / Double.parseDouble(now)));
          it.remove();
        } catch (NumberFormatException ignored) {
          // ignore
        }
      }

      it = numerators.listIterator();
      while (it.hasNext()) {
        String now = it.next();
        try {
          coef = coef.multiply(BigDecimal.valueOf(Double.parseDouble(now)));
          it.remove();
        } catch (NumberFormatException e) {
          findInRuleDFS(now, denumerators, new HashMap<>(), new BigDecimal(1));
          if (ansFound != null) {
            Pair<BigDecimal, String> temp = getPopAnsFound();
            denumerators.remove(temp.getValue());
            coef = coef.multiply(temp.getKey());
            it.remove();
          }
        }
      }
      return new Pair<>(coef, new Pair<>(numerators, denumerators));
    }
    if (numDenum.size() == 1) {
      Iterator<String> it = numerators.listIterator();
      while (it.hasNext()) {
        String now = it.next();
        try {
          coef = coef.multiply(BigDecimal.valueOf(Double.parseDouble(now)));
          it.remove();
        } catch (NumberFormatException ignored) {
          // ignore
        }
      }
      return new Pair<>(coef, new Pair<>(numerators, new ArrayList<>()));
    }
    return new Pair<>(coef, new Pair<>(new ArrayList<>(), new ArrayList<>()));
  }

  public BigDecimal convert(String from, String to) {
    List<String> fromNumDenum = splitDevide(from);
    List<String> toNumDenum = splitDevide(to);

    Pair<BigDecimal, Pair<List<String>, List<String>>> reducedFractionFrom =
        reduceFraction(fromNumDenum);
    Pair<BigDecimal, Pair<List<String>, List<String>>> reducedFractionTo =
        reduceFraction(toNumDenum);

    List<String> fromNumerators = reducedFractionFrom.getValue().getKey();
    List<String> toNumerators = reducedFractionTo.getValue().getKey();
    if (fromNumerators.size() != toNumerators.size())
      throw new IllegalArgumentException("Can't convert");

    ArrayList<BigDecimal> numeratorsCoef = new ArrayList<>();
    ArrayList<BigDecimal> denumeratorsCoef = new ArrayList<>();

    findRuleDfsInDenumerators(fromNumerators, toNumerators, numeratorsCoef);

    fromNumerators = reducedFractionFrom.getValue().getValue();
    toNumerators = reducedFractionTo.getValue().getValue();

    if (fromNumerators.size() != toNumerators.size())
      throw new IllegalArgumentException("Can't convert");

    findRuleDfsInDenumerators(fromNumerators, toNumerators, denumeratorsCoef);

    BigDecimal[] onlyNumerators = toOnlyNumerators(numeratorsCoef, denumeratorsCoef);
    return getAnswer(onlyNumerators)
        .multiply(reducedFractionFrom.getKey())
        .divide(reducedFractionTo.getKey(), 25, RoundingMode.UP);
  }

  private void findRuleDfsInDenumerators(
      List<String> fromNumerators,
      List<String> toNumerators,
      ArrayList<BigDecimal> denumeratorsCoef) {
    for (String denumerator : fromNumerators) {
      findInRuleDFS(denumerator, toNumerators, new HashMap<>(), new BigDecimal(1));
      if (ansFound == null) throw new IllegalArgumentException("Can't convert");

      Pair<BigDecimal, String> temp = getPopAnsFound();
      toNumerators.remove(temp.getValue());

      denumeratorsCoef.add(temp.getKey());
    }
  }

  private List<String> argsOfExpression(String exp) {
    return new LinkedList<>(Arrays.asList(exp.split("\\*")));
  }

  private List<String> splitDevide(String exp) {
    exp = exp.replace(" ", "");
    return new LinkedList<>(Arrays.asList(exp.split("/")));
  }

  private BigDecimal getAnswer(BigDecimal[] numeratorsArray) {
    BigDecimal answer = new BigDecimal(1);

    for (int i = 0; i < numeratorsArray.length / 2; i++) {
      answer =
          answer
              .multiply(numeratorsArray[i])
              .multiply(numeratorsArray[numeratorsArray.length - i - 1]);
    }
    if (numeratorsArray.length % 2 == 1) {
      answer = answer.multiply(numeratorsArray[numeratorsArray.length / 2]);
    }
    return answer;
  }

  public BigDecimal[] toOnlyNumerators(List<BigDecimal> numerators, List<BigDecimal> denumerators) {
    BigDecimal[] onlyNumerators = new BigDecimal[numerators.size() + denumerators.size()];
    int i = 0;
    for (BigDecimal decimal : denumerators) {
      onlyNumerators[i] = new BigDecimal(1).divide(decimal, 25, RoundingMode.UP);
      i++;
    }
    for (BigDecimal decimal : numerators) {
      onlyNumerators[i] = decimal;
      i++;
    }
    Arrays.sort(onlyNumerators);
    return onlyNumerators;
  }

  public String format15num(BigDecimal longNum) {
    DecimalFormat decimalFormat = new DecimalFormat("#.###############");
    return decimalFormat.format(longNum);
  }

  public void validationTransform(String toValidate) throws IllegalArgumentException {
    Matcher m = Pattern.compile("(\\*)|(\\s)|(\\/)").matcher(toValidate);
    ArrayList<String> elements = new ArrayList<>();
    ArrayList<String> separators = new ArrayList<>();
    int pos;
    for (pos = 0; m.find(); pos = m.end()) {
      elements.add(toValidate.substring(pos, m.start()));
      separators.add(m.group());
    }
    elements.add(toValidate.substring(pos));
    log.info(elements.toString());
    for (String str : elements) {
      try {
        if (!str.equals("")) {
          Double.parseDouble(str);
        }
      } catch (NumberFormatException e) {
        if (!info.containsKey(str)) {
          log.info(elements.toString());
          log.info("non valid str::{}::!", str);

          throw new IllegalArgumentException("NO RULE");
        }
      }
    }
  }
}
