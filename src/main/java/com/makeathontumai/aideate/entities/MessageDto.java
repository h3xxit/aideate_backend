package com.makeathontumai.aideate.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MessageDto {
    private Integer sessionId;
    private String text;
}
