package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import application.view.MessageLayoutController;
import application.view.RootLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private int reminderHour;
	private int reminderMin;
	
	// application stage is stored so that it can be shown and hidden based on system tray icon operations.
    //private Stage stage;

    // a timer allowing the tray icon to provide a periodic notification event.
    private Timer notificationTimer = new Timer();

    // format used to display the current time in a tray icon notification.
    private DateFormat timeFormat = SimpleDateFormat.getTimeInstance();

	
	//Constructor
	public MainApp(){
		//Определяем файл
	    File file = new File(getFilePath() + "\\properties.txt");	    
	    getReminderTime(file);	 
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Fill time sheet");
		
		// instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);
        
        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
		
		//устанавливаем иконку для приложения
		this.primaryStage.getIcons().add(new Image("file:resources/images/clock.png"));
		
		initRootLayout();	
		
		showMessage();
	}
	
	/**
	 * Инициализирует корневой макет 
	 */
	public void initRootLayout(){
		try{
			// Загружаем корневой макет из fxml файла.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			// Отображаем сцену, содержащую корневой макет.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			// Даём контроллеру доступ к главному прилодению.
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			
			primaryStage.show();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void showMessage(){
		try{
			// Загружаем сведения об адресатах.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MessageLayout.fxml"));
			AnchorPane messageLayout = (AnchorPane) loader.load();
			
			// Помещаем сведения об адресатах в центр корневого макета.
			rootLayout.setCenter(messageLayout);
			
			// Даём контроллеру доступ к главному приложению.
			MessageLayoutController controller = loader.getController();
			controller.setMainApp(this);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public String getFilePath(){
			String myJarPath = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String dirPath = new File(myJarPath).getParent();
			//System.out.println(dirPath);
			return dirPath;
		
	}
	
	public void getReminderTime(File file){
		try {
	        //проверяем, что если файл не существует то создаем его
	        if(!file.exists()){
	            file.createNewFile();
	        	        
		        //PrintWriter обеспечит возможности записи в файл
		        PrintWriter out = new PrintWriter(file.getAbsoluteFile());
		 
		        try {
		            //Записываем текст у файл
		            out.print("TIME=18:00");
		        } finally {
		            //После чего мы должны закрыть файл
		            //Иначе файл не запишется
		            out.close();
		        }
		        
		        reminderHour = 18;
		        reminderMin = 0;
	        }else{
	        	BufferedReader in = new BufferedReader(new FileReader( file.getAbsoluteFile()));
	            try {
	                //В цикле построчно считываем файл
	                String s = in.readLine();
	                
	                reminderHour = Integer.parseInt(s.substring(5, 7));
			        reminderMin = Integer.parseInt(s.substring(8, 10));
			        
	            } finally {
	                //Также не забываем закрыть файл
	                in.close();
	            }
	        }
	        
	        
	    } catch(IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	public void saveReminderTime(File file, String hour, String min){
		try {
	        //проверяем, что если файл не существует то создаем его
	        if(!file.exists()){
	            file.createNewFile();
	        }
	 
	        //PrintWriter обеспечит возможности записи в файл
	        PrintWriter out = new PrintWriter(file.getAbsoluteFile());
	 
	        try {
	            //Записываем текст у файл
	            out.print("TIME="+hour+":"+min);
	        } finally {
	            //После чего мы должны закрыть файл
	            //Иначе файл не запишется
	            out.close();
	        }
	    } catch(IOException e) {
	        throw new RuntimeException(e);
	    }
	}
	
	/**
     * Sets up a system tray icon for the application.
     */
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            
            File file = new File(getFilePath() + "\\resources\\images\\small_clock.png");
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(ImageIO.read(file));

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem("hello, world");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
                notificationTimer.cancel();
                Platform.exit();
                tray.remove(trayIcon);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // create a timer which periodically displays a notification message.
            notificationTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            javax.swing.SwingUtilities.invokeLater(() ->
                                trayIcon.displayMessage(
                                        "Remember",
                                        "Fill time sheets!",
                                        java.awt.TrayIcon.MessageType.INFO
                                )
                            );
                        }
                    },
                    5_000,
                    3600_000
            );

            // add the application tray icon to the system tray.
            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        if (primaryStage != null) {
        	primaryStage.show();
        	primaryStage.toFront();
        }
    }
    
	
	public static void main(String[] args) {
		launch(args);
	}
}
