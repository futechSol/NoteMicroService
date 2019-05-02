package com.bridgelabz.noteMicroService.service;

import java.util.List;
import java.util.Map;
import org.elasticsearch.action.DocWriteResponse.Result;
import com.bridgelabz.noteMicroService.model.Note;

public interface NoteElasticSearch {
	Result insertNote(Note note);

	Map<String, Object> updateNoteById(Note note);

	Result deleteNoteById(String id);

	Map<String, Object> getNoteById(String id);

	List<Note> searchNoteByUserId(String userId);

	List<Note> searchNoteByTitle(String title);

	List<Note> searchNoteByAnyText(String queryString, long userId);
}
