package backup;
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
			String prix = "";
			String des = "";
			String domaine = "";
			String aff = "";

			System.out.println("\n\t<- Menu -> \n");
			System.out.println("A : Display annonces");
			System.out.println("B : Post annonce");
			System.out.println("C : Update annonce");
			System.out.println("D : Delete annonce");
			/*	System.out.println("D : Retour menu");
			System.out.println("D : Quitter");*/

			//	System.out.println("Saisir 'nb' pour connaitre le nombre de client ou 'end' pour quitter la communication.  \n");

			while(!socket.isClosed()) {//R1
				str = br.readLine();
				System.out.println(str);//affiche un message de bienvenue
				//	while(scan.hasNextLine()) {
				line = scan.nextLine();//récupère la saisie du user

				line = line.toUpperCase();

				pw.println(line);//envoie une requête au serveur W1

				switch(line) {
				case "POST":{
					System.out.println("\n" + br.readLine());
					System.out.print("domaine : ");//Saisie du domaine
					line = scan.nextLine();
					pw.println(line);//W2

					System.out.print("prix : ");//Saisie du prix
					line = scan.nextLine();
					pw.println(line);//W3

					System.out.print("descriptif : ");//Saisie du descriptif
					line = scan.nextLine();
					pw.println(line);//W4

					System.out.println("\n" + br.readLine() + "\n"); //R2
					System.out.println(br.readLine() + "\n");
					break;
				}

				case "DISPLAY":{
					System.out.println(br.readLine()+"\n");
					aff = br.readLine();//R3
					System.out.println(aff);
					String lit = "";
					while ((lit = br.readLine()) != null) {//R4

						System.out.println(lit+"\n"); 
					} 
					System.out.println(br.readLine());
					break;
				}
				case "END":{
					scan.close();
					System.out.println("vous avez quitter");
					socket.close();
					break;
				}case "UPDATE":{
					System.out.print("saisir le numero de l'annoce que vous souhaiter modifier : ");
					line = scan.nextLine();
					pw.println(line);//W5
					
					//reçoit l'annoce à modifer
					System.out.println("l'annoce à modifier : ");
					System.out.println(br.readLine()); //R5
					String lit = "";
					while ((lit = br.readLine()) != null) {//R4
						if (!lit.equals("STOP")) {
							System.out.println(lit+"\n");
						}else {
							break;
						}
						 
					} 
					
					//envoie les modification
					System.out.println("saisir les modifications : ");
					System.out.print("domaine : ");//Saisie du domaine
					line = scan.nextLine();
					pw.println(line);//W6

					System.out.print("prix : ");//Saisie du prix
					line = scan.nextLine();
					pw.println(line);//W7

					System.out.print("descriptif : ");//Saisie du descriptif
					line = scan.nextLine();
					pw.println(line);//W8
					
					System.out.println(br.readLine()); //R2
                    break;
					
				} case "DELETE":{
					System.out.print("saisir le numero de l'annonce que vous souhaiter supprimer : ");
					line = scan.nextLine();
					pw.println(line);//W5
					
					System.out.println(br.readLine()); //R2
                    break;
					
				}
				default :{
					System.out.println("try again .....");
					break;
				}



				//}
				/*	if(line.equals("NB")) {
					System.out.println(br.readLine());//affiche le nombre de clients connectés
				}else {
					if(line.equals("END")) {
						break;//termine la communication client-serveur
					}
				}*/
				}
					System.out.println("next !!");
				
			}
			if(socket.isClosed()){
				System.out.println("socket closed !!");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
