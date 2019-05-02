package com.bridgelabz.noteMicroService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Response {
	private int statusCode;
	private String statusMessage;	
	private String token;
}
