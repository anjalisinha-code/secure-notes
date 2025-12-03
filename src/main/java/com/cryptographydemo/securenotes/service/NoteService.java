package com.cryptographydemo.securenotes.service;

import com.cryptographydemo.securenotes.dto.NoteRequest;
import com.cryptographydemo.securenotes.dto.NoteResponse;
import com.cryptographydemo.securenotes.entity.Note;
import com.cryptographydemo.securenotes.repository.NoteRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository repo;
    private final NoteEncryptionService encryption;

    public NoteService(NoteRepository repo, NoteEncryptionService encryption) {
        this.repo = repo;
        this.encryption = encryption;
    }

    @Transactional
    public NoteResponse create(NoteRequest request) {
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContentEncrypted(encryption.encrypt(request.getContent()));

        Note saved = repo.save(note);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public NoteResponse get(Long id) {
        Note note = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));

        return toDto(note);
    }

    @Transactional(readOnly = true)
    public List<NoteResponse> list() {
        return repo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public NoteResponse update(Long id, NoteRequest request) {
        Note note = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found: " + id));

        note.setTitle(request.getTitle());
        note.setContentEncrypted(encryption.encrypt(request.getContent()));

        Note saved = repo.save(note);
        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Note not found: " + id);
        }
        repo.deleteById(id);
    }

    // ------------------ Mapping helper ------------------

    private NoteResponse toDto(Note note) {
        NoteResponse dto = new NoteResponse();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(
                encryption.decrypt(note.getContentEncrypted())
        );
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        return dto;
    }
}
