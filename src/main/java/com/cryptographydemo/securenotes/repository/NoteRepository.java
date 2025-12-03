package com.cryptographydemo.securenotes.repository;

import com.cryptographydemo.securenotes.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
