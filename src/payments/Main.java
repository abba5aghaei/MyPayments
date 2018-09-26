//INOG
package payments;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import payments.model.Payment;
import payments.model.PaymentListWrapper;
import payments.model.PersianDate;
import payments.model.User;
import payments.view.ChartController;
import payments.view.EditController;
import payments.view.PaneController;
import payments.view.RootController;
import payments.view.SignupController;

//author @abba5aghaei

public class Main extends Application {
	private Stage primary_stage, stage, edit_stage, chart_stage;
	private BorderPane root;
	private ObservableList<Payment> paymentData = FXCollections.observableArrayList();
	private boolean load = false;
	private boolean save = false;
	private boolean loggined = false;
	private boolean first = true;
	private boolean loaddb = false;
	private boolean savedb = false;
	private boolean remember = false;
	private User user;
	private String table;
	private PaneController pane;
	private SignupController signup;
	private int counter = 0;
	private double j = 0;

	@Override
	public void start(Stage primaryStage) {
		primary_stage = primaryStage;
		loadUser();
		if (user.getId() != 0) {
			remember = user.getRemember();
			table = "payments_" + user.getUsername();
			loggined = true;
		}
		if (!remember)
			showSignup();
		else {
			//initizaleRoot();
			showPane();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void showSignup() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/Signup.fxml"));
			AnchorPane signup_panel = loader.load();
			Scene signup_scene = new Scene(signup_panel);
			primary_stage.setScene(signup_scene);
			primary_stage.centerOnScreen();
			primary_stage.setTitle("حسابان");
			primary_stage.getIcons().add(new Image(Main.class.getResourceAsStream("view/paid.png")));
			signup = loader.getController();
			signup.setMain(this);
			primary_stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initizaleRoot() {
		File file = getPaymentFilePath();
		if (file != null)
			loadPaymentDataFromFile(file);
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/Root.fxml"));
			root = (BorderPane) loader.load();
			Scene scene = new Scene(root);
			stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("حسابان");
			stage.getIcons().add(new Image(Main.class.getResourceAsStream("view/paid.png")));
			stage.centerOnScreen();
			RootController controller = loader.getController();
			controller.setMain(this);
			stage.show();
		} catch (Exception e) {
		}
	}

	public void showPane() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/Pane.fxml"));
			AnchorPane panel = loader.load();
			root.setCenter(panel);
			pane = loader.getController();
			pane.setMain(this);
		} catch (Exception e) {
		}
	}

	public Stage getPrimaryStage() {
		return primary_stage;
	}

	public Stage getStage() {
		return stage;
	}

	public Stage getEditStage() {
		return edit_stage;
	}

	public Stage getChartStage() {
		return chart_stage;
	}

	public ObservableList<Payment> getPaymentData() {
		return paymentData;
	}

	public void setSave(boolean b) {
		save = b;
	}

	public void setLoad(boolean b) {
		load = b;
	}

	public boolean getSave() {
		return save;
	}

	public boolean getLoad() {
		return load;
	}

	public boolean showEditDialog(Payment payment) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("view/Edit.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			edit_stage = new Stage();
			edit_stage.setTitle("ویرایش پرداخت");
			edit_stage.initModality(Modality.WINDOW_MODAL);
			edit_stage.initOwner(stage);
			Scene scene = new Scene(page);
			edit_stage.setScene(scene);
			edit_stage.getIcons().add(new Image(Main.class.getResourceAsStream("view/paid.png")));
			EditController controller = loader.getController();
			controller.setMain(this);
			controller.setPayment(payment);
			edit_stage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public File getPaymentFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	public void setPaymentFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(Main.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());
			stage.setTitle("حسابان - " + file.getName());
		} else {
			prefs.remove("filePath");
			stage.setTitle("حسابان");
		}
	}

	public void loadPaymentDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PaymentListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			PaymentListWrapper wrapper = (PaymentListWrapper) um.unmarshal(file);
			paymentData.clear();
			paymentData.addAll(wrapper.getPayments());
			setPaymentFilePath(file);
		} catch (Exception e) {}
	}

	public void addPaymentDataFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PaymentListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			PaymentListWrapper wrapper = (PaymentListWrapper) um.unmarshal(file);
			paymentData.addAll(wrapper.getPayments());
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("خطا");
			alert.setHeaderText("خطا در بارگذاری فایل");
			alert.setContentText("نمی توان این فایل را بارگذاری کرد:\n" + file.getPath());
			alert.showAndWait();
		}
	}

	public void savePaymentDataToFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PaymentListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			PaymentListWrapper wrapper = new PaymentListWrapper();
			wrapper.setPayments(paymentData);
			m.marshal(wrapper, file);
			save = true;
			setPaymentFilePath(file);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("خطا");
			alert.setHeaderText("خطا در ذخیره  فایل");
			alert.setContentText("نمی توان این فایل را ذخیره کرد:\n" + file.getPath());
			alert.showAndWait();
		}
	}

	public void setLoggined(boolean b) {
		loggined = b;
	}

	public boolean getLoggined() {
		return loggined;
	}

	public void setFirst(boolean b) {
		first = b;
	}

	public boolean getFirst() {
		return first;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User u) {
		user = u;
	}

	public void loadUser() {
		File userFile = new File("setting.abs");
		if (userFile.exists()) {
			try {
				BufferedReader input = new BufferedReader(new FileReader(userFile));
				char[] information = new char[(int) userFile.length()];
				input.read(information);
				String info = String.valueOf(information);
				ArrayList<Integer> points = new ArrayList<Integer>();
				for (int i = 0; i < info.length(); i++)
					if (info.charAt(i) == '%')
						points.add(i);
				user = new User();
				user.setId(Integer.parseInt(info.substring(0, points.get(0))));
				user.setUsername(info.substring(points.get(0) + 1, points.get(1)));
				user.setPassword(info.substring(points.get(1) + 1, points.get(2)));
				user.setEmail(info.substring(points.get(2) + 1, points.get(3)));
				user.setRemember(Boolean.parseBoolean(info.substring(points.get(3) + 1, info.length())));
				input.close();
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("خطا");
				alert.setHeaderText("خطا در بارگذاری فایل");
				alert.setContentText("نمی توان این فایل را بارگذاری کرد:\n" + userFile.getPath());
				alert.showAndWait();
			}
		} else
			user = new User();
	}

	public void saveUser() {
		byte[] information = (user.getId() + "%" + user.getUsername() + "%" + user.getPassword() + "%" + user.getEmail()
				+ "%" + user.getRemember()).getBytes();
		File userFile = new File("setting.abs");
		try {
			FileOutputStream output = new FileOutputStream(userFile);
			output.write(information);
			output.close();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("خطا");
			alert.setHeaderText("خطا در ذخیره  فایل");
			alert.setContentText("نمی توان این فایل را ذخیره کرد:\n" + userFile.getPath());
			alert.showAndWait();
		}
	}

	public void printData() {
		class tryPrint implements Printable {
			public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
				if (pageIndex != 0)
					return NO_SUCH_PAGE;
				Graphics2D g2d = (Graphics2D) g;
				g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
				g2d.setPaint(Color.BLACK);
				try {
					BufferedImage bf = ImageIO.read(getClass().getResourceAsStream("view/paid2.png"));
					g2d.drawImage(bf, null, 520, 30);
				} catch (IOException e) {
				}
				PersianDate ld = PersianDate.Convert(LocalDate.now());
				String cd = String.valueOf(ld.getDay());
				String cm = String.valueOf(ld.getMonth());
				String cy = String.valueOf(ld.getYear());
				g2d.drawString("لیست پرداختهای ماهانه", 420, 50);
				g2d.drawString("تاریخ: " + cd + "/" + cm + "/" + cy, 50, 50);
				g2d.drawRect(50, 75, 130, 35);
				g2d.drawString("بابت پرداخت", 75, 90);
				g2d.drawRect(180, 75, 90, 35);
				g2d.drawString("مبلغ", 205, 90);
				g2d.drawRect(270, 75, 80, 35);
				g2d.drawString("تاریخ", 295, 90);
				g2d.drawRect(350, 75, 200, 35);
				g2d.drawString("توضیحات", 430, 90);
				int i = 0;
				for (Payment p : paymentData) {
					g2d.drawRect(50, 110 + i, 130, 35);
					g2d.drawString(p.getPayfor(), 75, 125 + i);
					g2d.drawRect(180, 110 + i, 90, 35);
					g2d.drawString(p.getPrice(), 195, 125 + i);
					g2d.drawRect(270, 110 + i, 80, 35);
					g2d.drawString(p.getDate(), 275, 125 + i);
					g2d.drawRect(350, 110 + i, 200, 35);
					g2d.drawString(p.getComment(), 365, 125 + i);
					i += 35;
				}
				return PAGE_EXISTS;
			}
		}
		PrinterJob pj = PrinterJob.getPrinterJob();
		pj.setPrintable(new tryPrint());
		if (pj.printDialog())
			try {
				pj.print();
			} catch (PrinterException pe) {
				JOptionPane.showMessageDialog(null, "حطایی در پرینت گرفتن رخ داده است!", "پرداحتهای من",
						JOptionPane.ERROR_MESSAGE);
			}
	}

	public void showChart() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("view/Chart.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			chart_stage = new Stage();
			chart_stage.setTitle("نمودار مخارج");
			chart_stage.initModality(Modality.WINDOW_MODAL);
			chart_stage.initOwner(stage);
			Scene scene = new Scene(page);
			chart_stage.setScene(scene);
			chart_stage.getIcons().add(new Image(Main.class.getResourceAsStream("view/paid.png")));
			ChartController controller = loader.getController();
			controller.setPaymentData(paymentData);
			controller.setStage(chart_stage);
			chart_stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void signIn(String u, String p, String e) {
		Thread signer = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							signup.setStatusSig("ارسال...");
						}
					});
					signup.increaseSig(0.11);
					Thread.sleep(500);
					signup.increaseSig(0.38);
					Thread.sleep(500);
					URL url = new URL("https://abba5aghaei.ir/payments/signin.php");
					Map<String, Object> params = new LinkedHashMap<>();
					params.put("username", u);
					params.put("password", p);
					params.put("email", e);
					StringBuilder postData = new StringBuilder();
					for (Map.Entry<String, Object> param : params.entrySet()) {
						if (postData.length() != 0)
							postData.append('&');
						postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
						postData.append('=');
						postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
					}
					byte[] postDataBytes = postData.toString().getBytes("UTF-8");
					signup.increaseSig(0.06);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							signup.setStatusSig("منتظر سرور");
						}
					});
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
					conn.setDoOutput(true);
					signup.increaseSig(0.16);
					conn.getOutputStream().write(postDataBytes);
					signup.increaseSig(0.16);
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					String res = in.readLine();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							signup.setStatusSig("پایان");
						}
					});
					signup.increaseSig(0.12);
					Thread.sleep(800);
					signup.increaseSig(0.01);
					Thread.sleep(200);
					JOptionPane.showMessageDialog(null, res, "حسابان", JOptionPane.INFORMATION_MESSAGE);
					in.close();
					conn.disconnect();
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							signup.clearSig();
						}
					});
				} catch (IOException | InterruptedException e1) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							signup.clearSig();
						}
					});
					showError(e1);
				}
			}
		});
		signer.start();
	}

	public void login(String u, String p) {
		Thread loginer = new Thread(new Runnable() {
			@Override
			public void run() {
				if (u.length() != 0 && p.length() != 0) {
					try {
						signup.increaseLog(0.03);
						URL url = new URL("https://abba5aghaei.ir/payments/login.php");
						Map<String, Object> params = new LinkedHashMap<>();
						params.put("username", u);
						params.put("password", p);
						signup.increaseLog(0.07);
						StringBuilder postData = new StringBuilder();
						for (Map.Entry<String, Object> param : params.entrySet()) {
							if (postData.length() != 0)
								postData.append('&');
							postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
							postData.append('=');
							postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
							signup.increaseLog(0.02);
						}
						byte[] postDataBytes = postData.toString().getBytes("UTF-8");
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								signup.setStatusLog("در حال ارسال...");
							}
						});
						signup.increaseLog(0.03);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								signup.setStatusLog("اعتبارسنجی...");
							}
						});
						for (int i = 0; i < 42; i++) {
							signup.increaseLog(0.01);
							Thread.sleep(10);
						}
						conn.setRequestMethod("POST");
						conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
						signup.increaseLog(0.03);
						conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
						conn.setDoOutput(true);
						signup.increaseLog(0.12);
						conn.getOutputStream().write(postDataBytes);
						signup.increaseLog(0.03);
						BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
						String res = in.readLine();
						signup.increaseLog(0.04);
						if (res.equals("0")) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									signup.clearLog();
								}
							});
							JOptionPane.showMessageDialog(null, "گذر واژه اشتباه است!", "خطا",
									JOptionPane.ERROR_MESSAGE);
						} else if (res.equals("1")) {
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									signup.clearLog();
								}
							});
							JOptionPane.showMessageDialog(null, "نام کاربری وجود ندارد", "خطا",
									JOptionPane.ERROR_MESSAGE);
						} else {
							signup.increaseLog(0.05);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									signup.setStatusLog("تایید شد");
								}
							});
							signup.increaseLog(0.03);
							ArrayList<Integer> points = new ArrayList<Integer>();
							for (int i = 0; i < res.length(); i++)
								if (res.charAt(i) == '%')
									points.add(i);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									signup.setStatusLog("ارزیابی...");
								}
							});
							signup.increaseLog(0.03);
							user = new User();
							user.setId(Integer.parseInt(res.substring(0, points.get(0))));
							user.setUsername(res.substring(points.get(0) + 1, points.get(1)));
							user.setPassword(res.substring(points.get(1) + 1, points.get(2)));
							user.setEmail(res.substring(points.get(2) + 1, res.length()));
							signup.increaseLog(0.05);
							if (remember)
								user.setRemember(true);
							else
								user.setRemember(false);
							Thread.sleep(500);
							signup.increaseLog(0.01);
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									signup.setStatusLog("وارد شدید!");
								}
							});
							signup.increaseLog(0.01);
							loggined = true;
							Thread.sleep(800);
							signup.increaseLog(0.01);
							Thread.sleep(200);
							if (first) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										initizaleRoot();
										showPane();
									}
								});
							}
							table = "payments_" + user.getUsername();
							saveUser();
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									primary_stage.close();
								}
							});
						}
						conn.disconnect();
					} catch (Exception e) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								signup.clearLog();
							}
						});
						showError(e);
					}
				}
			}
		});
		loginer.start();
	}

	public void saveToDB() {
		Thread saver = new Thread(new Runnable() {
			@Override
			public void run() {
				int state;
				if (loaddb || savedb)
					state = 0;
				else
					state = 1;
				try {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							pane.setStatus("در حال اتصال به پایگاه داده...");
						}
					});
					URL url = new URL("https://abba5aghaei.ir/payments/savedb.php");
					double size = paymentData.size();
					j = 1.00 / size;
					counter = 0;
					for (Payment p : paymentData) {
						Map<String, Object> params = new LinkedHashMap<>();
						params.put("state", state);
						params.put("table", table);
						params.put("payfor", p.getPayfor());
						params.put("price", p.getPrice());
						params.put("date", p.getDate());
						params.put("comment", p.getComment());
						StringBuilder postData = new StringBuilder();
						for (Map.Entry<String, Object> param : params.entrySet()) {
							if (postData.length() != 0)
								postData.append('&');
							postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
							postData.append('=');
							postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
						}
						byte[] postDataBytes = postData.toString().getBytes("UTF-8");
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setRequestMethod("POST");
						conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
						conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
						conn.setDoOutput(true);
						conn.getOutputStream().write(postDataBytes);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								pane.setStatus("در حال ذخیره سازی " + counter + "/" + (int) size);
								pane.increaseProgress(j);
								counter++;
							}
						});
						params.clear();
						state = 1;
						BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
						while ((in.read()) > 0) {
						}
						in.close();
						conn.disconnect();
					}
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							pane.setStatus("ذخیره شد!");
							pane.setProgress(1);
						}
					});
					Thread.sleep(1000);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							pane.setProgressVisible(false);
							pane.setStatus("آنلاین");
						}
					});
					savedb = true;
				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							pane.setStatus("ناموفق");
							pane.clearProgress();
						}
					});
					showError(e);
				}
			}
		});
		saver.start();
	}

	public void loadFromDB() {
		if (paymentData.isEmpty()) {
			loadAndParse();
		} else {
			int r = JOptionPane.showConfirmDialog(null, "داده های فعلی جدول را ذخیره نکرده اید، آیا ادامه می دهید؟",
					"هشدار", JOptionPane.YES_NO_OPTION);
			if (r == 0)
				loadAndParse();
		}
	}

	private void loadAndParse() {
		Thread loader = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							pane.setStatus("در حال اتصال به پایگاه داده...");
						}
					});
					pane.increaseProgress(0.09);
					URL url = new URL("https://abba5aghaei.ir/payments/loaddb.php");
					StringBuilder postData = new StringBuilder();
					postData.append(URLEncoder.encode("table", "UTF-8"));
					postData.append('=');
					postData.append(URLEncoder.encode(table, "UTF-8"));
					byte[] postDataBytes = postData.toString().getBytes("UTF-8");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					pane.increaseProgress(0.19);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
					conn.setDoOutput(true);
					conn.getOutputStream().write(postDataBytes);
					String res, pf, pr, dt, cm;
					counter = 0;
					Vector<String> inputs = new Vector<String>();
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					res = in.readLine();
					while (!(res.equals("finish"))) {
						inputs.addElement(res);
						res = in.readLine();
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								pane.setStatus("در حال دریافت " + counter);
							}
						});
						pane.increaseProgress(0.05);
						if (pane.getProgress() > 0.7)
							pane.setProgress(0.65);
						counter++;
						Thread.sleep(50);
					}
					for (String e : inputs) {
						ArrayList<Integer> points = new ArrayList<Integer>();
						for (int i = 0; i < e.length(); i++)
							if (e.charAt(i) == '%')
								points.add(i);
						pf = e.substring(0, points.get(0));
						pr = e.substring(points.get(0) + 1, points.get(1));
						dt = e.substring(points.get(1) + 1, points.get(2));
						cm = e.substring(points.get(2) + 1, e.length());
						paymentData.add(new Payment(pf, pr, dt, cm));
					}
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							pane.setStatus("در حال تجزیه...");
						}
					});
					for (int u = 0; u < 30; u++) {
						pane.increaseProgress(0.01);
						Thread.sleep(50);
					}
					conn.disconnect();
					loaddb = true;
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							pane.setStatus("بارگذاری با موفقیت پایان یافت!");
							pane.setProgress(1);
						}
					});
					Thread.sleep(1500);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							pane.setProgressVisible(false);
							pane.setStatus("آنلاین");
						}
					});
					pane.computeTotal();
				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							pane.setStatus("ناموفق");
							pane.clearProgress();
						}
					});
					showError(e);
				}
			}
		});
		loader.start();
	}

	private void showError(Exception e) {
		JOptionPane.showMessageDialog(null, "خطا در برقراری ارتباط", "خطا", JOptionPane.ERROR_MESSAGE);
	}

	public void setRemember(boolean b) {
		remember = b;
	}

	public boolean getRemember() {
		return remember;
	}

	public void forgot() {
		String backup = JOptionPane.showInputDialog(null, "ایمیل بازیابی خود را وارد کنید:", "Example@yahoo.com");
		if (backup != null) {
			if (user.getId() != 0) {
				if (backup.equals(user.getEmail()))
					JOptionPane.showMessageDialog(null, "گذرواژه شما: " + user.getPassword(), "بازیابی گذرواژه",
							JOptionPane.INFORMATION_MESSAGE);
				else {
					checkEmail(backup);
				}
			} else {
				checkEmail(backup);
			}
		}
	}

	private void checkEmail(String em) {
		Thread checker = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					signup.increaseLog(0.03);
					URL url = new URL("https://abba5aghaei.ir/payments/check.php");
					StringBuilder postData = new StringBuilder();
					postData.append(URLEncoder.encode("email", "UTF-8"));
					postData.append('=');
					postData.append(URLEncoder.encode(em, "UTF-8"));
					signup.increaseLog(0.02);
					byte[] postDataBytes = postData.toString().getBytes("UTF-8");
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							signup.setStatusLog("در حال جستجو...");
						}
					});
					signup.increaseLog(0.03);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					for (int i = 0; i < 42; i++) {
						signup.increaseLog(0.01);
						Thread.sleep(10);
					}
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					signup.increaseLog(0.03);
					conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
					conn.setDoOutput(true);
					signup.increaseLog(0.12);
					conn.getOutputStream().write(postDataBytes);
					signup.increaseLog(0.12);
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					String res = in.readLine();
					signup.increaseLog(0.04);
					if (res.equals("0")) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								signup.clearLog();
							}
						});
						JOptionPane.showMessageDialog(null, "آدرس ایمیل اشتباه است", "بازیابی گذرواژه",
								JOptionPane.ERROR_MESSAGE);
					} else {
						signup.increaseLog(0.05);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								signup.setStatusLog("تایید شد");
							}
						});
						signup.increaseLog(0.11);
						Thread.sleep(500);
						signup.increaseLog(0.02);
						Thread.sleep(800);
						signup.increaseLog(0.01);
						Thread.sleep(200);
						JOptionPane.showMessageDialog(null, "گذرواژه شما: " + res, "بازیابی گذرواژه",
								JOptionPane.INFORMATION_MESSAGE);
						conn.disconnect();
					}
				} catch (Exception e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							signup.clearLog();
						}
					});
					showError(e);
				}
			}
		});
		checker.start();
	}

	public PaneController getPane() {
		return pane;
	}

	public void saveImage(Image img) {
		BufferedImage bfimg = SwingFXUtils.fromFXImage(img, null);
		File out = new File("profile.png");
		try {
			ImageIO.write(bfimg, "png", out);
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
	}

	public void setTable(String s) {
		table = s;
	}
}
