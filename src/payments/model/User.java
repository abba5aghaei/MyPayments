package payments.model;

public class User {
	
	private int id;
	private String username;
	private String password;
	private String email;
	private boolean remember = false;
	
	public User(int i, String u, String p, String e) {
		
		id = i;
		username = u;
		password = p;
		email = e;
	}
	public User() {
		
		id = 0;
		username = "";
		password = "";
		email = "";
	}
	public void setId(int i) {
		
		id = i;
	}
	public int getId() {
		
		return id;
	}
	public void setUsername(String u) {
		
		username = u;
	}
	public String getUsername() {
		
		return username;
	}
	public void setPassword(String p) {
		
		password = p;
	}
	public String getPassword() {
		
		return password;
	}
	public void setEmail(String e) {
		
		email = e;
	}
	public String getEmail() {
		
		return email;
	}
	public void setRemember(boolean r) {
		
		remember = r;
	}
	public boolean getRemember() {
		
		return remember;
	}
}
