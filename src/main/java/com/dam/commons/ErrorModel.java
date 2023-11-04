package com.dam.commons;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonPropertyOrder({ "title", "errorCode", "description", "descriptionEn", "uuid", "time", "validationError" })
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorModel {
    @JsonProperty(index = 2)
    private int errorCode;
    @JsonProperty(value = "description", index = 3)
    private String message;
    @JsonProperty(index = 4)
    private String descriptionEN;
    @JsonProperty(value = "uuid", index = 5)
    private String uuid;
    @JsonProperty(value = "time", index = 6)
    private String time;
}
