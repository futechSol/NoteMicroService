package com.bridgelabz.noteMicroService.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import com.bridgelabz.noteMicroService.model.Note;
import com.bridgelabz.noteMicroService.model.NoteContainer;

@Component
public class RabbitMQServiceImpl implements RabbitMQService {
	@Autowired
	private AmqpTemplate amqpTemplate;
	@Value("${spring.rabbitmq.template.exchange}")
	private String exchange;
	@Value("${spring.rabbitmq.user.routingKey}")
	private String userRoutingKey;
	@Value("${spring.rabbitmq.note.routingKey}")
	private String noteRoutingKey;
	@Autowired
	private MailService mailService;
	@Autowired
	private NoteElasticSearch noteElasticSearch;
	private static final Logger logger = LoggerFactory.getLogger(RabbitMQServiceImpl.class);
	
	@Override
	public void publishUserMail(SimpleMailMessage mail) {
		logger.info("published message = " + mail);
		logger.info("exchange = "+exchange);
		logger.info("routingKey = "+userRoutingKey);
		amqpTemplate.convertAndSend(exchange, userRoutingKey, mail);
	}

	@Override
	@RabbitListener(queues="${spring.rabbitmq.user.queue}")
	public void recieveUserMail(SimpleMailMessage mail) {
		logger.info("consumed message = "+ mail.toString());
		mailService.sendEmail(mail);
	}
	
	@Override
	public void publishNoteData(NoteContainer noteContainer) {
		logger.info("published message = " + noteContainer);
		logger.info("exchange = "+exchange);
		logger.info("routingKey = "+userRoutingKey);
		amqpTemplate.convertAndSend(exchange, noteRoutingKey, noteContainer);
	}
	
	@Override
	@RabbitListener(queues = "${spring.rabbitmq.note.queue}")
	public void recieveNoteData(NoteContainer noteContainer) {
		logger.info("Note operation : " + noteContainer.getNoteOperation());
		Note note = noteContainer.getNote(); 
		switch(noteContainer.getNoteOperation()) 
		{
			case CREATE : noteElasticSearch.insertNote(note);
			break;
			case UPDATE : noteElasticSearch.updateNoteById(note);
			break;
			case DELETE : noteElasticSearch.deleteNoteById(String.valueOf(note.getId()));
			break;
		}
	}
}
