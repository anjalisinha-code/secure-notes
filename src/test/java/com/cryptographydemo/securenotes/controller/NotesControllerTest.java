package com.cryptographydemo.securenotes.controller;

import com.cryptographydemo.securenotes.entity.Note;
import com.cryptographydemo.securenotes.repository.NoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NotesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String AUTH_HEADER = "Authorization";
    // use env var if provided, otherwise fall back to demo token
    private static final String TOKEN = System.getenv().getOrDefault("SECURITY_TOKEN", "Bearer static-token-123");

    @AfterEach
    void cleanup() {
        noteRepository.deleteAll();
    }

    @Test
    void createNote_shouldSucceed_andStoreEncryptedContent() throws Exception {
        var payload = objectMapper.createObjectNode()
                .put("title", "Integration test note")
                .put("content", "super-secret-content");

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTH_HEADER, TOKEN)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Integration test note"))
                .andExpect(jsonPath("$.content").value("super-secret-content"));

        // verify persisted record is encrypted (rough check)
        Optional<Note> opt = noteRepository.findAll().stream().findFirst();
        assertThat(opt).isPresent();
        Note saved = opt.get();
        assertThat(saved.getContentEncrypted()).isNotNull();
        // should not store plaintext directly
        assertThat(saved.getContentEncrypted()).doesNotContain("super-secret-content");
        // basic sanity: encrypted blob is longer than small plaintext
        assertThat(saved.getContentEncrypted().length()).isGreaterThan(20);
    }

    @Test
    void createNote_withoutAuth_shouldReturn401() throws Exception {
        var payload = objectMapper.createObjectNode()
                .put("title", "No auth")
                .put("content", "n/a");

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        // no Authorization header
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createNote_withInvalidPayload_shouldReturn400() throws Exception {
        // title is blank -> validation should fail
        var payload = objectMapper.createObjectNode()
                .put("title", "")
                .put("content", "some-content");

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AUTH_HEADER, TOKEN)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }
}
