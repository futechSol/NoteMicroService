package com.bridgelabz.noteMicroService.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bridgelabz.noteMicroService.model.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
	  Optional<Note> findByIdAndUserId(long id, long userId); 
	  List<Note> findAllByUserId(long userId);
	 
}
