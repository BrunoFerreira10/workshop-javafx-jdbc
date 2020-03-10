package models.exception;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Map<String, String> errors = new HashMap<String, String>();
	
	public ValidationException(String message) {
		super(message); 
	} 

	public Map<String, String> getErrors(){
		return errors;
	}
	
	public void addError(String fieldName, String errorMessage) {
		 errors.put(fieldName, errorMessage);
	}
	
}
