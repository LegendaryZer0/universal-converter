package ru.itis.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.itis.Application;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = Application.class)
@SpringBootTest(
    classes = Application.class,
    args = {"src/test/resources/test.csv"})
@AutoConfigureMockMvc
class ConvertControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void convert() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/convert")
            .content("{\n" + " \"from\": \"м/ с\",\n" + " \"to\": \"км /час\"\n" + "}")
            .contentType(MediaType.APPLICATION_JSON);

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertEquals("3.6", result.getResponse().getContentAsString());
  }

  @Test
  void convertWithNoValidValues() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/convert")
            .content("{\n" + " \"from\": \"м/ с\",\n" + " \"to\": \"км /гномы\"\n" + "}")
            .contentType(MediaType.APPLICATION_JSON);

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(404, result.getResponse().getStatus());
  }

  @Test
  void convertWithNoValidRules() throws Exception {
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/convert")
            .content("{\n" + " \"from\": \"м/ с\",\n" + " \"to\": \"км /м\"\n" + "}")
            .contentType(MediaType.APPLICATION_JSON);

    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertEquals(400, result.getResponse().getStatus());
  }
}
