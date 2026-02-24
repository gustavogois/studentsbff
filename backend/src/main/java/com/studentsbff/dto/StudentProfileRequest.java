package com.studentsbff.dto;

import com.studentsbff.model.Grade;
import lombok.Data;

@Data
public class StudentProfileRequest {

    private Grade grade;

    private String school;

    private String turma;
}
