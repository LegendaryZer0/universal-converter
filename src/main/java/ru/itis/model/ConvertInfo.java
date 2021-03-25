package ru.itis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.validation.ValidValue;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConvertInfo {
  @ValidValue(message = "unexpected value from")
  private String from;

  @ValidValue(message = "unexpected value to")
  private String to;

  @Override
  public String toString() {
    return "ConvertInfo{" + "from='" + from + '\'' + ", to='" + to + '\'' + '}';
  }
}
