package com.studentsbff.dto;

import lombok.Data;

@Data
public class GmailSyncRequest {

    private Integer daysBack = 30;
}
