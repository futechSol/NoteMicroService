package com.bridgelabz.noteMicroService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bridgelabz.noteMicroService.model.Response;
import com.bridgelabz.noteMicroService.util.ResponseHelper;

@RestControllerAdvice
public class NotesExceptionHandler {
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> GlobalExceptionHandler(Exception e)
	{
		Response response = ResponseHelper.getResponse( -200, "Internal Error", null);
		return new ResponseEntity<>(response,HttpStatus.OK);	
	}
	
	@ExceptionHandler(NoteException.class)
	public ResponseEntity<Response> noteExceptionHandler(NoteException e) {
		Response statusInfo = ResponseHelper.getResponse(e.getErrorCode(), e.getMessage(), null);
		return new ResponseEntity<>(statusInfo, HttpStatus.OK);
	}
	
	@ExceptionHandler(TokenException.class)
	public ResponseEntity<Response> tokenExceptionHandler(TokenException e) {
		Response statusInfo = ResponseHelper.getResponse(e.getErrorCode(), e.getMessage(), null);
		return new ResponseEntity<>(statusInfo, HttpStatus.OK);
	}
}
