package com.flauschcode.broccoli.seasons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Month {

    JANUARY(1),
    FEBRUARY(2),
    MARCH(3),
    APRIL(4),
    MAY(5),
    JUNE(6),
    JULY(7),
    AUGUST(8),
    SEPTEMBER(9),
    OCTOBER(10),
    NOVEMBER(11),
    DECEMBER(12);

    @JsonValue
    private int value;

    Month(int value) {
        this.value = value;
    }

    @JsonCreator
    public static Month of(int number) {
        for (Month month : Month.values()) {
            if (month.value == number) {
                return month;
            }
        }
        return null;
    }

}
