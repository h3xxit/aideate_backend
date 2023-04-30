package com.makeathontumai.aideate.entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class GptMessage {
    private String role;
    private String content;
}
