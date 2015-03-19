package webchat_server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class Server 
{
	private static ArrayList<String> userList = new ArrayList<String>();
	private static ArrayList<Server> clientList = new ArrayList<Server>();
	
	private Socket socket;
	private ServerThread thread;
	
	private String ipaddress;
	private String username;
	
	//Constructor
	public Server( Socket s ) throws IOException
	{
		//Save the clients connection & ipaddress
		socket = s;
		ipaddress = s.getLocalAddress().getHostAddress();
		
		//We create a new thread to allow for multi-threading on the server
		//This is create a new Input/Output for the client to handle through the server
		thread = new ServerThread( this );
		thread.start();
		
		//Add the client to the list of Connected Clients
		Server.addClient( this );
		
		//Inform the server a client connected
		//System.out.println( getUsername() + " disconnected from " + getIPAddress() );
	}
	
	//Connection
	public Socket getSocket()
	{
		return socket;
	}
	
	//This will get the thread of the user
	//This is needed since we are handling Input/Output on the thread
	public ServerThread getThread()
	{
		return thread;
	}
	
	//Add a client to the connections list
	public static void addClient( Server c )
	{
		clientList.add( c );
	}
	
	//Remove a client from the connections list
	public static void removeClient( Server c )
	{
		clientList.remove( c );
	}
	
	//Add the client's username to the users list
	public static void addUser( String name )
	{
		userList.add( name );
		updateUserList();
	}
	
	//Remove the client's username from the users list
	public static void removeUser( String name )
	{
		userList.remove( name );
		updateUserList();
	}
	
	//Everytime a user is removed or added the userList updates and sends to the client
	//This will ensure all clients stay updated with the same list
	public static void updateUserList()
	{
		try
		{
			broadcast( userList );
		} catch ( IOException e ) {
			//do things
		}
	}
	
	//Get the list of all connected clients
	public static ArrayList<Server> getClientList()
	{
		return clientList;
	}
	
	//Sends a message to all clients connected to this server
	public static void broadcast( Object object ) throws IOException
	{
		for ( Server c : getClientList() )
		{
			c.getThread().sendMessage( object );
		}
	}
	
	public String getIPAddress()
	{
		return ipaddress.replace( "127.0.0.1", "localhost" );
	}
	
	public void setUsername( String name )
	{
		username = name;
		System.out.println( name + " connected from " + getIPAddress() );
	}
	
	public String getUsername()
	{
		return username;
	}
}
