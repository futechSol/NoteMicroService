package com.bridgelabz.noteMicroService.service;

import java.util.List;
import com.bridgelabz.noteMicroService.dto.NoteDTO;
import com.bridgelabz.noteMicroService.dto.ReminderDTO;
import com.bridgelabz.noteMicroService.model.Note;
import com.bridgelabz.noteMicroService.model.Response;

public interface NoteService {
	Response create(NoteDTO noteDTO, String userToken);

	Response update(NoteDTO noteDTO, long noteId, String userToken);

	Response delete(long noteId, String userToken);

	Object getNote(long noteId, String userToken);

	List<Note> getAllNotes(String userToken);

	Response archiveNote(String userToken, long noteId);

	Response pinNote(String userToken, long noteId);

	Response trashNote(String userToken, long noteId);

	Response addReminder(String userToken, long noteId, ReminderDTO reminderDTO);
	
	Response removeReminder(String userToken, long noteId);

	Response addColor(String userToken, long noteId, String color);

	List<Note> searchNotes(String query, String token);
}
