package server;

import java.io.IOException;
import java.net.ServerSocket;

import text.output.ProcessText;

/**
 * A flexible Server class
 * 
 * @author ksmall
 */
public class Server {

	protected ServerSocket server;
	protected ProcessText processor;
	protected boolean running;
	
	public Server(int portNumber, ProcessText processor) {
		try {
			server = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + portNumber);
			System.exit(1);
		}
		this.processor = processor;
	}
		
	public void start() {
		running = true;
		try {
			while (running)
				new ServerThread(server.accept(), processor.newInstance()).start();
			server.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
