package com.knee.spotifystreamer.bus;

public class DialogMessage {
	private String dialogMessage;
	private String dialogTitle;
	private DialogAction dialogAction;
	
	public DialogMessage(String dialogMessage, String dialogTitle,
						 DialogAction dialogAction) {
		super();
		this.dialogMessage = dialogMessage;
		this.dialogTitle = dialogTitle;
		this.dialogAction = dialogAction;
	}
	public String getDialogMessage() {
		return dialogMessage;
	}
	public String getDialogTitle() {
		return dialogTitle;
	}
	public DialogAction getDialogAction() {
		return dialogAction;
	}
	public void setDialogAction(DialogAction dialogAction) {
		this.dialogAction = dialogAction;
	}
	
	public enum DialogAction{
		START, STOP;
	}
}
