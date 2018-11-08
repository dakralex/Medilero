package com.github.dakralex.medilero;

import com.github.dakralex.medilero.connection.MediolaConnection;
import com.github.dakralex.medilero.exception.InvalidPasswordException;
import com.github.dakralex.medilero.exception.NotMediolaException;
import com.github.dakralex.medilero.exception.UnsuccessfulResponseException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class MedileroController {

	private static final Logger logger = LogManager.getFormatterLogger();

	private MediolaConnection mediolaConnection;

	private boolean learning;
	private int channelSelected;

	private JsonArray channelStates;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button buttonConnect;

	@FXML
	private Button buttonLearnStart;

	@FXML
	private Button buttonLearnStop;

	@FXML
	private Button buttonLearnDown;

	@FXML
	private Button buttonLearnUp;

	@FXML
	private Button buttonTestDown;

	@FXML
	private Button buttonTestUp;

	@FXML
	private Button buttonTestStop;

	@FXML
	private CheckBox checkUsePassword;

	@FXML
	private ComboBox<Integer> comboChannel;

	@FXML
	private TextField fieldEid;

	@FXML
	private TextField fieldState;

	@FXML
	private TextField fieldAddress;

	@FXML
	private PasswordField fieldPassword;

	@FXML
	private AnchorPane paneStep1;

	@FXML
	private AnchorPane paneStep2;

	@FXML
	private AnchorPane paneStep3;

	@FXML
	private AnchorPane paneExtra;

	@FXML
	void tryToConnect (ActionEvent event) {
		String address = fieldAddress.getText();
		String password = fieldPassword.getText();

		if (checkUsePassword.isSelected()) {
			mediolaConnection = new MediolaConnection(address, password);
		} else {
			mediolaConnection = new MediolaConnection(address);
		}

		paneStep1.setDisable(true);
		paneStep2.setDisable(true);
		paneStep3.setDisable(true);
		paneExtra.setDisable(true);

		fieldAddress.setDisable(true);
		buttonConnect.setDisable(true);
		if (checkUsePassword.isSelected()) {
			fieldPassword.setDisable(true);
		}
		checkUsePassword.setDisable(true);

		fieldAddress.getStyleClass().removeAll(Collections.singleton("error"));
		fieldPassword.getStyleClass().removeAll(Collections.singleton("error"));

		try {
			logger.info("Es wird versucht eine Verbindung zur Mediola aufzubauen.");
			if (mediolaConnection.checkConnection()) {
				logger.info("Die Verbindung zur Mediola wurde erfolgreich hergestellt.");
				paneStep1.setDisable(false);
				paneExtra.setDisable(false);
			}

			try {
				JsonElement responeElement = mediolaConnection.getSuccessfulJsonResponse("XC_FNC=GetSI", "Frage die Elero ID ab");
				fieldEid.setText(responeElement.getAsJsonObject().get("EID").getAsString());
			} catch (UnsuccessfulResponseException e) {
				logger.warn("Die Elero-ID konnte nicht abgefragt werden.");
			}

		} catch (NotMediolaException e) {
			logger.error("Die angegebene Addresse \"%s\" ist keine Mediola.", address);

			fieldAddress.getStyleClass().add("error");
		} catch (InvalidPasswordException e) {
			if (checkUsePassword.isSelected()) {
				logger.error("Das angegebene Passwort ist falsch.");
			} else {
				logger.error("Für diese Mediola wird ein Passwort benötigt.");
				checkUsePassword.setSelected(true);
			}

			fieldPassword.getStyleClass().add("error");
		}

		fieldAddress.setDisable(false);
		buttonConnect.setDisable(false);
		if (checkUsePassword.isSelected()) {
			fieldPassword.setDisable(false);
		}
		checkUsePassword.setDisable(false);
	}

	@FXML
	void channelSelected (ActionEvent event) {
		if (comboChannel.getValue() != null) {
			channelSelected = comboChannel.getValue();
			logger.info("Der Kanal " + getPaddedHex(channelSelected) + " wurde ausgewählt.");
			buttonLearnStart.setDisable(false);
		} else {
			buttonLearnStart.setDisable(true);
		}
	}

	@FXML
	void sendCodeDown (ActionEvent event) {
		mediolaConnection.checkSuccess("XC_FNC=SendSC&type=ER&data=" + getPaddedHex(channelSelected) + "00",
				"Sende Code zum Runterfahren des Kanals " + getPaddedHex(channelSelected));
	}

	@FXML
	void sendCodeUp (ActionEvent event) {
		mediolaConnection.checkSuccess("XC_FNC=SendSC&type=ER&data=" + getPaddedHex(channelSelected) + "01",
				"Sende Code zum Hinauffahren des Kanals " + getPaddedHex(channelSelected));
	}

	@FXML
	void sendCodeStop (ActionEvent event) {
		mediolaConnection.checkSuccess("XC_FNC=SendSC&type=ER&data=" + getPaddedHex(channelSelected) + "02",
				"Sende Code zum Stoppen des Kanals " + getPaddedHex(channelSelected));
	}

	@FXML
	void startLearningProcess (ActionEvent event) {
		learning = true;
		comboChannel.setDisable(true);
		buttonLearnStart.setDisable(true);

		if (mediolaConnection.checkSuccess(
				"XC_FNC=learnSC&type=ER&adr=" + getPaddedHex(channelSelected), "Starte Lernprozess")) {
			logger.info("Der Lernprozess wurde erfolgreich gestartet.");
			paneStep2.setDisable(false);
			paneStep3.setDisable(false);
		} else {
			logger.error("Der Lernprozess konnte nicht gestartet werden. Es wird versucht ihn zu stoppen, versuchen Sie es dann erneut.");
			mediolaConnection.checkSuccess(
					"XC_FNC=stopLearn&type=ER&adr=" + getPaddedHex(channelSelected), "Beende Lernprozess");
			paneStep2.setDisable(true);
			paneStep3.setDisable(true);
			comboChannel.setDisable(false);
			buttonLearnStart.setDisable(false);
		}
	}

	@FXML
	void stopLearningProcess (ActionEvent event) {
		learning = false;
		buttonLearnStop.setDisable(true);

		if (mediolaConnection.checkSuccess(
				"XC_FNC=stopLearn&type=ER&adr=" + getPaddedHex(channelSelected), "Beende Lernprozess")) {
			logger.info("Der Lernprozess wurde erfolgreich beendet.");
			paneStep2.setDisable(true);
			paneStep3.setDisable(true);
			comboChannel.setDisable(false);
			buttonLearnStart.setDisable(false);
		} else {
			logger.error("Der Lernprozess konnte nicht beendet werden.");
			paneStep2.setDisable(false);
			paneStep3.setDisable(false);
		}

		buttonLearnStop.setDisable(false);
	}

	@FXML
	void togglePasswordField (ActionEvent event) {
		if (fieldPassword.isDisabled()) {
			fieldPassword.setDisable(false);
		} else {
			fieldPassword.setDisable(true);
		}
	}

	@FXML
	void initialize () {
		mediolaConnection = new MediolaConnection("0.0.0.0");

		logger.info("%s wurde erfolgreich gestartet!", Medilero.APPLICATION_NAME);
	}

	private String getPaddedHex (int number) {
		return String.format("%1$" + 2 + "s", Integer.toHexString(number)).replace(' ', '0').toUpperCase();
	}

	public MediolaConnection getMediolaConnection () {
		return mediolaConnection;
	}

	public boolean isLearning () {
		return learning;
	}

	public int getChannelSelected () {
		return channelSelected;
	}

	public Button getButtonConnect () {
		return buttonConnect;
	}

	public Button getButtonLearnStart () {
		return buttonLearnStart;
	}

	public Button getButtonLearnStop () {
		return buttonLearnStop;
	}

	public Button getButtonLearnUp () {
		return buttonLearnUp;
	}

	public Button getButtonLearnDown () {
		return buttonLearnDown;
	}

	public Button getButtonTestUp () {
		return buttonTestUp;
	}

	public Button getButtonTestDown () {
		return buttonTestDown;
	}

	public Button getButtonTestStop () {
		return buttonTestStop;
	}

	public CheckBox getCheckUsePassword () {
		return checkUsePassword;
	}

	public ComboBox<Integer> getComboChannel () {
		return comboChannel;
	}

	public TextField getFieldEid () {
		return fieldEid;
	}

	public TextField getFieldState () {
		return fieldState;
	}

	public TextField getFieldAddress () {
		return fieldAddress;
	}

	public PasswordField getFieldPassword () {
		return fieldPassword;
	}

	public AnchorPane getPaneStep1 () {
		return paneStep1;
	}

	public AnchorPane getPaneStep2 () {
		return paneStep2;
	}

	public AnchorPane getPaneStep3 () {
		return paneStep3;
	}
}
