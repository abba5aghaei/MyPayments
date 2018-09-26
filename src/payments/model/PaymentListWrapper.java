package payments.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "payments")
public class PaymentListWrapper {
	
	private List<Payment> payments;
	
	@XmlElement(name = "payment")
	public List<Payment> getPayments() {
		
		return payments;
		}
	public void setPayments(List<Payment> payments) {
		
		this.payments = payments;
		}
}
