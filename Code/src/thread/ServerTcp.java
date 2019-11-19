package thread;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class ServerTcp extends Thread{

	int nbClient = 0;
	
	List<Socket> cc = new ArrayList<Socket>();
	
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(1228);
			
			while(true) {
				Socket s = ss.accept();
				cc.add(s);
				++nbClient;
				new Conversation(s, nbClient).start();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void broadcast(int i) {
		for(Socket k : cc) {
			if(k != null) {
			try {
				PrintWriter pw = new PrintWriter(k.getOutputStream(), true);
				pw.println("compteur client = "+ i + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
			else System.out.println("Aucune socket dispo ! ");
		}
	}
	
	class Conversation extends Thread{
		private Socket socket;
		private int numClient;
		private int cpt = 0;
		
		public Conversation(Socket socket, int nbClient) {
			super();
			this.socket = socket;
			this.numClient = nbClient;
		}

		public void run() {
			try {
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				
				String ip = socket.getRemoteSocketAddress().toString();
				System.out.println("Connexion du client n°"+numClient+" IP : "+ip);
				pw.println("Welcome, you are client n° "+numClient+"\n\t***** Appuyer sur Entrer pour terminer la communication *****");
				
				
				int cpt = cc.size();
				
				while(true) {
					broadcast(cpt);
					String req;
					req = br.readLine();
				
					if(req.isEmpty()) {
						
						cpt--;
						cc.remove(socket);
						cpt = cc.size();
						
						broadcast(cpt);
						socket.close();
						break;
					}
				
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public static void main(String[] args) {
		new ServerTcp().start();
	}


}
