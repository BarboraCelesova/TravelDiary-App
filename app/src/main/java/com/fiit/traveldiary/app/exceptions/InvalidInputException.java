package com.fiit.traveldiary.app.exceptions;

/**
 * Created by Jakub Dubec on 14/04/16.
 */
public class InvalidInputException extends BaseException {
	public InvalidInputException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
