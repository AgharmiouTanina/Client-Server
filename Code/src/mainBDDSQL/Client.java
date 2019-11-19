/*
 * Titre du TP :		Gestionnaire d'annonces Version 1
 * 
 * Date : 				31/10/2019
 * 
 * Nom : 				AGHARMIOU
 * Prénom :				Tanina
 * Numéro étudiant : 	21961776
 * email : 				20185597@etud.univ-evry.fr
 *  
 * Nom : 				HAMOUCHE
 * Prénom :				Nassila
 * Numéro étudiant : 	21967736
 * email : 				nassilahamouche@gmail.com
 * 
 * Remarques : Lire le ReadMe.pdf inclus dans le zip
 * 
 * */

package mainBDDSQL;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	
	public Client(){
		try {
			Socket socket = new Socket("127.0.0.1", 1229);
			
			//Pour lire les messages du serveur
			InputStream inputStream = socket.getInputStream();
			InputStreamReader inputStRead = new InputStreamReader(inputStream);
			BufferedReader buffRead = new BufferedReader(inputStRead);

			//Pour écrire et envoyer des messages au serveur
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(outputStream, true);


			// line est une variable qui sert à recuperer la saisie du client en console
			String line = "";
			Scanner scanner = new Scanner(System.in);
			boolean close = false;

			String connexionSucces = "";//Pour la verification de la validité des identifiants
			String existe = "";//pour tester l'existance d'une annonce
			String lit = ""; //Sert à recevoir les annonces de la part du serveur
			String updateYN = "";//Sert à verifier si le client souhaite modifier ou pas l'annoce

			//Tant que connection_succes ne reçoit pas CONNECTED de la part du serveur la saisie des identifiants est redemandée
			while (!connexionSucces.equals("CONNECTED")) {

				System.out.println("Saisir le login : ");
				line = scanner.nextLine();//récupère la saisie du user
				pw.println(line);//envoie le login au serveur

				System.out.println("Saisir le mot de passe : ");
				line = scanner.nextLine();//récupère la saisie du user
				pw.println(line);//envoie le mot de passe au serveur

				connexionSucces = buffRead.readLine();
				System.out.println("");
				System.err.println(connexionSucces + "\n");

			}

			//Affichage du menu d'accueil
			System.out.println("\n***** Menu ***** \n");
			System.out.println("1 : Display annonces");
			System.out.println("2 : Display mes annonces");
			System.out.println("3 : Post annonce");
			System.out.println("4 : Update annonce");
			System.out.println("5 : Delete annonce");
			System.out.println("6 : Quitter\n");

			//Affiche un message de bienvenue
			System.out.println(buffRead.readLine()+"\n");

			System.out.println("Choisissez une operation dans le menu.\n");

			while (!socket.isClosed()) {

				System.out.print("[CLIENT] : ");
				//Récupère la saisie du user
				line = scanner.nextLine();
				
				//Traitement de la saisie selon le cas
				do {

					switch (line) {
					case "1":
						line = "GETANNONCES";
						break;
					case "2":
						line = "GETMYANNONCES";
						break;
					case "3":
						line = "NEWANNONCE";
						break;
					case "4":
						line = "UPDATEANNONCE";
						break;
					case "5":
						line = "DELETEANNONCE";
						break;
					case "6":
						line = "EXIT";
						break;	

					default:
						System.out.println("[CLIENT] : try again !!! choisir un chiffre entre 1 et 5");
						break;
					}
					//Envoie la requête au serveur 
					pw.println(line);
					
					//Traiter la requête selon le cas
					switch(line) {
					
					case "GETANNONCES" : {

						//Si la saisie du client est le choix 1, alors reception de toutes les annonces présentes dans la BDD 
						System.out.println(buffRead.readLine()+"\n");
						
						//Lit et affiche toutes les annonces présentes su la BDD
						while ((lit = buffRead.readLine()) != null) {//
							if (!lit.equals("DISPLAYENDED")) {
								System.out.println(lit+"\n");
							}else {
								break;//Une fois que DISPLAYENDED est reçu, sortir de la while, ce qui veut dire que toutes les annonces ont été lues
							}

						} 

						System.out.println(buffRead.readLine());//Lit le message reçu et l'affiche à la console
						break;
					}
					
					case "GETMYANNONCES" : {

						//Si la saisie du client est le choix 1, alors reception de toutes les annonces présentes dans la BDD 
						System.out.println(buffRead.readLine()+"\n");
						
						//Lit et affiche toutes les annonces présentes su la BDD
						while ((lit = buffRead.readLine()) != null) {//
							if (!lit.equals("DISPLAYENDED")) {
								System.out.println(lit+"\n");
							}else {
								break;//Une fois que DISPLAYENDED est reçu, sortir de la while, ce qui veut dire que toutes les annonces ont été lues
							}

						} 

						System.out.println(buffRead.readLine());//Lit le message reçu et l'affiche à la console
						break;
					}

					case "NEWANNONCE" : {
						//Si le client saisi le choix 2, il pourra puplier une annonce en saisissant le domaine, le prix et la description
						System.out.println("\n" + buffRead.readLine() + "\n");// Lit le message reçu du serveur et 
						
						System.out.print("Domaine : ");//Saisie du domaine
						line = scanner.nextLine();
						pw.println(line);//Envoi du domaine saisi au serveur

						System.out.print("Prix : ");//Saisie du prix
						line = scanner.nextLine();
						pw.println(line);//Envoi du prix saisi au serveur

						System.out.print("Descriptif : ");//Saisie du descriptif
						line = scanner.nextLine();
						pw.println(line);//Envoi du descriptif au serveur

						System.out.println("\n" + buffRead.readLine() + "\n"); //Lit le message reçu et l'affiche à la console
						System.out.println(buffRead.readLine() + "\n");//Lit le message reçu et l'affiche à la console
						break;

					}
					
					case "UPDATEANNONCE" : {
						//Si la saisie du client est le choix 3, alors il pourra modifier ses annonces
						System.out.print("[SERVEUR] : Saisir le numero de l'annonce que vous souhaitez modifier : ");
						line = scanner.nextLine();
						System.out.println("you wrote =========== "+ line);
						pw.println(line);//Envoie du numero de l'annonce au serveur

						existe = buffRead.readLine();//Reçoit si l'annoce existe ou pas
						
						if (existe.equals("EXIST")) {//Verifier si l'annoce existe dans la BDD
							//Reçoit l'annoce à modifer
							System.out.println("*****Annonce à modifier*****\n");

							//lit et affiche l'annonce à modifier
							while ((lit) != null) {
								
								lit = buffRead.readLine();
								
								if (!lit.equals("UPDISPLAYENDED")) {
									System.out.println("\n"+ lit + "\n");
								}
								else {
									break;
								}

							} 

							System.out.println(buffRead.readLine()); //reçoit fin annonce

							updateYN = "oui";
							
							while (updateYN.equals("oui")) {

								System.out.println(buffRead.readLine()); //Reçoit "quel champ vous modifier dans l'annonce ?"
								line = scanner.nextLine();// Envoi le champ
								pw.println(line);

								System.out.println(buffRead.readLine()); // Reçoit "saisir les modifications"

								line = scanner.nextLine(); // saisi les modifications
								pw.println(line);//envoie les modifications

								System.out.println(buffRead.readLine());//reçoit "annoce modifiée"
								System.out.println(buffRead.readLine()); //recçoit si il souhaite toujours modifier?
								updateYN = scanner.nextLine();// Repond par oui ou non
								pw.println(updateYN);//Envoi la reponse
							}
						}

						else {//Si l'annoce n'existe pas
							System.out.println(buffRead.readLine());//Reçoit le fichier n'existe pas 
						}

						break;

					}
					
					case "DELETEANNONCE" : {
						//Si le client fait le choix 3, alors il pourra supprimer un annonce
						System.out.print("[SERVEUR] : Saisir le numero de l'annonce que vous souhaiter supprimer : ");
						line = scanner.nextLine();
						pw.println(line);//

						System.out.println(buffRead.readLine());//Reçoit si l'annonce existe ou pas

						System.out.println(buffRead.readLine());// Reçoit fin de la requête 
						break;

					}

					case "EXIT" : {
						//Si le client fait le choix 5; il quitte la communication avec le serveur
						System.out.println(buffRead.readLine());
						close = true;
						socket.close();
						break;

					} 
					}			
					
					if(!close) {
						System.out.print("\n[CLIENT] : ");
						line = scanner.nextLine();
						line = line.toUpperCase();
					}
					else {
						scanner.close();
					}

				}
				while (!line.equals("EXIT"));
			}


		} catch (IOException e) {
			e.printStackTrace();
		} 
	
	}
	
	public static void main(String[] args) {
		Client c = new Client();
	}

}
