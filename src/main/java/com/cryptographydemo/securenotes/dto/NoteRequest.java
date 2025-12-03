package com.cryptographydemo.securenotes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for creating/updating notes.
 */
@Data
public class NoteRequest {

    @NotBlank(message = "title must not be empty")
    @Size(max = 255, message = "title must be at most 255 characters")
    private String title;

    @NotBlank(message = "content must not be empty")
    @Size(max = 5000, message = "content must be at most 5000 characters")
    private String content;
}
