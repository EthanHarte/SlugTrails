
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Hashtable;


public class ServerBackend 
{
	public static Hashtable<Integer, Location> db = 
		new Hashtable<Integer, Location>();
	private static File dbFile;

	public static void main(String[] args) throws Exception 
	{
		int port = 25565;
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("Awaiting Connection.");
		try{
			dbFile = new File("db.data");
			BufferedReader buffer = new BufferedReader(new FileReader("db.data"));
			String inStr;
			String[] strs;
			while((inStr = buffer.readLine()) != null){
				strs = inStr.split("_");
			}
 			buffer.close();
		} catch(Exception e){
			e.printStackTrace();
		}

		while(true)
		{	
			Scanner consoleIn = new Scanner(System.in);
			//LocalServerCommand lsd = new LocalServerCommand(consoleIn);
			//Thread input = new Thread(lsd);
			ServerRequest request = new ServerRequest(serverSocket);
			Thread thread = new Thread(request);
			thread.start();

			String command = consoleIn.nextLine();
			if(command.equals("q"))
			{
				try{
					FileOutputStream fos = new FileOutputStream(dbFile);
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
					for (Location l : db.values())
						bw.write(l.toString());
 
					bw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.exit(1);
			}
		}
	}
}

class Location
{
	int id;
	String name;
	int time;
	float gps1; 
	float gps2;
	String description;

	public Location(){}

	public Location(int id, String name, int time, float gps1, float gps2, String description){
		this.id = id;
		this.name = name;
		this.time = time;
		this.gps1 = gps1;
		this.gps2 = gps2;
		this.description = description;
	}
	
	public int getId(){ return this.id; }

	public void fillLoc(String[] str){
		
	}

	public String toString(){
		String ret = String.format("%d_%s_%d_%f_%f_%s\0", this.id, this.name, 
			this.time, this.gps1, this.gps2, this.description);
		return ret;
	}
}

final class LocalServerCommand implements Runnable {
	Scanner in;
	public LocalServerCommand(Scanner in){
		this.in = in;
	}

	public void run(){
		String command = in.nextLine();
		if(command.equals("q")) 
			System.exit(1);
	}
}

final class ServerRequest implements Runnable 
{
	final static String CRLF = "\r\n";
	ServerSocket sSocket;
	Socket socket;
	Location gpsLoc;
	
	public ServerRequest(ServerSocket sSocket) throws Exception
	{
		this.sSocket = sSocket;
	}
	
	public void run()
	{
		try
		{
			this.socket = sSocket.accept();
			System.out.println("Connection established.");
			processRequest();
		} catch(Exception e){
			e.printStackTrace();
		}
	
	}
	
	private void processRequest() throws Exception 
	{
		BufferedReader input;
		PrintWriter output;
		String inStr;
		// State of command issued by client (-1 default)
		// 0 = getall
		// 1 = update
		// 2 = post
		int commandState = -1;

		float gpsLoc[] = {0.0f, 0.0f}; // GPS location if getAll command is used.
		float radius = 0.0f; // Radius of GPS if getAll command is used.

		ArrayList<Integer> idList = new ArrayList<Integer>();
		

		// Debug information. 
		System.out.println("Connected to " + this.socket.getRemoteSocketAddress() + "\n");

		// Initialize output stream.
		output = new PrintWriter(socket.getOutputStream());

		// Instantiate BufferedReader as a listener stream for incoming server data.
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		// Read information from socket (User command)
		System.out.printf("Reading Information:\n");
		inStr = input.readLine();

		// Splitting and loop borrowed from Oracle
     		String[] result = inStr.split("_");

		// Get command state based on first number
		if(result[0].equals("getall")){
			commandState = 0;
		}
		else if(result[0].equals("update")){
			commandState = 1;
		}
		else if(result[0].equals("post")){
			commandState = 2;
		} else {
			//output.print("Error: command not supported.\0");
			//output.flush();
			throw new Exception("Error: command not supported. [" + result[0] + "]");
		}


		switch(commandState){
			// Getall command "getall_[GPS1]_[GPS2]_[RADIUS]"
			case 0:
			if(result.length != 4){
				this.socket.close();
				throw new Exception("Invalid arguments.");
			}
			gpsLoc[0] = Float.parseFloat(result[1]);
			gpsLoc[1] = Float.parseFloat(result[2]);
			radius = Float.parseFloat(result[3]);

			System.out.printf("Sending data now:\n");
			for(Location l : ServerBackend.db.values()){
				System.out.printf("Value sent to client: %d\n", l.toString());
				output.print(l.toString());
				output.flush();
			}
			break;

			// Update command "update_[ID LIST]"
			case 1:
			if(result.length < 2){
				throw new Exception("Invalid number of arguments.");
			}
			for(int x = 1; x < result.length; x++){
				idList.add(Integer.parseInt(result[x]));
			}
			break;

			// Post command "post_[ID]_[NAME]_[TIME]_[GPS1]_[GPS2]_[DESCRIPTION]"
			case 2:
			if(result.length < 6){
				throw new Exception("Invalid number of arguments.");
			}
			int id = (int)(Integer.parseInt(result[1]));
			Location newLoc = new Location(id, result[2], Integer.parseInt(result[3]), 
				Float.parseFloat(result[4]), Float.parseFloat(result[5]), result[6]);
			ServerBackend.db.put(id, newLoc);
			System.out.printf("Object Added to Database: %s\n", ServerBackend.db.get(id).toString());
			break;

		}

		System.out.printf("GetAll Data: Command: %d, GPS1: %f, GPS2: %f, Radius: %f\n", 
			commandState, gpsLoc[0], gpsLoc[1], radius);
		System.out.printf("Update Data: Command: %d", commandState);	
		for(Integer i : idList){
			System.out.printf("ID:%d\n", i);
		}	

		//System.out.printf("Sending data now:\n");
		output.print("\0");
		output.flush();

		//BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        	//DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
        	//out.writeUTF("Thank you for connecting to " + this.socket.getLocalSocketAddress() + 
			//"\nGoodbye!");
		System.out.printf("Server connection closed.\n");
		this.socket.close();
	}
}