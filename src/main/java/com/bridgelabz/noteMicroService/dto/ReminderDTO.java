package com.bridgelabz.noteMicroService.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor @Getter @Setter @ToString
public class ReminderDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	private String reminder;
	private String repeatReminder;
}
