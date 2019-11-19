package other;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

public class ServerMT extends Thread{

	
	int cpt = 0;
	int numClient = 0;
	List<Socket> cc = new ArrayList<Socket>();
	
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(1229);
			
			while(true) {
				Socket s = ss.accept();
				++cpt;//compte le nombre de client
				++numClient;//affecte un numéro au client
				cc.add(s);
				
				new Conversation(s, numClient).start();
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void broadcast(int i) {//diffuse le nombre de clients connectés
		PrintWriter ppw = null;
		int a = i;
		
		for(int g = 0; g < cc.size(); g++) {
			try {	
				ppw = new PrintWriter(cc.get(g).getOutputStream(), true);
				ppw.println("compteur client = "+ a + "\n");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class Conversation extends Thread{
		
		private Socket socket;
		public int num;

		public Conversation(Socket socket, int j) {
			super();
			this.socket = socket;
			this.num = j;
		}

		public void run() {
			try {
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				
				String ip = socket.getRemoteSocketAddress().toString();
				System.out.println("Connexion du client n°" + this.num + " IP : "+ ip);
				
				pw.println("\tWelcome, you are client n° " + this.num + ".  \n");

				String req ="";
			
				while(!socket.isClosed()) {
				
					req = br.readLine();//recupère la requête cliente
				
					switch(req.toUpperCase()) {//traite la requête selon le cas
						case "NB" :{
							
							pw.println("compteur client = "+ cpt + "\n");
						
							break;
						}
						
						case "END":{
							System.out.println("\t\tLe client " + this.num + " vient de terminer la communication. \n");
							cpt--;//déconnexion d'un client
							cc.remove(socket);
						//	++this.nbClient;
							pw.println("compteur client = "+ cpt + "\n");
					
							pw.close();
										
							br.close();
							socket.close();
							break;
						}
						
					}
				
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}	
	}
	
	public static void main(String[] args) {
		new ServerMT().start();
	}


}
