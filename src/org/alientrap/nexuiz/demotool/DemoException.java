package org.alientrap.nexuiz.demotool;

public class DemoException extends Exception {

	private static final long serialVersionUID = 1L;
	private int errorCode;
	
	public DemoException(int _errorCode) {
		super();
		errorCode = _errorCode;
	}
	
	public DemoException(String _message, int _errorCode){
		super(_message);
		errorCode = _errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public String toString() {
		if (getMessage() == null) {
			return new String("Error: " + errorCode + ": " + getText());
		}
		return new String("Error: " + errorCode + ": " + getText() + " " + getMessage());
	}
	
	public String getText() {
		switch (errorCode) {
		case 10: 
			return "Packet too large:";
		case 11:
			return "Could not read enough data";
		case 12:
			return "Got a bad packet:";
		case 20:
			return "Cdtrack isn't defined";
		case 21:
			return "No packets for this demo";
		}
		
		return "";
	}

}
