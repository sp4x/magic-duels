package core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import entities.Utente;

public class Connection extends Thread {
	
	Utente utente;
	
	Socket player;
	Server server;
	ObjectOutputStream out;
	ObjectInputStream in;

	HostedGame hostedGame;
	
	public Connection(Socket player, Server server) {

		this.utente = null;
		
		this.player = player;
		this.server = server;
		
		this.hostedGame = null;
		
		try {
			
			this.out = new ObjectOutputStream(player.getOutputStream());
			this.out.flush();
			this.in = new ObjectInputStream(player.getInputStream());
			
			this.sendMessage(Messages.WELCOME);
			
			this.start();
			
		} catch (IOException e) {
			System.out.println("Server>Errore durante la creazione degli IOStream");
		}
		
	}
	
	@Override
	public void run() {
		
		String message = null;
		
		// Mi aspetto di ricevere una richiesta di Login, un iscrizione, o la chiusura		
		do
		{
			message = this.readMessage();
			
			if(message.startsWith(Messages.LOGIN))
				login(message);
			
			if(message.startsWith(Messages.NEWUSER))
				if(Server.newUser(message, this.player.getLocalAddress()))
					this.sendMessage(Messages.NEWUSEROK);
				else
					this.sendMessage(Messages.NEWUSERFAILED);
			
		} while(!message.equals(Messages.CLOSE) && this.utente == null);
		
		System.out.println("INIZIO SECONDO WHILE (THREAD)");
		
		if(this.utente != null)
		{
			this.initLobby();
			
			while(!message.equals(Messages.CLOSE))
			{				
				message = this.readMessage();
				System.out.println("client>" + message);
				
				if (message.equals(Messages.CLOSE))
					this.sendMessage(Messages.CLOSE);
			
				if(message.startsWith(Messages.CHAT))
					this.server.sendChatMessage(this.utente.getNome(), message.substring(Messages.CHAT.length()));
				
				if(message.startsWith(Messages.CREATE))
					this.newGame(message);
				
				if(message.startsWith(Messages.CHANGESLOTTYPE))
					System.out.println("CHANGE SLOT STATE");
			
			}
		}
		
		this.sendMessage(Messages.CLOSE);
		this.closeConnection();
		this.server.removePlayer(this);
	}

	public void login(String msg)
	{
		String []logInfo = (msg.substring(Messages.LOGIN.length())).split(";");
		
		this.utente = DBFunctions.logIn(logInfo[0], logInfo[1], this.player.getInetAddress());
		if(this.utente == null)
			this.sendMessage(Messages.LOGINFAILED);
		else
			this.sendMessage(Messages.LOGINOK);

	}
	
	public void sendMessage(String msg)
	{
		try{
			this.out.writeObject(msg);
			this.out.flush();
			System.out.println("server>" + msg);
		}
		catch(IOException ioException){
			System.out.println("Server>Errore durante l'invio del messaggio al client");
		}
	}

	public String readMessage()
	{
		try{
			
			String message;
			message = (String)this.in.readObject();
			
			return message;
			
		} catch(IOException e) {
			System.out.println("Errore durante la ricezione del messaggio");
		} catch (ClassNotFoundException e) {
			System.out.println("Classe non trovata!");
		}
		
		return null;
	}
	
	protected void closeConnection()
	{
		try {
		
			this.in.close();
			this.out.close();
			
			this.player.close();
			
		} catch (IOException e) {
			System.out.println("Errore nella chiusura della connessione");
		}
		
	}	
	
	public void initLobby()
	{
		if(this.readMessage().equals(Messages.LOBBYSTARTED))
		{
			this.sendMessage(Server.getClientList(this.server));
			this.sendMessage(Server.getGameList(this.server));
		}
	}

	public void newGame(String msg)
	{
		String []gameParameter = msg.substring(Messages.CREATE.length()).split(";");
		
		if(this.hostedGame != null)
			this.sendMessage(Messages.CREATEFAILED);
		
		this.hostedGame = new HostedGame(this, Integer.parseInt(gameParameter[2]), gameParameter[0], Integer.parseInt(gameParameter[1]));
		this.server.hostedGames.put(this.hostedGame.gameName, this.hostedGame);
		
		this.sendMessage(Messages.CREATEOK);
	}
	
	public void changeSlotState(int slotIndex, String state)
	{
		this.hostedGame.slots.get(slotIndex).changeSlotState(state);
	}
	
}