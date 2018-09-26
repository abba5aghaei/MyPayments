package payments.model;

import java.time.LocalDate;

public class PersianDate {

	private String day;
	private String month;
	private String year;
	static final String[] months = { "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر",
			"دی", "بهمن", "اسفند" };

	public PersianDate(String d, String m, String y) {
		if (d.length() == 1)
			d = "0" + d;
		if (m.length() == 1)
			m = "0" + m;
		day = d;
		month = m;
		year = y;
	}

	public PersianDate(int d, int m, int y) {
		day = String.valueOf(d);
		month = String.valueOf(m);
		year = String.valueOf(y);
		if (day.length() == 1)
			day = "0" + day;
		if (month.length() == 1)
			month = "0" + month;
	}

	public PersianDate(LocalDate date) {
		day = String.valueOf(date.getDayOfMonth() + 9);
		month = String.valueOf(date.getMonthValue() - 3);
		year = String.valueOf(date.getYear() - 621);
		if (day.length() == 1)
			day = "0" + day;
		if (month.length() == 1)
			month = "0" + month;
	}

	public PersianDate(String date) {
		String d, m, y;
		date = date.replace('.', '/');
		date = date.replace('-', '/');
		date = date.replace(' ', '/');
		date = date.replace('\\', '/');
		date = date.replace('_', '/');
		int c = 0;
		for (int i = 0; i < date.length(); i++)
			if (date.charAt(i) == '/')
				c++;
		if (c == 2) {
			int index = date.indexOf("/");
			d = date.substring(0, index);
			index++;
			int indexm = date.indexOf("/", index);
			m = date.substring(index, indexm);
			indexm++;
			y = date.substring(indexm, date.length());
			if (Integer.parseInt(d) > 31) {
				String t = y;
				y = d;
				d = t;
			}
			if (d.length() == 1)
				d = "0" + d;
			if (m.length() == 1)
				m = "0" + m;
			if (y.length() == 2)
				y = "13" + y;

			Integer.parseInt(m);
			Integer.parseInt(y);
		} else {
			d = "00";
			m = "00";
			y = "0000";
		}
		day = d;
		month = m;
		year = y;
	}

	public void setDay(String d) {

		day = d;
	}

	public void setMonth(String m) {

		month = m;
	}

	public void setYear(String y) {

		year = y;
	}

	public String getDay() {

		return day;
	}

	public String getMonth() {

		return month;
	}

	public String getYear() {

		return year;
	}

	public void parse(String date) {

		String d, m, y;
		date = date.replace('.', '/');
		date = date.replace('-', '/');
		date = date.replace(' ', '/');
		date = date.replace('\\', '/');
		date = date.replace('_', '/');
		int c = 0;
		for (int i = 0; i < date.length(); i++)
			if (date.charAt(i) == '/')
				c++;
		if (c == 2) {
			int index = date.indexOf("/");
			d = date.substring(0, index);
			index++;
			int indexm = date.indexOf("/", index);
			m = date.substring(index, indexm);
			indexm++;
			y = date.substring(indexm, date.length());
			if (Integer.parseInt(d) > 31) {
				String t = y;
				y = d;
				d = t;
			}
			if (d.length() == 1)
				d = "0" + d;
			if (m.length() == 1)
				m = "0" + m;
			if (y.length() == 2)
				y = "13" + y;

			Integer.parseInt(m);
			Integer.parseInt(y);
			if (Integer.parseInt(d) < 1 || Integer.parseInt(d) > 31 || Integer.parseInt(m) < 1
					|| Integer.parseInt(m) > 12)
				Integer.parseInt("Error");
		}
	}

	public static String toString(PersianDate date) {

		return date.getDay() + "/" + date.getMonth() + "/" + date.getYear();
	}

	public String toString() {

		return day + "/" + month + "/" + year;
	}

	public String[] getMonthsNames() {

		return months;
	}

	public String getMothName(int index) {

		return months[index];
	}

	public int getMonthIndex(String m) {

		for (int i = 0; i < months.length; i++)
			if (m.equals(months[i]))
				return i + 1;
		return 0;
	}

	public void set(String date) {

		String d, m, y;
		date = date.replace('.', '/');
		date = date.replace('-', '/');
		date = date.replace(' ', '/');
		date = date.replace('\\', '/');
		date = date.replace('_', '/');
		int c = 0;
		for (int i = 0; i < date.length(); i++)
			if (date.charAt(i) == '/')
				c++;
		if (c == 2) {
			int index = date.indexOf("/");
			d = date.substring(0, index);
			index++;
			int indexm = date.indexOf("/", index);
			m = date.substring(index, indexm);
			indexm++;
			y = date.substring(indexm, date.length());
			if (Integer.parseInt(d) > 31) {
				String t = y;
				y = d;
				d = t;
			}
			if (d.length() == 1)
				d = "0" + d;
			if (m.length() == 1)
				m = "0" + m;
			if (y.length() == 2)
				y = "13" + y;

			Integer.parseInt(m);
			Integer.parseInt(y);
		} else {
			d = "00";
			m = "00";
			y = "0000";
		}
		day = d;
		month = m;
		year = y;
	}

	public void set(LocalDate date) {
		day = String.valueOf(date.getDayOfMonth());
		month = String.valueOf(date.getMonthValue());
		year = String.valueOf(date.getYear());
	}

	public static PersianDate Convert(LocalDate date) {
		int d = date.getDayOfMonth() + 9;
		int m = date.getMonthValue() - 3;
		int y = date.getYear() - 621;
		switch (date.getMonthValue()) {
		case 10:
		case 11:
		case 12:
		case 1:
		case 2: {
			d--;
			if (d > 30) {
				d = d - 30;
				m++;
				if (m > 12) {
					m = m + 12;
					y--;
				}
			}
			break;
		}
		case 3: {
			d = d - 2;
			if (d > 29) {
				d = d - 29;
				m++;
				y++;
			}
			break;
		}
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9: {
			if (d > 31) {
				d = d - 31;
				m++;
			}
			break;
		}
		}
		return new PersianDate(d, m, y);
	}
}
