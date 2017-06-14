package application.view;

import application.MainApp;

public class MessageLayoutController {

	private MainApp mainApp;
	
	/**
     * Вызывается главным приложением, чтобы оставить ссылку на самого себя.
     * 
     * @param mainApp
     */
	public void setMainApp(MainApp mainApp){
		this.mainApp = mainApp;
	}
}
