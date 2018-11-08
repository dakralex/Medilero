package com.github.dakralex.medilero;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Medilero extends Application {

	//private static final String ICON_FILE = "mediola.png";
	public static final String APPLICATION_NAME = "Medilero v1.0.0";
	public static final int WINDOW_WIDTH = 400;
	public static final int WINDOW_HEIGHT = 800;
	private static final Logger logger = LogManager.getFormatterLogger();
	private static final String FXML_FILE = "MedileroMain.fxml";
	private MedileroController medileroController;

	public static void main (String[] args) {
		launch(args);
	}

	@Override
	public void start (Stage stage) {
		logger.info("Starte %s...", APPLICATION_NAME);

		medileroController = new MedileroController();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle (WindowEvent event) {
				if (medileroController.isLearning()) {
					medileroController.stopLearningProcess(new ActionEvent(stage, medileroController.getButtonLearnStop()));
				}

				Platform.exit();
				System.exit(0);
			}
		});

		try {
			logger.debug("Lade FXML-Datei für Medilero...");
			FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(FXML_FILE));
			fxmlLoader.setController(medileroController);
			fxmlLoader.load();

			stage.setTitle(APPLICATION_NAME);
			stage.setScene(new Scene(fxmlLoader.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT));
			//stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream(ICON_FILE)));
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			logger.error("Die Layoutdatei %s konnte nicht geladen werden!: %s", FXML_FILE, e);
			stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
		}
	}

	public MedileroController getMedileroController () {
		return medileroController;
	}
}
