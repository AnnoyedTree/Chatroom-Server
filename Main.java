package webchat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main 
{
	private static ServerSocket listener;
	private static int PORT = 9020;
	private static final boolean allow = false; //change to true to change the port
	
	public static void main( String[] args ) throws IOException
	{
		if ( allow )
		{
			Scanner input = new Scanner( System.in );
			PORT = input.nextInt();
			input.close();
		}
		
		//Establish a port for clients to connect to
		listener = new ServerSocket( PORT );
		System.out.println( "Chatroom Server started on port: 9020" );
		
		//Connection is valid, keep looping and looking for clients attempting to connect
		while ( listener.isBound() )
		{
			try 
			{
				Socket s = listener.accept();
				//New client connection established, simulate client on server
				new Server( s );
			} catch ( IOException e ) {
				//dothings
			}
		}
	}
}
