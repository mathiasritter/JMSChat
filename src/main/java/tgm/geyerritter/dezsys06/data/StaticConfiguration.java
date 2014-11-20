package tgm.geyerritter.dezsys06.data;

import org.apache.activemq.ActiveMQConnection;

public class StaticConfiguration implements Configuration {

	@Override
	public String getUser() {
		return ActiveMQConnection.DEFAULT_USER;
	}

	@Override
	public String getPassword() {
		return ActiveMQConnection.DEFAULT_PASSWORD;
	}

	@Override
	public String getHostAddress() {
		return ActiveMQConnection.DEFAULT_BROKER_URL;
	}

	@Override
	public String getSystemName() {
		return "Chat";
	}

}
