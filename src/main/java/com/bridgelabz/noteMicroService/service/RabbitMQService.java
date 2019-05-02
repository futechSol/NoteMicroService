package com.bridgelabz.noteMicroService.service;

import org.springframework.mail.SimpleMailMessage;

import com.bridgelabz.noteMicroService.model.NoteContainer;

public interface RabbitMQService {
	/**
	 * User mail publisher
	 * @param mail
	 */
	void publishUserMail(SimpleMailMessage mail);
	/**
	 * RabbitListner for the user mail
	 * @param mail
	 */
	void recieveUserMail(SimpleMailMessage mail);
	/**
	 * NoteData publisher
	 * @param noteContainer
	 */
	void publishNoteData(NoteContainer noteContainer);
	/**
	 * RabbitListener for the Note Data
	 * @param noteContainer
	 */
	void recieveNoteData(NoteContainer noteContainer);
}
