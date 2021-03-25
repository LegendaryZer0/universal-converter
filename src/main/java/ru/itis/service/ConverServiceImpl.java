package ru.itis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.model.ConvertInfo;
import ru.itis.util.Formatter;

@Service
public class ConverServiceImpl implements ConvertService {
  @Autowired private Formatter formatter;

  public String execute(ConvertInfo info) {
    return formatter.format15num(formatter.convert(info.getFrom(), info.getTo())).replace(',', '.');
  }
}
