package payments.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;
import payments.Main;
import payments.model.Payment;
import payments.model.PersianDate;

public class PaneController {
	private Main main;
	@FXML
	private CheckBox sizeCheck;
	@FXML
	private TableView<Payment> table;
	@FXML
	private TableColumn<Payment, String> payforColumn;
	@FXML
	private TableColumn<Payment, String> priceColumn;
	@FXML
	private TableColumn<Payment, PersianDate> dateColumn;
	@FXML
	private TableColumn<Payment, String> commentColumn;
	@FXML
	private Label payforLabel;
	@FXML
	private Label priceLabel;
	@FXML
	private Label dateLabel;
	@FXML
	private Label commentLabel;
	@FXML
	private Label total;
	@FXML
	private Label name;
	@FXML
	private Label status;
	@FXML
	private DatePicker datep;
	@FXML
	private ProgressBar bar;
	@FXML
	private ImageView user_image;
	private ContextMenu context_menu;

	public void initialize() {
		payforColumn.setCellValueFactory(cellData -> cellData.getValue().Payfor());
		priceColumn.setCellValueFactory(cellData -> cellData.getValue().Price());
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().Date());
		commentColumn.setCellValueFactory(cellData -> cellData.getValue().Comment());
		showDetails(null);
		table.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showDetails(newValue));
		bar.setVisible(false);
		context_menu = new ContextMenu();
		MenuItem select_menu = new MenuItem("انتخاب عکس");
		select_menu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(main.getLoggined()) {
					FileChooser imager = new FileChooser();
					ArrayList<String> list = new ArrayList<String>();
					list.add("*.png");
					list.add("*.jpg");
					list.add("*.gif");
					FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images (*.png , *.jpg , *.gif)", list);
					imager.getExtensionFilters().add(extFilter);
					File file = imager.showOpenDialog(main.getStage());
					if(file != null) {
						InputStream is = null;
						try {
							is = new FileInputStream(file);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						Image image = new Image(is);
						image.widthProperty().add(100);
						image.heightProperty().add(100);
						user_image.setImage(image);
						main.saveImage(image);
					}
				}
				else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("پرداختهای من");
					alert.setHeaderText("شما بصورت آفلاین از برنامه استفاده می کنید!");
					alert.setContentText("ابتدا وارد حسابتان شوید");
					alert.showAndWait();
				}
			}
		});
		context_menu.getItems().add(select_menu);
	}

	public void setMain(Main m) {
		main = m;
		table.setItems(main.getPaymentData());
		if(main.getLoggined()) {
			status.setText("آنلاین");
			name.setText(main.getUser().getUsername());
			File profile = new File("profile.png");
			if(profile.exists()) {
				Image img = new Image("profile.png");
				user_image.setImage(img);
			} else {
				user_image.setImage(new Image(getClass().getResourceAsStream("user.png")));
			}
		}
		else {
			status.setText("آفلاین");
			name.setText("");
			user_image.setImage(new Image(getClass().getResourceAsStream("user.png")));
		}
		computeTotal();
	}
	
	public void computeTotal() {
		long c = 0;
		for (Payment p : main.getPaymentData())
			c += Long.parseLong(p.getPrice());
		total.setText(String.valueOf(c));
	}

	public void showDetails(Payment payment) {

		if (payment != null) {
			payforLabel.setText(payment.getPayfor());
			priceLabel.setText(payment.getPrice());
			dateLabel.setText(payment.getDate());
			commentLabel.setText(payment.getComment());
		} else {
			payforLabel.setText("");
			priceLabel.setText("");
			dateLabel.setText("");
			commentLabel.setText("");
		}
	}

	public void setScreenSize() {
		if (sizeCheck.isSelected())
			main.getStage().setFullScreen(true);
		else
			main.getStage().setFullScreen(false);
	}

	public void handleDeletePayment() {
		int selectedIndex = table.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			table.getItems().remove(selectedIndex);
			if (table.getItems().isEmpty())
				main.setSave(true);
			computeTotal();
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(main.getStage());
			alert.setTitle("بدون انتخاب");
			alert.setHeaderText("هیچ پرداختی انتخاب نشده است");
			alert.setContentText("لطفا یک پرداخت را از جدول انتخاب کنید.");
			alert.showAndWait();
		}
	}

	public void handleNewPayment() {
		Payment tempPayment = new Payment();
		boolean okClicked = main.showEditDialog(tempPayment);
		if (okClicked) {
			main.getPaymentData().add(tempPayment);
		}
		computeTotal();
	}

	public void handleEditPayment() {
		Payment selectedPayment = table.getSelectionModel().getSelectedItem();
		if (selectedPayment != null) {
			boolean okClicked = main.showEditDialog(selectedPayment);
			if (okClicked) {
				showDetails(selectedPayment);
			}
			computeTotal();
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(main.getStage());
			alert.setTitle("بدون انتخاب");
			alert.setHeaderText("هیچ پرداختی انتخاب نشده است");
			alert.setContentText("لطفا یک پرداخت را از جدول انتخاب کنید.");
			alert.showAndWait();
		}
	}

	public void handleDate() {
		if (main.getPaymentData().isEmpty()) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.initOwner(main.getStage());
			alert.setTitle("خطا");
			alert.setHeaderText("لیست خالی است!");
			alert.showAndWait();
		} else {
			PersianDate date = new PersianDate(datep.getValue());
			boolean find = false;
			for (Payment p : main.getPaymentData())
				if (p.getDate().equals(date.toString())) {
					table.scrollTo(p);
					table.getSelectionModel().select(p);
					find = true;
					break;
				}
			if (!find)
				for (Payment p : main.getPaymentData())
					if (p.Date().get().getMonth().equals(p.Date().get().getMonth()))
						if (p.Date().get().getYear().equals(date.getYear())) {
							table.scrollTo(p);
							table.getSelectionModel().select(p);
							find = true;
							break;
						}
			if (!find) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.initOwner(main.getStage());
				alert.setTitle("خطا");
				alert.setHeaderText("هیچ پرداختی در این تاریخ و یا در این ماه وجود ندارد!");
				alert.showAndWait();
			}
		}
	}
	
	public void setStatus(String s) {
		status.setText(s);
	}
	
	public void clearProgress() {
		bar.setProgress(0);
		bar.setVisible(false);
	}
	
	public double getProgress() {
		return bar.getProgress();
	}
	
	public void setProgress(double i) {
		bar.setProgress(i);
	}
	
	
	public void setProgressVisible(boolean b) {
		bar.setVisible(b);
	}
	
	public void increaseProgress(double i) {
		bar.setProgress(bar.getProgress()+i);
	}
	
	public void imageHandler(ContextMenuEvent event) {
		context_menu.show(user_image, event.getScreenX(), event.getScreenY());
	}
	
	public void setImage() {
		user_image.setImage(new Image(getClass().getResourceAsStream("user.png")));
	}
	
	public void clearName() {
		name.setText("");
	}
}
