package com.bridgelabz.noteMicroService.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import com.bridgelabz.noteMicroService.dto.NoteDTO;
import com.bridgelabz.noteMicroService.dto.ReminderDTO;
import com.bridgelabz.noteMicroService.exception.NoteException;
import com.bridgelabz.noteMicroService.model.Note;
import com.bridgelabz.noteMicroService.model.NoteComparator;
import com.bridgelabz.noteMicroService.model.NoteContainer;
import com.bridgelabz.noteMicroService.model.NoteOperation;
import com.bridgelabz.noteMicroService.model.Response;
import com.bridgelabz.noteMicroService.repository.NoteRepository;
import com.bridgelabz.noteMicroService.util.ResponseHelper;
import com.bridgelabz.noteMicroService.util.TokenGenerator;

@Service
@PropertySource("classpath:status.properties")
public class NoteServiceImpl implements NoteService {
	@Autowired
	private NoteRepository noteRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private Environment environment;
	@Autowired
	private RabbitMQService rabbitMQService;
	@Autowired
	private NoteElasticSearch noteElasticSearch;
	@Autowired
	private TokenGenerator tokenGenerator;
	private NoteContainer noteContainer;
	private Response response;

	@Override
	public Response create(NoteDTO noteDTO, String userToken) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Note note = modelMapper.map(noteDTO, Note.class);
		note.setColor("white");
		note.setUserId(userId);
		note.setCreatedDate(LocalDateTime.now());
		note.setModifiedDate(LocalDateTime.now());
		note = noteRepository.save(note);

		if (note == null) {
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.errorMessage"), null);
		} else {
			noteContainer = new NoteContainer();
			noteContainer.setNote(note);
			noteContainer.setNoteOperation(NoteOperation.CREATE);
			rabbitMQService.publishNoteData(noteContainer);
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.success.code")),
					environment.getProperty("status.note.create.success"), null);
		}
		return response;
	}

	@Override
	public Response update(NoteDTO noteDTO, long noteId, String userToken) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Note noteToUpdate = null;
		Optional<Note> opNote = noteRepository.findByIdAndUserId(noteId, userId);
		if (opNote.isPresent())
			noteToUpdate = opNote.get();
		else
			return response = ResponseHelper.getResponse(
					Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.exists.error"), null);

		if (!(noteDTO.getTitle() == null || noteDTO.getTitle().equals("")))
			noteToUpdate.setTitle(noteDTO.getTitle());
		if (!(noteDTO.getDescription() == null || noteDTO.getDescription().equals("")))
			noteToUpdate.setDescription(noteDTO.getDescription());
		noteToUpdate.setModifiedDate(LocalDateTime.now());
		noteToUpdate = noteRepository.save(noteToUpdate);
		if (noteToUpdate == null)
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.update.error"), null);
		else {
			noteContainer = new NoteContainer();
			noteContainer.setNote(noteToUpdate);
			noteContainer.setNoteOperation(NoteOperation.UPDATE);
			rabbitMQService.publishNoteData(noteContainer);
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.success.code")),
					environment.getProperty("status.note.update.success"), null);
		}
		return response;
	}

	@Override
	public Response delete(long noteId, String userToken) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Optional<Note> opNote = noteRepository.findByIdAndUserId(noteId, userId);
		if (!opNote.isPresent())
			return response = ResponseHelper.getResponse(
					Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.exists.error"), null);
		noteRepository.deleteById(noteId);
		if (noteRepository.findByIdAndUserId(noteId, userId).isPresent())
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.delete.error"), null);
		else {
			noteContainer = new NoteContainer();
			noteContainer.setNote(opNote.get());
			noteContainer.setNoteOperation(NoteOperation.DELETE);
			rabbitMQService.publishNoteData(noteContainer);

			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.success.code")),
					environment.getProperty("status.note.delete.success"), null);
		}
		return response;
	}

	@Override
	public Object getNote(long noteId, String userToken) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Optional<Note> note = noteRepository.findByIdAndUserId(noteId, userId);
		if (!note.isPresent())
			throw new NoteException(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.exists.error"));
		return noteElasticSearch.getNoteById(String.valueOf(noteId));
	}

	@Override
	public List<Note> getAllNotes(String userToken) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		List<Note> allNotes = noteElasticSearch.searchNoteByUserId(String.valueOf(userId));
		Collections.sort(allNotes, new NoteComparator());
		// noteRepository.findAllByUserId(userId).stream()
		//.filter(u -> u.getUserId() == userId && !(u.isArchived() || u.isTrashed())).collect(Collectors.toList());
		return allNotes;
	}

	@Override
	public Response pinNote(String userToken, long noteId) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Optional<Note> opNote = noteRepository.findByIdAndUserId(noteId, userId);
		if (!opNote.isPresent())
			return ResponseHelper.getResponse(
					Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.exists.error"), null);
		Note note = opNote.get();
		note.setPinned(!note.isPinned());
		note = noteRepository.save(note);
		if (note == null)
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.pinned.error"), null);
		else {
			noteContainer = new NoteContainer();
			noteContainer.setNote(note);
			noteContainer.setNoteOperation(NoteOperation.UPDATE);
			rabbitMQService.publishNoteData(noteContainer);
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.success.code")),
					environment.getProperty("status.note.pinned.success"), null);
		}
		return response;
	}

	@Override
	public Response trashNote(String userToken, long noteId) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Optional<Note> opNote = noteRepository.findByIdAndUserId(noteId, userId);
		if (!opNote.isPresent())
			return response = ResponseHelper.getResponse(
					Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.exists.error"), null);
		Note note = opNote.get();
		note.setTrashed(!note.isTrashed());
		note = noteRepository.save(note);
		if (note == null)
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.trashed.error"), null);
		else {
			noteContainer = new NoteContainer();
			noteContainer.setNote(note);
			noteContainer.setNoteOperation(NoteOperation.UPDATE);
			rabbitMQService.publishNoteData(noteContainer);
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.success.code")),
					environment.getProperty("status.note.trashed.success"), null);
		}
		return response;
	}

	@Override
	public Response archiveNote(String userToken, long noteId) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Optional<Note> opNote = noteRepository.findByIdAndUserId(noteId, userId);
		if (!opNote.isPresent())
			return response = ResponseHelper.getResponse(
					Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.exists.error"), null);
		Note note = opNote.get();
		note.setArchived(!note.isArchived());
		note = noteRepository.save(note);
		if (note == null)
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.archived.error"), null);
		else {
			noteContainer = new NoteContainer();
			noteContainer.setNote(note);
			noteContainer.setNoteOperation(NoteOperation.UPDATE);
			rabbitMQService.publishNoteData(noteContainer);
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.success.code")),
					environment.getProperty("status.note.archived.success"), null);
		}
		return response;
	}

	@Override
	public Response addColor(String userToken, long noteId, String color) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Optional<Note> opNote = noteRepository.findByIdAndUserId(noteId, userId);
		if (!opNote.isPresent())
			return response = ResponseHelper.getResponse(
					Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.exists.error"), null);
		Note note = opNote.get();
		note.setColor(color);
		note = noteRepository.save(note);
		if (note == null)
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.color.error"), null);
		else {
			noteContainer = new NoteContainer();
			noteContainer.setNote(note);
			noteContainer.setNoteOperation(NoteOperation.UPDATE);
			rabbitMQService.publishNoteData(noteContainer);
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.success.code")),
					environment.getProperty("status.note.color.success"), null);
		}
		return response;
	}

	@Override
	public Response addReminder(String userToken, long noteId, ReminderDTO reminderDTO) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Optional<Note> opNote = noteRepository.findByIdAndUserId(noteId, userId);
		if (!opNote.isPresent())
			return response = ResponseHelper.getResponse(
					Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.exists.error"), null);
		Note note = opNote.get();
		note.setReminder(reminderDTO.getReminder());
		note.setRepeatReminder(reminderDTO.getRepeatReminder());
		note = noteRepository.save(note);
		if (note == null)
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.update.error"), null);
		else {
			noteContainer = new NoteContainer();
			noteContainer.setNote(note);
			noteContainer.setNoteOperation(NoteOperation.UPDATE);
			rabbitMQService.publishNoteData(noteContainer);
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.success.code")),
					environment.getProperty("status.note.addReminder.success"), null);
		}
		return response;
	}

	public Response removeReminder(String userToken, long noteId) {
		long userId = tokenGenerator.retrieveIdFromToken(userToken);
		Optional<Note> opNote = noteRepository.findByIdAndUserId(noteId, userId);
		if (!opNote.isPresent())
			return response = ResponseHelper.getResponse(
					Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.exists.error"), null);
		Note note = opNote.get();
		note.setReminder(null);
		note.setRepeatReminder(null);
		note = noteRepository.save(note);
		if (note == null)
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.note.errorCode")),
					environment.getProperty("status.note.update.error"), null);
		else {
			noteContainer = new NoteContainer();
			noteContainer.setNote(note);
			noteContainer.setNoteOperation(NoteOperation.UPDATE);
			rabbitMQService.publishNoteData(noteContainer);
			response = ResponseHelper.getResponse(Integer.parseInt(environment.getProperty("status.success.code")),
					environment.getProperty("status.note.removeReminder.success"), null);
		}
		return response;
	}

	public List<Note> searchNotes(String query, String token) {
		long userId = tokenGenerator.retrieveIdFromToken(token);
		return noteElasticSearch.searchNoteByAnyText(query, userId);
	}
	 
}
