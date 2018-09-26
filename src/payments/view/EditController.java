package payments.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import payments.Main;
import payments.model.Payment;

public class EditController {
	@FXML
	private TextField payforField;
	@FXML
	private TextField priceField;
	@FXML
	private TextField dateField;
	@FXML
	private TextField commentField;
	private Payment payment;
	private boolean okClicked = false;
	private Main main;

	public void setMain(Main m) {
		main = m;
	}

	public void initialize() {

	}

	public void setPayment(Payment payment) {
		this.payment = payment;
		payforField.setText(payment.getPayfor());
		priceField.setText(payment.getPrice());
		dateField.setText(payment.getDate());
		commentField.setText(payment.getComment());
	}

	public boolean isOkClicked() {
		return okClicked;
	}

	public void handleOk() {
		if (isInputValid()) {
			payment.setPayfor(payforField.getText());
			payment.setPrice(priceField.getText());
			payment.setComment(commentField.getText());
			payment.setDate(dateField.getText());
			okClicked = true;
			main.setSave(false);
			main.getEditStage().close();
		}
	}

	public void handleCancel() {
		main.getEditStage().close();
	}

	private boolean isInputValid() {
		String errorMessage = "";

		if (payforField.getText() == null || payforField.getText().length() == 0) {
			errorMessage += "بابت پرداخت غیر معتبر است!\n";
		}
		if (priceField.getText() == null || priceField.getText().length() == 0) {
			errorMessage += "مبلغ پرداخت غیر معتبر است!\n";
		}
		try {
			Integer.parseInt(priceField.getText());
		} catch (Exception e) {
			errorMessage += "مبلغ پرداخت غیر معتبر است!\n";
		}
		try {
			payment.Date().getValue().parse(dateField.getText());
		} catch (Exception e) {
			errorMessage += "تاریخ غیر معتبر است!\n";
		}
		if (errorMessage.length() == 0) {
			return true;
		} else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(main.getEditStage());
			alert.setTitle("برخی مقادیر غیر معتبر هستند");
			alert.setHeaderText("لطفا مقادیر نامعتبر را تصحبح کنید.");
			alert.setContentText(errorMessage);
			alert.showAndWait();
			return false;
		}
	}
}
