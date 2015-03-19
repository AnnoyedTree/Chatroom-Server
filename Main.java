package webchat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main 
{
	private static ServerSocket listener;
	
	public static void main( String[] args ) throws IOException
	{
		//Establish a port for clients to connect through
		listener = new ServerSocket( 9020 );
		System.out.println( "Chatroom Server started on port: 9020" );
		
		//While the ServerSocket has a valid connection then loop
		//We loop so we can keep accetping connections from different clients
		while ( listener.isBound() )
		{
			try 
			{
				//Socket is accepted by the server, so create a new Client since once has connected
				Socket s = listener.accept();
				new Server( s );
			} catch ( IOException e ) {
				//dothings
			}
		}
	}
}
