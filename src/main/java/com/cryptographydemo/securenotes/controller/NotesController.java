package com.cryptographydemo.securenotes.controller;

import com.cryptographydemo.securenotes.dto.NoteRequest;
import com.cryptographydemo.securenotes.dto.NoteResponse;
import com.cryptographydemo.securenotes.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
@Tag(name = "Secure Notes API", description = "CRUD endpoints for encrypted notes")
@SecurityRequirement(name = "bearerAuth")
public class NotesController {

    private final NoteService noteService;

    public NotesController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    @Operation(summary = "Create a new secure note")
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request) {
        NoteResponse created = noteService.create(request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a secure note by ID")
    public ResponseEntity<NoteResponse> getById(@PathVariable Long id) {
        NoteResponse dto = noteService.get(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @Operation(summary = "List all secure notes")
    public ResponseEntity<List<NoteResponse>> listAll() {
        List<NoteResponse> all = noteService.list();
        return ResponseEntity.ok(all);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a secure note by ID")
    public ResponseEntity<NoteResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest request
    ) {
        NoteResponse updated = noteService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a secure note by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
