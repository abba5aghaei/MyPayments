package payments.view;

import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import payments.Main;

public class RootController {
	private Main main;

	public void initialize() {

	}

	public void setMain(Main m) {
		main = m;
	}

	public void handleNew() {
		main.getPaymentData().clear();
		main.setLoad(false);
		main.setPaymentFilePath(null);
		main.getPane().computeTotal();
	}

	public void handleOpen() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(main.getStage());
		if (file != null) {
			main.loadPaymentDataFromFile(file);
		}
		main.getPane().computeTotal();
	}

	public void handleAdd() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(main.getStage());
		if (file != null) {
			main.addPaymentDataFromFile(file);
		}
		main.getPane().computeTotal();
	}

	public void handleSave() {
		if (main.getSave() || main.getLoad()) {
			File personFile = main.getPaymentFilePath();
			if (personFile != null) {
				main.savePaymentDataToFile(personFile);
			} else {
				handleSaveAs();
			}
		} else
			handleSaveAs();
	}

	public void handleSaveAs() {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(main.getStage());
		if (file != null) {
			if (!file.getPath().endsWith(".xml")) {
				file = new File(file.getPath() + ".xml");
			}
			main.setLoad(true);
			main.savePaymentDataToFile(file);
		}
	}

	public void handleAbout() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("حسابان");
		alert.setHeaderText("درباره");
		alert.setContentText("برنامه نویس: سید عباس آقائی \n@abba5aghaei");
		alert.showAndWait();
	}

	public void handleExit() {
		System.exit(0);
	}

	public void handleShowChart() {
		main.showChart();
	}

	public void hanldeSaveDB() {
		if(!main.getLoggined()) {
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("حسابان");
	        alert.setHeaderText("شما هنوز وارد حسابتان نشده اید!");
	        alert.showAndWait();
    	}
    	else {
    		main.getPane().setProgress(0);
    		main.getPane().setProgressVisible(true);
    		main.saveToDB();
    	}
	}

	public void handleLoadDB() {
		if(!main.getLoggined()) {
	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setTitle("حسابان");
	        alert.setHeaderText("شما هنوز وارد حسابتان نشده اید!");
	        alert.showAndWait();
    	}
    	else {
    		main.getPane().setProgressVisible(true);
    		main.getPane().setProgress(0);
    		main.loadFromDB();
    	}
	}

	public void handleLogin() {
		if (main.getLoggined()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("حسابان");
			alert.setHeaderText("شما قبلا وارد حساب شده اید");
			alert.setContentText(
					"در صورتی که می خواهید تغییر حساب بدهید ابتدا از حساب فعلی خارج شوید سپس دوباره سعی کنید.");
			alert.showAndWait();
		} else {
			main.setFirst(false);
			main.showSignup();
		}
	}

	public void handleOut() {
		if (main.getLoggined()) {
			main.setUser(null);
			File userFile = new File("setting.abs");
			userFile.delete();
			main.setLoggined(false);
			main.getPane().setStatus("آفلاین");
			main.getPane().setImage();
			main.getPane().clearName();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("حسابان");
			alert.setHeaderText("از حساب خارج شدید!");
			alert.setContentText("می توانید از برنامه بصورت آفلاین استفاده کنید");
			alert.showAndWait();
		} else {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("حسابان");
			alert.setHeaderText("هنوز وارد هیچ حسابی نشده اید!");
			alert.showAndWait();
		}
	}

	public void handlePrint() {
		main.printData();
	}
}
