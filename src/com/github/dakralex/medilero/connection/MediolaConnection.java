package com.github.dakralex.medilero.connection;

import com.github.dakralex.medilero.exception.InvalidPasswordException;
import com.github.dakralex.medilero.exception.NotMediolaException;
import com.github.dakralex.medilero.exception.UnsuccessfulResponseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MediolaConnection {

	private static final Logger logger = LogManager.getFormatterLogger();

	private String address;
	private String password;
	private boolean passwordHashed;

	public MediolaConnection (String address) {
		this(address, null);
	}

	public MediolaConnection (String address, String password) {
		setAddress(address);
		setPassword(password);
	}

	public boolean checkConnection () throws NotMediolaException, InvalidPasswordException {
		JsonElement responseElement = getJsonResponse("", "Überprüfe Verbindung").get("XC_ERR");

		if (responseElement != null && responseElement.isJsonObject()) {
			String responseCode = "";
			JsonObject responseObject = responseElement.getAsJsonObject();
			if (responseObject.get("code") != null) {
				responseCode = responseObject.get("code").getAsString();
			} else {
				responseCode = responseObject.get("CODE").getAsString();
			}

			if (responseCode.contentEquals("010000")) {
				return true;
			} else {
				throw new InvalidPasswordException();
			}
		} else {
			throw new NotMediolaException();
		}
	}

	/**
	 * @param responseElement XC_SUC JsonElement
	 *
	 * @return
	 */
	public boolean checkSuccess (JsonElement responseElement) {
		if (responseElement != null) {
			return responseElement.isJsonObject() || responseElement.isJsonArray();
		} else {
			return false;
		}
	}

	public boolean checkSuccess (String command, String debugMessage) {
		JsonElement responseElement = null;
		try {
			responseElement = getJsonResponse(command, debugMessage).get("XC_SUC");
		} catch (NotMediolaException e) {
			logger.error("Die angegebene Addresse \"%s\" ist keine Mediola.", address);
		}

		return checkSuccess(responseElement);
	}

	public JsonElement getSuccessfulJsonResponse (String command,
	                                              String debugMessage) throws UnsuccessfulResponseException {
		JsonElement responseElement = null;
		try {
			responseElement = getJsonResponse(command, debugMessage).get("XC_SUC");
		} catch (NotMediolaException e) {
			logger.error("Die angegebene Addresse \"%s\" ist keine Mediola.", address);
		}

		if (responseElement != null) {
			return responseElement;
		} else {
			throw new UnsuccessfulResponseException();
		}
	}

	private JsonObject getJsonResponse (String command, String debugMessage) throws NotMediolaException {
		String requestString = "http://" + this.address + "/cmd?";

		if (this.password != null) {
			if (this.passwordHashed) {
				requestString += "at=";
			} else {
				requestString += "auth=";
			}
			requestString += this.password;
		}

		if (command != null && !command.isEmpty()) {
			requestString += "&" + command;
		}

		logger.debug("%s... Anfrage: %s", debugMessage, requestString);

		URLConnection requestConnection;
		JsonElement jsonElement = null;

		try {
			URL requestUrl = new URL(requestString);
			requestConnection = requestUrl.openConnection();
			requestConnection.connect();
			JsonParser jsonParser = new JsonParser();
			jsonElement = jsonParser.parse(new InputStreamReader(requestConnection.getInputStream()));
		} catch (FileNotFoundException e) {
			throw new NotMediolaException();
		} catch (IOException e) {
			logger.error("Es konnte keine gültige Verbindung zu \"%s\" hergestellt werden: %s", this.address, e);
		}

		if (jsonElement != null) {
			return jsonElement.getAsJsonObject();
		} else {
			return null;
		}
	}

	public boolean isPasswordHashed () {
		return passwordHashed;
	}

	public String getAddress () {
		return address;
	}

	public void setAddress (String address) {
		this.address = address;
	}

	public String getPassword () {
		return password;
	}

	public void setPassword (String password) {
		if (password != null) {
			String tmpPassword = password;
			try {
				tmpPassword = URLDecoder.decode(tmpPassword, "UTF-8");
				tmpPassword = URLEncoder.encode(tmpPassword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				logger.warn("Verwende kein Unescape und Encoding, da es nicht unterstützt wird: %s", e);
			}

			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				tmpPassword += "_SALT!";
				byte[] tmpPasswordDigest = messageDigest.digest(tmpPassword.getBytes(StandardCharsets.UTF_8));
				String hashedPassword = DatatypeConverter.printHexBinary(tmpPasswordDigest).toLowerCase();
				this.passwordHashed = true;
				this.password = hashedPassword;
			} catch (NoSuchAlgorithmException e) {
				logger.warn("Das Passwort wird in Klartext gesendet, da MD5 nicht unterstützt wird: %s", e);
				this.passwordHashed = false;
				this.password = password;
			}
		}
	}
}
