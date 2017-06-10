package annoy34.mail;

public class Server {
	private String hostname;
	private int port;
	private String socketType;
	
	public Server(String hostname, int port, String socketType) {
		this.hostname = hostname;
		this.port = port;
		this.socketType = socketType;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getSocketType() {
		return socketType;
	}

}