package tgm.geyerritter.dezsys06.data;

public class ExplicitConfiguration implements Configuration {

	private String username;
	private String password;
	private String host;
	private String name;
	
	public ExplicitConfiguration(String username, String password, String host, String name) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.name = name;
	}
	
	public String getUser() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getHostAddress() {
		return this.host;
	}

	public String getSystemName() {
		return this.name;
	}

}
