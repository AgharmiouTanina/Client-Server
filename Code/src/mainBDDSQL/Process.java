/*
 * Titre du TP :		Gestionnaire d'annonces Version 1
 * 
 * Date : 				31/10/2019
 * 
 * Nom : 				AGHARMIOU
 * Pr�nom :				Tanina
 * Num�ro �tudiant : 	21961776
 * email : 				20185597@etud.univ-evry.fr
 * 
 * Nom : 				HAMOUCHE
 * Pr�nom :				Nassila
 * Num�ro �tudiant : 	21967736
 * email : 				nassilahamouche@gmail.com
 *  
 * 
 * Remarques : Lire le ReadMe.pdf inclus dans le zip
 * 
 * */
package mainBDDSQL;

import java.io.*;
import java.net.*;
import java.sql.*;

class Process extends Thread{

	Statement state = null;
	ResultSet resultSet = null;
	PreparedStatement preparedState = null;

	private Socket socketClient;

	int idClient = 0;


	public Process(Socket socket) {//Constrcuteur de la classe Process qui prend en argument une socket cliente
		super();
		this.socketClient = socket;//R�cup�ration de la socket cliente
	}

	public void run() {
		try {
			//Initialisation des stream de lecture et d'�criture
			InputStream inputStream = null;
			InputStreamReader inputStreamRead = null;
			BufferedReader buffRead = null;
			OutputStream outputStream = null;
			PrintWriter printWrite = null;

			//Pr�paration des stream de communication entre le serveur et ses clients
			inputStream = socketClient.getInputStream();
			inputStreamRead = new InputStreamReader(inputStream);
			buffRead= new BufferedReader(inputStreamRead);
			outputStream = socketClient.getOutputStream();
			printWrite = new PrintWriter(outputStream, true);

			String ip = socketClient.getRemoteSocketAddress().toString();//R�cupr�ation de l'adresse IP du client connect�
			System.out.println("Connexion du client IP : "+ ip);//Affichage des informations concernant le client connect�

			//Initialisation des variables
			String request ="";
			//Variables concernant les informations des annonces
			String prix = "";
			String descriptif = "";
			String domaine = "";

			//Variables concernant les informations de l'utilisateur (entr�es au clavier)
			String login = "";
			String password = "";

			//Variables concernant les informations de l'utilisateur (r�cup�r�es de la bdd)
			String loginBdd = "";
			String passwordBdd = "";

			String annonceExists = "";//Existance d'une annonce
			String codeUpdate = "";//Code de l'annonce � modifier ou supprimer (entr�e au clavier)
			String codeUpdateBdd = "";//Code de l'annonce � modifier (r�cup�r�e de la bdd)
			String codeDelete = "";//Code de l'annonce � supprimer (entr�e au clavier)
			String codeDeleteBdd = "";//Code de l'annonce � supprimer (r�cup�r�e de la bdd)
			String updateYN = "";//Le client veut modifier (Yes) ou non (No)
			String sqlRequest = "";//Requ�te SQL
			String champUpdate = "";//Champ de l'annonce � modifier

			String connexionSuccess = "";//Etat de la connexion avec la base de donn�es

			while (!connexionSuccess.equals("CONNECTED")) {//Test sur la validit� des identifiants saisis

				//Entr�es de l'utilisateur pour �tablir une connexion
				login = buffRead.readLine();//Lecture du login
				password = buffRead.readLine();//Lecture du mot de passe

				try {
					//Cr�ation de la connexion avec la base de donn�es
					state = Connexion.connexionBD().createStatement();

					//Requ�te SQL
					sqlRequest= "SELECT * FROM `clients`";

					//Ex�cution de la requ�te SQL
					resultSet = state.executeQuery(sqlRequest);

					while (resultSet.next()){//Parcours de la base de donn�es
						idClient = resultSet.getInt("id");//ID client
						loginBdd = resultSet.getString("username"); //login client
						passwordBdd = resultSet.getString("password");//password client

						if ( (loginBdd.equals(login)) && (passwordBdd.equals(password)) ) {//Test sur le login et le mot de passe saisis
							connexionSuccess = "CONNECTED";//Connexion r�ussie
							printWrite.println(connexionSuccess);//Envoie au client la confirmation de connexion
							break;
						}
					}//End While BDD

					if (connexionSuccess == "CONNECTED") {//Si le client est connect�, la while de validit� est termin�e
						break;
					}
					else {//Sinon, le serveur attend la saisie des bon identifiants
						printWrite.println("DENIED : Erreur dans le login ou le mot de passe !");
					}

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}//End While test de validit�

			printWrite.println("\tBienvenue, vous �tes le client n� " + idClient);//Message de bienvenue

			while (!socketClient.isClosed()) {//Tant que la connexion avec le client n'est pas perdue

				request = buffRead.readLine();//Recup�ration la requ�te cliente

				switch(request.toUpperCase()) {//Traitement la requ�te selon le cas

				case "GETANNONCES" : {//Choix 1 : Affichage des annonces disponibles

					printWrite.println("\n*****Affichage des annonces*****");

					try {
						//Connexion avec la base de donn�es
						state = Connexion.connexionBD().createStatement();

						//Requ�te SQL
						sqlRequest= "SELECT * FROM `annonces`";

						//Ex�cution de la requ�te
						resultSet = state.executeQuery(sqlRequest);

						while (resultSet.next()){ //Envoi des annonces � partir de la base de donn�es au demandeur

							printWrite.println("Annonce n� : " + resultSet.getInt("id") 
							+ ", domaine : " + resultSet.getString("domaine") 
							+ ", prix : " + resultSet.getInt("prix") + "� "
							+ ", descriptif : " + resultSet.getString("descriptif"));
						}

						printWrite.println("DISPLAYENDED");//Envoie au client que toutes les annonces ont �t� affich�es

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					printWrite.println("*****Fin des annonces.*****");

					break;
				}//End GETANNONCES
				
				case "GETMYANNONCES" : {//Choix 2 : Affichage des annonces disponibles

					printWrite.println("\n*****Affichage de mes annonces*****");

					try {
						//Connexion avec la base de donn�es
						state = Connexion.connexionBD().createStatement();

						//Requ�te SQL
						sqlRequest= "SELECT * FROM `annonces` WHERE `refCL` = " + idClient + ";";

						//Ex�cution de la requ�te
						resultSet = state.executeQuery(sqlRequest);

						while (resultSet.next()){ //Envoi des annonces � partir de la base de donn�es au demandeur

							printWrite.println("Annonce n� : " + resultSet.getInt("id") 
							+ ", domaine : " + resultSet.getString("domaine") 
							+ ", prix : " + resultSet.getInt("prix") + "� "
							+ ", descriptif : " + resultSet.getString("descriptif"));
						}

						printWrite.println("DISPLAYENDED");//Envoie au client que toutes les annonces ont �t� affich�es

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					printWrite.println("*****Fin de mes annonces.*****");

					break;
				}//End GETANNONCES

				case "NEWANNONCE" : { //Choix 3 : Ajout d'une annonce

					printWrite.println("*****Cr�ation d'une annonce*****");

					//Lecture des donn�es re�ues du client
					domaine = buffRead.readLine();//Domaine de l'annonce
					prix = buffRead.readLine();//Prix de l'annonce
					descriptif = buffRead.readLine();//Descriptif de l'annonce

					try {
						//Connexion avec la base de donn�es
						state = Connexion.connexionBD().createStatement();

						//Requ�te SQL
						sqlRequest = "INSERT INTO `annonces` (`domaine`, `prix`, `descriptif`, `refCL`) VALUES ('" + domaine + "', '" + prix + "', '" + descriptif + "' , '" + idClient + "');";

						//Ex�cution de la requ�te
						state.executeUpdate(sqlRequest);

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					printWrite.println("*****Saisie termin�e.*****");
					printWrite.println("[SERVEUR] : Annonce cr��e avec succ�s !");

					break;
				}//End NEWANNONCE

				case "UPDATEANNONCE" : {//Choix 4 : Modification d'une annonce

					//Recup�ration du num�ro de l'annoce � modifier
					codeUpdate = buffRead.readLine();

					try {

						//Connexion avec la base de donn�es
						state = Connexion.connexionBD().createStatement();

						//Requ�te SQL
						sqlRequest= "SELECT * FROM `annonces`";

						//Ex�cution de la requ�te
						resultSet = state.executeQuery(sqlRequest);

						while (resultSet.next()) {//Parcours de la bdd � la recherche du code de l'annonce � modifier

							codeUpdateBdd = resultSet.getString("id");

							if (codeUpdateBdd.equals(codeUpdate)) {//Test sur l'existance de  l'annonce

								annonceExists = "EXIST";
								break;
							}
							else {
								annonceExists = "NOTEXIST";
							}
							//Dans tout les cas, le serveur envoie au client une r�ponse

						}

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					printWrite.println(annonceExists);//Envoi la r�ponse au client

					if (annonceExists == "EXIST") {//Si l'annonce existe

						//Le serveur envoie au client l'annoce � modifier
						try {
							//Connexion avec la base de donn�es
							state = Connexion.connexionBD().createStatement();

							//Requ�te SQL
							sqlRequest= "SELECT `domaine`, `prix`, `descriptif` FROM `annonces` WHERE id = " + codeUpdate + " and `refCL` = " + idClient + ";";

							//Ex�cution de la requ�te
							resultSet = state.executeQuery(sqlRequest);

							while (resultSet.next()) {

								printWrite.println("le domaine : " + resultSet.getString("domaine") 
								+ ", le prix : " + resultSet.getInt("prix")
								+ ", descriptif : " + resultSet.getString("descriptif"));
							}

							printWrite.println("UPDISPLAYENDED");//L'annonce � modifier a �t� trouv�e

							printWrite.println("*****Fin annonce.*****");

							updateYN = "oui";

							while (updateYN.equals("oui")) {//Tant que le client veut toujours modifier

								printWrite.println("Quel champs souhaitez-vous modifier dans l'annonce ?");

								champUpdate = buffRead.readLine();

								if (champUpdate.equals("domaine")) {

									printWrite.println("Saisissez les modifications sur le domaine : ");
									domaine = buffRead.readLine();

									//Requ�te SQL et ex�cution
									sqlRequest= "UPDATE `annonces` SET `domaine`= ? WHERE `id`=" + codeUpdate + " and `refCL` = " + idClient + ";";
									preparedState = Connexion.connexionBD().prepareStatement(sqlRequest);
									preparedState.setString(1, domaine);
									preparedState.execute();		

								}
								else 
									if (champUpdate.equals("prix")) {

										printWrite.println("Saisissez les modifications sur le prix : ");
										prix = buffRead.readLine();

										//Requ�te SQL et ex�cution
										sqlRequest= "UPDATE `annonces` SET `prix`= " + prix + " WHERE `id`=" + codeUpdate + " and `refCL` = " + idClient + ";";
										state.executeUpdate(sqlRequest);

									}
									else 
										if (champUpdate.equals("descriptif")) {

											printWrite.println("Saisissez les modifications sur le descriptif : ");
											descriptif = buffRead.readLine();

											//Requ�te SQL et ex�cution
											sqlRequest= "UPDATE `annonces` SET `descriptif`= ? WHERE id=" + codeUpdate + " and `refCL` = " + idClient + ";";
											preparedState = Connexion.connexionBD().prepareStatement(sqlRequest);
											preparedState.setString(1, descriptif);
											preparedState.execute();
										}

								printWrite.println("[SERVEUR] : Annonce modifi�e avec succ�s !");

								printWrite.println("Souhaitez vous modifier autre chose dans cette annonce (oui / non) ? ");

								updateYN = buffRead.readLine();//Lecture de la r�ponse du client 

							}

						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					else {//Si l'annonce n'existe pas
						printWrite.println("\n[SERVEUR] : L'annoce demand�e n'existe pas !");//envoi fin
					}

					break;
				}//End UPDATEANNONCE

				case "DELETEANNONCE" : {//Choix 5 : Suppression d'une annonce

					//Recup�ration du num�ro de l'annoce � supprimer
					codeDelete = buffRead.readLine();
					System.out.println("codeDele === "+codeDelete);

					try {
						//Connexion avec la base de donn�es
						state = Connexion.connexionBD().createStatement();

						//Requ�te SQL
						sqlRequest = "SELECT * FROM `annonces`";

						//Ex�cution de la requ�te
						resultSet = state.executeQuery(sqlRequest);

						while (resultSet.next()) {//Parcours de la bdd � la recherche du code de l'annonce � supprimer

							codeDeleteBdd = resultSet.getString("id");

							if (codeDeleteBdd.equals(codeDelete)) {

								//Connexion avec la base de donn�es
								state = Connexion.connexionBD().createStatement();

								//Requ�te SQL
								sqlRequest= "DELETE FROM `annonces` WHERE id = "+codeDelete+" and `refCL` = "+idClient+" ;";

								//Ex�cution de la requ�te
								state.executeUpdate(sqlRequest);

								annonceExists = "EXIST";
								break;

							}
							else {
								annonceExists = "NOTEXIST";
							}

						}

					} catch (SQLException e1) {
						e1.printStackTrace();
					}


					if (annonceExists == "NOTEXIST") {

						printWrite.println("\n[SERVEUR] : L'annonce n'existe pas ! ");
					}
					else {
						printWrite.println("\n[SERVEUR] : Annonce supprim� avec succ�s !");}

					printWrite.println("*****Fin de suppression.*****");

					break;
				}//End DELETEANNONCE

				case "EXIT" : {//Choix 6 : Quitter la conversation

					System.out.println("\nLe client " + this.idClient + " vient de terminer la communication. \n");
					printWrite.println("[SERVEUR] : Au revoir, � bient�t !");//Message de fin de communication

					//Fermeture des streams
					printWrite.close();
					buffRead.close();
					socketClient.close();// Fermeture de la socket cliente

					break;
				}//End EXIT

				default : {//Un choix autre que 1..6 a �t� demand�
					System.out.println("[SERVEUR] : try again ...");
					break;
				}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}	
	}	
}