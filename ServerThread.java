package webchat_server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ServerThread extends Thread
{
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private Server client;
	
	//Constructor
	public ServerThread( Server c ) throws IOException
	{
		//The client running this thread
		client = c;
		
		//Add an output stream from the connection (to send messages)
		out = new ObjectOutputStream( c.getSocket().getOutputStream() );
		//Add an input stream from the connection (to receive messages)
		in = new ObjectInputStream( c.getSocket().getInputStream() );
	}
	
	//Send a message through the thread
	public void sendMessage( Object object ) throws IOException
	{
		//Reset the output stream to send ArrayLists
		if ( object instanceof ArrayList )
			out.reset();
		
		out.flush();
		out.writeObject( object ); //Write message to the client
	}
	
	//Disconnect the client when the client is requesting a connection abort
	public void disconnect() throws IOException
	{
		//Close our input/output streams
		out.close();
		in.close();
		
		//Inform the server
		System.out.println( client.getUsername() + " disconnected from " + client.getIPAddress() );
		
		//Close connections and stop this thread from running anymore
		client.getSocket().close();
		interrupted();
		
		//Removes client from the connection list and the users list
		Server.removeClient( client );
		Server.removeUser( client.getUsername() );
	}
	
	//Private messaging handled by the server
	public void handlePrivateMessage( String line ) throws IOException
	{
		//Split our string so we can re-arrange it.
		String[] msg = line.split( " ", 3 );

		//Silly guy, the client tried to send a private message to himself.
		if ( msg[1].equals(client.getUsername()) )
			return;
		
		//Re-arrange our string to not include the user's name (It would look awkward)
		line = msg[0] + " " + msg[2];
		//Loop through all of the clients currently connected to us
		for ( Server s : Server.getClientList() )
		{
			//If we found the user the client is trying to private message
			if ( s.getUsername().equals(msg[1]) )
			{
				//Send him the private message and only him
				s.getThread().sendMessage( "-" + line );
				//We did what we need to in our loop, so stop our loop
				break;
			}
		}
	}
	
	//Runnable thread
	@Override
	public void run()
	{
		try
		{
			out.flush();
			
			Object object;
			int disconnected;
			
			//Keep looping through messages until the client decides to disconnect
			while ( true )
			{
				//Recieved a message from the client requesting him to disconnect
				if ( (disconnected = in.read()) == 1 )
				{
					disconnect();
					return;
				}
				
				//There is a valid message waiting to be processed
				if ( (object = in.readObject()) != null )
				{
					String line = object.toString();
					
					if ( object instanceof String )
						
						//This is a client connecting. We are receiving his username
						if ( line.startsWith("+") )
						{
							//Remove the '+' symbol since its only used to distinguish what type of message it is (+ = Add User)
							line = line.replace( "+", "" );
							
							//Add the user to the user's list and set the clients name of the server
							Server.addUser( line );
							client.setUsername( line );
						}
						//This is a client trying to send a private Message
						else if ( line.startsWith("@") )
						{
							//Remove the '@' symbol since its only used to distinguish what type of message it is (@ = PM)
							line = line.replace( "@", "" );
							handlePrivateMessage( line );
						}
						//If the string holds no special character in the beginning, it is just a regular message
						else
							Server.broadcast( object );
				}
			}
		} catch ( Exception e ) {
			//dothings
		}
	}
}
