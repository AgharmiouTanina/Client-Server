package other;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	public static void main(String[] args) {

		try {
			Socket socket = new Socket("127.0.0.1", 1229);

			InputStream is = socket.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			OutputStream os = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(os, true);

			String line = "";

			Scanner scan = new Scanner(System.in);

			String str = "";

			System.out.println("\n\t<- Menu -> \n");
			System.out.println("Saisir 'nb' pour connaitre le nombre de client ou 'end' pour quitter la communication.  \n");

			while((str = br.readLine()) != null) {
				System.out.println(str);//affiche un message de bienvenue
				
				line = scan.nextLine();//récupère la saisie du user
				
				line = line.toUpperCase();
				
				pw.println(line);//envoie une requête au serveur

				if(line.equals("NB")) {
					System.out.println(br.readLine());//affiche le nombre de clients connectés
				}else {
					if(line.equals("END")) {
						break;//termine la communication client-serveur
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
