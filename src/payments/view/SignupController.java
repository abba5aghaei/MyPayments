package payments.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import payments.Main;

public class SignupController {
	@FXML
	private TextField usernameField;
	@FXML
	private TextField passwordField;
	@FXML
	private TextField newUserField;
	@FXML
	private TextField newPassField;
	@FXML
	private TextField newPass2Field;
	@FXML
	private TextField emailField;
	@FXML
	private CheckBox check;
	@FXML
	private ProgressIndicator log;
	@FXML
	private ProgressIndicator sig;
	@FXML
	private Label status_log;
	@FXML
	private Label status_sig;
	private String username, password, newuser, newpass, newpass2, email;
	private Main main;

	public void initialize() {
		status_sig.setVisible(false);
		status_log.setVisible(false);
		sig.setVisible(false);
		log.setVisible(false);
	}

	public void setMain(Main m) {
		main = m;
		if(main.getUser() != null) {
			if (main.getUser().getId() != 0)
				usernameField.setText(main.getUser().getUsername());
			if (main.getRemember()) {
				check.setSelected(true);
				passwordField.setText(main.getUser().getPassword());
			}
			else
				check.setSelected(false);
		}
	}

	public void handleLogin() {
		status_log.setVisible(true);
		status_log.setText("اتصال...");
		log.setVisible(true);
		log.setProgress(0);
		username = usernameField.getText();
		password = passwordField.getText();
		if(main.getUser().getId() != 0) {
			if(main.getUser().getUsername().equals(username) && main.getUser().getPassword().equals(password)) {
				if (main.getRemember())
					main.getUser().setRemember(true);
				else
					main.getUser().setRemember(false);
				main.setLoggined(true);
				main.initizaleRoot();
				main.showPane();
				main.setTable("payments_" + main.getUser().getUsername());
				main.saveUser();
				main.getPrimaryStage().close();
			}
			else {
				main.login(username, password);
			}
		}
		else
			main.login(username, password);
	}

	public void handleForgot() {
		status_log.setVisible(true);
		status_log.setText("در حال بررسی...");
		log.setVisible(true);
		log.setProgress(0);
		main.forgot();
	}

	public void handleSignin() {
		status_sig.setVisible(true);
		status_sig.setText("اتصال...");
		sig.setVisible(true);
		sig.setProgress(0);
		newuser = newUserField.getText();
		newpass = newPassField.getText();
		newpass2 = newPass2Field.getText();
		email = emailField.getText();
		if (isValidInput()) {
			main.signIn(newuser, newpass, email);
			newUserField.setText("");
			newPassField.setText("");
			newPass2Field.setText("");
			emailField.setText("");
		}
	}

	private boolean isValidInput() {
		String errorMessage = "";
		if (newuser == null || newuser.length() == 0)
			errorMessage += "نام کاربری صحیح نیست!\n";

		if (newpass == null || newpass.length() == 0)
			errorMessage += "گذر واژه صحیح نیست!\n";

		if (!(email.contains("@")))
			errorMessage += "آدرس ایمیل نامعتبر است!\n";

		if (!(newpass.equals(newpass2)))
			errorMessage += "گذر واژه و تایید آن باهم مطابقت ندارند!\n";

		if (errorMessage.length() == 0) {
			return true;
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(main.getPrimaryStage());
			alert.setTitle("برخی مقادیر غیر معتبر هستند");
			alert.setHeaderText("لطفا مقادیر نامعتبر را تصحبح کنید.");
			alert.setContentText(errorMessage);
			alert.showAndWait();
			return false;
		}
	}

	public void handleCancel() {
		main.getPrimaryStage().close();
		System.exit(0);
	}

	public void handleOffline() {
		main.setLoggined(false);
		main.initizaleRoot();
		main.showPane();
		main.getPrimaryStage().close();
	}
	
	public void CheckRemember() {
		if(check.isSelected())
			main.setRemember(true);
		else if(!check.isSelected())
			main.setRemember(false);
	}
	
	public void setStatusLog(String s) {
		status_log.setText(s);
	}
	
	public void setStatusSig(String s) {
		status_sig.setText(s);
	}
	
	public void increaseLog(double i) {
		log.setProgress(log.getProgress()+i);
	}
	
	public void increaseSig(double j) {
		sig.setProgress(sig.getProgress()+j);
	}
	
	public void clearLog() {
		log.setProgress(0);
		status_log.setText("");
	}
	
	public void clearSig() {
		sig.setProgress(0);
		status_sig.setText("");
	}
}
