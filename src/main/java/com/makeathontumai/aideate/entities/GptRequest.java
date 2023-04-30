package com.makeathontumai.aideate.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GptRequest {
    private String model;
    private List<GptMessage> messages;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
}
