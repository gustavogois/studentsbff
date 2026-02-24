package com.studentsbff.model;

import lombok.Getter;

@Getter
public enum Grade {
    GRADE_7("7th Grade"),
    GRADE_8("8th Grade"),
    GRADE_9("9th Grade"),
    GRADE_10("10th Grade"),
    GRADE_11("11th Grade"),
    GRADE_12("12th Grade");

    private final String label;

    Grade(String label) {
        this.label = label;
    }
}
