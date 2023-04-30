package com.makeathontumai.aideate.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Rating {
    public Double avg;
    public Integer nrOf;
    public Integer latest;
}
