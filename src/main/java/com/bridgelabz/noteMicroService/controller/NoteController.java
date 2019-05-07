package com.bridgelabz.noteMicroService.controller;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bridgelabz.noteMicroService.dto.NoteDTO;
import com.bridgelabz.noteMicroService.dto.ReminderDTO;
import com.bridgelabz.noteMicroService.exception.NoteException;
import com.bridgelabz.noteMicroService.model.Response;
import com.bridgelabz.noteMicroService.service.NoteService;
import com.bridgelabz.noteMicroService.util.ResponseHelper;

@RestController
@RequestMapping("/notes")
public class NoteController {
	private static final Logger logger = LoggerFactory.getLogger(NoteController.class);
	@Autowired
	private NoteService noteService;

	@PostMapping
	public ResponseEntity<Response> create(@RequestBody NoteDTO noteDTO, @RequestHeader String token) {
		logger.info("NoteDTO : " + noteDTO);
		logger.trace("Note Creation");
		Response response = null;
		if (noteDTO.getTitle().equals("") && noteDTO.getDescription().equals("")) {
			response = ResponseHelper.getResponse(-700, "title and  description both can't be empty", null);
		} else
			response = noteService.create(noteDTO, token);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Response> update(@Valid @RequestBody NoteDTO noteDTO, @RequestHeader String token,
			@PathVariable long id) {
		logger.info("NoteDTO : " + noteDTO);
		logger.trace("Note updating");
		Response response = noteService.update(noteDTO, id, token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Response> delete(@RequestHeader String token, @PathVariable long id) {
		logger.info("Token: " + token);
		logger.info("NoteId : " + id);
		logger.trace("Note Deleting");
		Response response = noteService.delete(id, token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<Object> getAllNotes(@RequestHeader String token) {
		logger.info("token : " + token);
		logger.trace("Geting all notes");
		Object obj = noteService.getAllNotes(token);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getNote(@RequestHeader String token, @PathVariable long id) {
		logger.info("token: " + token);
		logger.trace("Get Note By id");
		Object obj;
		try {
			obj = noteService.getNote(id, token);
			return new ResponseEntity<>(obj, HttpStatus.OK);
		} catch (NoteException e) {
			obj = ResponseHelper.getResponse(e.getErrorCode(), e.getMessage(), null);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PutMapping("/pin/{id}")
	public ResponseEntity<Response> pinNote(@RequestHeader String token, @PathVariable long id) {
		logger.info("token: " + token);
		logger.trace("Pin Note By id");
		Response response = noteService.pinNote(token, id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/trash/{id}")
	public ResponseEntity<Response> trashNote(@RequestHeader String token, @PathVariable long id) {
		logger.info("token: " + token);
		logger.trace("Pin Note By id");
		Response response = noteService.trashNote(token, id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/archive/{id}")
	public ResponseEntity<Response> archiveNote(@RequestHeader String token, @PathVariable long id) {
		logger.info("token: " + token);
		logger.trace("Archive Note By id");
		Response response = noteService.archiveNote(token, id);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/color/{id}")
	public ResponseEntity<Response> colorNote(@RequestHeader String token, @PathVariable long id,
			@RequestParam String color) {
		logger.info("color: " + color);
		logger.trace("Add color to Note By id");
		Response response = noteService.addColor(token, id, color);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/{noteId}/reminder")
	public ResponseEntity<Response> addReminder(@RequestHeader String token, @PathVariable long noteId,
			@RequestBody ReminderDTO reminderDTO) {
		logger.info("token: " + token);
		logger.trace("add reminder to note");
		Response response = noteService.addReminder(token, noteId, reminderDTO);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/{noteId}/reminder")
	public ResponseEntity<Response> removeReminder(@RequestHeader String token, @PathVariable long noteId) {
		logger.info("token: " + token);
		logger.trace("add reminder to note");
		Response response = noteService.removeReminder(token, noteId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/search")
	public ResponseEntity<Object> searchNotes(@RequestParam String queryString, @RequestHeader String token) {
		Object response = noteService.searchNotes(queryString, token);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
