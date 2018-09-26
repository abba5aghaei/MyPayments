package payments.view;

import java.util.Arrays;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import payments.model.Payment;

public class ChartController {
	@FXML
	private BarChart<String, Long> barChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private ComboBox<String> list;
	private ObservableList<String> monthNames = FXCollections.observableArrayList();
	private Stage stage;
	private List<Payment> payments;

	public void initialize() {

		String[] months = { "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی",
				"بهمن", "اسفند" };
		monthNames.addAll(Arrays.asList(months));
		xAxis.setCategories(monthNames);
	}

	public void setStage(Stage s) {
		stage = s;
	}

	private ObservableList<Payment> separatePayments(String year) {
		ObservableList<Payment> pays = FXCollections.observableArrayList();
		for (Payment p : payments) {
			if (p.Date().get().getYear().equals(year))
				pays.add(p);
		}
		return pays;
	}

	private ObservableList<String> getYears() {
		ObservableList<String> l = FXCollections.observableArrayList();
		for (Payment p : payments) {
			if (!l.contains(p.Date().get().getYear()))
				l.add(p.Date().get().getYear());
		}
		return l;
	}

	private void drawChart(List<Payment> pays) {
		long[] priceCounter = new long[12];
		for (Payment p : pays) {
			int month = Integer.parseInt(p.Date().getValue().getMonth()) - 1;
			priceCounter[month] += Long.parseLong(p.getPrice());
		}
		XYChart.Series<String, Long> series = new XYChart.Series<>();
		for (int i = 0; i < priceCounter.length; i++) {
			series.getData().add(new XYChart.Data<>(monthNames.get(i), priceCounter[i]));
		}
		barChart.getData().clear();
		barChart.getData().add(series);
	}

	public void setPaymentData(List<Payment> pd) {
		if(pd.size()!=0) {
		payments = pd;
		list.setItems(getYears());
		list.getSelectionModel().select(0);
		drawChart(separatePayments(list.getItems().get(0)));
		}
		else {
			System.out.println("Threre are no payment");
		}
	}

	public void close() {
		stage.close();
	}

	public void selectYear() {
		drawChart(separatePayments(list.getSelectionModel().getSelectedItem()));
	}
}