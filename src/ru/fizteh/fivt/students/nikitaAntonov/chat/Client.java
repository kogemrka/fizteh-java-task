package yachat;

import java.util.*;
import java.lang.ref.Reference;
import java.net.*;
import java.io.*;

class NetworkListener extends Thread
{
	private static final String addr = "127.0.0.1";
	private static final int port = 1234;
	
	private ChatClient chat;
	private Socket s;
	private PrintWriter out;
    private BufferedReader in;
	
	public NetworkListener(ChatClient c) throws Exception 
	{
		chat = c;
		s = new Socket(addr, port);
		out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		start();
	}
	
	public void run()
	{
		while (!isInterrupted())
		{
			
		}
		
		System.out.println("Die and bye");
	}
	
	public void login(String nick)
	{
	
	}
	
	public void close()
	{
		interrupt();
	}
	
}

class ChatClient
{
	private String nickname;
	private BufferedReader in;
	private NetworkListener connection;
	
	public ChatClient() {
		in = new BufferedReader(new InputStreamReader(System.in));
	}
	
	@SuppressWarnings("deprecation")
	public void run() throws IOException
	{
		System.out.println("Nickname: ");
		nickname = in.readLine();
		
		System.out.println("Hey!");
		
		String cmd = in.readLine();
		cmd = cmd.trim();
		while (!cmd.equals("quit"))
		{
			if (cmd.equals("connect"))
			{
				if (connection == null)
				{
					connection = new NetworkListener(this);
					connection.login(nickname);
				}
				else
					System.out.println("You are already connected");
			}
			else if (cmd.equals("disconnect"))
			{
				if (connection == null)
				{
					System.out.println("You aren't connected");
				}
				else
				{
					connection.close();
					connection = null;
				}
			}
			cmd = in.readLine();
			cmd = cmd.trim();
		}
		
		
	}
}

public class Client {
	public static void main(String[] args) throws IOException
	{
		
		ChatClient chat = new ChatClient();
		chat.run();
	}

}
