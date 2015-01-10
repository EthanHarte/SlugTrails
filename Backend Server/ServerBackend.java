
import java.io.*;
import java.net.*;
import java.util.*;


public class ServerBackend 
{
	public static void main(String[] args) throws Exception 
	{
		int port = 25565;
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Awaiting Connection.");
		while(true)
		{
			ServerRequest request = new ServerRequest(serverSocket.accept());
			System.out.println("Connection established.");
			Thread thread = new Thread(request);
			thread.start();
		}
	}
}

final class ServerRequest implements Runnable 
{
	final static String CRLF = "\r\n";
	Socket socket;
	
	public ServerRequest(Socket socket) throws Exception
	{
		this.socket = socket;
	}
	
	public void run()
	{
		try
		{
			processRequest();
		} catch(Exception e){
			System.err.println(e);
		}
	
	}
	
	private void processRequest() throws Exception
	{
		BufferedReader input;
		PrintWriter output;
		String inStr;

		// Debug information. 
		System.out.println("Connected to " + this.socket.getRemoteSocketAddress() + "\n");

		// Initialize output stream.
		output = new PrintWriter(socket.getOutputStream());

		// Instantiate BufferedReader as a listener stream for incoming server data.
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		inStr = input.readLine();

		// Borrowed from Oracle
     		String[] result = inStr.split(",");
     		for (int x=0; x<result.length; x++)
         		System.out.println(result[x]);

		output.println("You have connected to the server; go fuck yourself");
		output.flush();

		//BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        	//DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
        	//out.writeUTF("Thank you for connecting to " + this.socket.getLocalSocketAddress() + 
			//"\nGoodbye!");
		System.out.printf("Server connection closed.\n");
		this.socket.close();
	}
}