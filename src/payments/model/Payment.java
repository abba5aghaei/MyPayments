package payments.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Payment {

	private final StringProperty payfor;
	private final StringProperty price;
	private final ObjectProperty<PersianDate> date;
	private final StringProperty comment;

	public Payment() {

		this(null, null);
	}

	public Payment(String pf, String pr) {
		payfor = new SimpleStringProperty(pf);
		price = new SimpleStringProperty(pr);
		date = new SimpleObjectProperty<PersianDate>(PersianDate.Convert(LocalDate.now()));
		comment = new SimpleStringProperty("");
	}

	public Payment(String pf, String pr, String dt, String cm) {

		payfor = new SimpleStringProperty(pf);
		price = new SimpleStringProperty(pr);
		date = new SimpleObjectProperty<PersianDate>(new PersianDate(dt));
		comment = new SimpleStringProperty(cm);
	}

	public StringProperty Payfor() {

		return payfor;
	}

	public String getPayfor() {

		return payfor.get();
	}

	public void setPayfor(String pf) {

		payfor.set(pf);
	}

	public StringProperty Price() {

		return price;
	}

	public String getPrice() {

		return price.get();
	}

	public void setPrice(String pr) {

		price.set(pr);
	}

	public ObjectProperty<PersianDate> Date() {

		return date;
	}

	public String getDate() {

		return date.get().toString();
	}

	public void setDate(String dt) {

		date.set(new PersianDate(dt));
		;
	}

	public StringProperty Comment() {

		return comment;
	}

	public String getComment() {

		return comment.get();
	}

	public void setComment(String cm) {

		comment.set(cm);
		;
	}
}
