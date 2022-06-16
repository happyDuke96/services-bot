package com.example.start_bot.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Data
@Getter
@Setter
public class Currency {
    private String name;
    private String nameUZ;
    private String nameRU;
    private String nameUZC;
    private BigDecimal rate;
    private BigDecimal diff;
}
