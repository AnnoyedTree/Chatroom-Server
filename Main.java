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
		listener = new ServerSocket( 9020 );
		System.out.println( "Chatroom Server started on port: 9020" );
		
		while ( listener.isBound() )
		{
			try 
			{
				Socket s = listener.accept();
				new Server( s );
			} catch ( IOException e ) {
				//dothings
			}
		}
	}
}
