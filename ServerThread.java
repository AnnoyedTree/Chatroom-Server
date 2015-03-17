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
	
	public ServerThread( Server c ) throws IOException
	{
		client = c;
		
		out = new ObjectOutputStream( c.getSocket().getOutputStream() );
		in = new ObjectInputStream( c.getSocket().getInputStream() );
	}
	
	public void sendMessage( Object object ) throws IOException
	{
		if ( object instanceof ArrayList )
			out.reset();
		
		out.flush();
		out.writeObject( object );
	}
	
	public void disconnect() throws IOException
	{
		out.close();
		in.close();
		
		System.out.println( "Connection terminated from " + client.getIPAddress() );
		
		client.getSocket().close();
		interrupted();
		
		Server.removeClient( client );
		Server.removeUser( client.getUsername() );
	}
	
	public void handlePrivateMessage( String line ) throws IOException
	{
		String[] msg = line.split( " ", 3 );
		//String user = msg[1];
		
		if ( msg[1].equals(client.getUsername()) ) //Client tries to PM himself
			return;
		
		line = msg[0] + " " + msg[2];
		for ( Server s : Server.getClientList() )
		{
			if ( s.getUsername().equals(msg[1]) )
			{
				s.getThread().sendMessage( "-" + line ); //
				//sendMessage( "-" + line );
				break;
			}
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			out.flush();
			
			Object object;
			int disconnected;
			
			while ( true )
			{
				if ( (disconnected = in.read()) == 1 )
				{
					disconnect();
					return;
				}
					
				if ( (object = in.readObject()) != null )
				{
					String line = object.toString();
					
					if ( object instanceof String )
						
						if ( line.startsWith("+") ) //connection establish (first message)
						{
							line = line.replace( "+", "" );
							
							Server.addUser( line );
							client.setUsername( line );
						}
						else if ( line.startsWith("@") )
						{
							line = line.replace( "@", "" );
							handlePrivateMessage( line );
						}
						else
							Server.broadcast( object );
				}
			}
		} catch ( Exception e ) {
			//dothings
		}
	}
}
