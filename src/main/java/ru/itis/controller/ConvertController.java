package ru.itis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.model.ConvertInfo;
import ru.itis.service.ConvertService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
public class ConvertController {

  @Autowired private ConvertService service;

  @PostMapping("/convert")
  public String convert(
      @RequestBody @Valid ConvertInfo convertInfo,
      BindingResult result,
      HttpServletResponse response) {
    log.info(String.valueOf(convertInfo));
    if (!result.hasErrors()) {
      try {
        return service.execute(convertInfo);
      } catch (IllegalStateException e) {
        response.setStatus(400);
      }

    } else {
      response.setStatus(404);
    }
    return "";
  }
}
