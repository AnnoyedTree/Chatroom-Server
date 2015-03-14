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
	
	public Server( Socket s ) throws IOException
	{
		socket = s;
		ipaddress = s.getLocalAddress().getHostAddress();
		
		System.out.println( "Connection established from " + getIPAddress() );
		
		thread = new ServerThread( this );
		thread.start();
		
		Server.addClient( this );
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	public ServerThread getThread()
	{
		return thread;
	}
	
	public static void addClient( Server c )
	{
		clientList.add( c );
	}
	
	public static void removeClient( Server c )
	{
		clientList.remove( c );
	}
	
	public static void addUser( String name )
	{
		userList.add( name );
		updateUserList();
	}
	
	public static void removeUser( String name )
	{
		userList.remove( name );
		updateUserList();
	}
	
	public static void updateUserList()
	{
		try
		{
			broadcast( getUserList() );
		} catch ( IOException e ) {
			//do things
		}
	}
	
	public static ArrayList<Server> getClientList()
	{
		return clientList;
	}
	
	public static ArrayList<String> getUserList()
	{
		return userList;
	}
	
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
	}
	
	public String getUsername()
	{
		return username;
	}
}
