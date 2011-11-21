package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;

import org.xml.sax.InputSource;

import text.output.ProcessText;

/**
 * The individual server thread.
 * 
 * @author ksmall
 */
public class ServerThread extends Thread {
	
	/** the server socket */
	protected Socket socket;
	/** the reader for getting data from the client */
	protected BufferedReader fromClient;
	/** the object which processes text */
	protected ProcessText process;

	/**
	 * Constructor
	 * 
	 * @param socket	server socket
	 * @param process	text processor
	 * @throws Exception
	 */
	public ServerThread(Socket socket, ProcessText process) throws Exception {
		super("ServerThread");
		this.socket = socket;
		this.fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.process = process;
	}
	
	public void run() {
		StringBuffer entry = new StringBuffer();
		String input = null;
		PrintWriter toClient = null;
		
		try {
			toClient = new PrintWriter(socket.getOutputStream(), true);
			
			while ((input = fromClient.readLine()) != null) {
				entry.append(input + "\n");
				break;
			}
			
			if (input != null)
			process.process(input(entry), toClient);
			
			toClient.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public InputSource input(StringBuffer s) {
		return new InputSource(new StringReader(s.toString()));
	}
}
