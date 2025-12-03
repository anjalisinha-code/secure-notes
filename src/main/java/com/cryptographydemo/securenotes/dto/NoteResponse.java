package com.cryptographydemo.securenotes.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class NoteResponse {
    private Long id;
    private String title;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
