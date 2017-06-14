package application.view;

import application.MainApp;
import javafx.fxml.FXML;

/**
 * Контроллер для корневого макета. Корневой макет предоставляет базовый
 * макет приложения, содержащий строку меню и место, где будут размещены
 * остальные элементы JavaFX.
 * 
 * @author Alex G
 */
public class RootLayoutController {

	private MainApp mainApp;
	
	/**
     * Вызывается главным приложением, чтобы оставить ссылку на самого себя.
     * 
     * @param mainApp
     */
	public void setMainApp(MainApp mainApp){
		this.mainApp = mainApp;
	}
	
	@FXML
    private void handleExit() {
        System.exit(0);
    }
	
}
