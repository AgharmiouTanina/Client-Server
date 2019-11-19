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
		this.socketClient = socket;//Récupération de la socket cliente
	}

	public void run() {
		try {
			//Initialisation des stream de lecture et d'écriture
			InputStream inputStream = null;
			InputStreamReader inputStreamRead = null;
			BufferedReader buffRead = null;
			OutputStream outputStream = null;
			PrintWriter printWrite = null;

			//Préparation des stream de communication entre le serveur et ses clients
			inputStream = socketClient.getInputStream();
			inputStreamRead = new InputStreamReader(inputStream);
			buffRead= new BufferedReader(inputStreamRead);
			outputStream = socketClient.getOutputStream();
			printWrite = new PrintWriter(outputStream, true);

			String ip = socketClient.getRemoteSocketAddress().toString();//Récupréation de l'adresse IP du client connecté
			System.out.println("Connexion du client IP : "+ ip);//Affichage des informations concernant le client connecté

			//Initialisation des variables
			String request ="";
			//Variables concernant les informations des annonces
			String prix = "";
			String descriptif = "";
			String domaine = "";

			//Variables concernant les informations de l'utilisateur (entrées au clavier)
			String login = "";
			String password = "";

			//Variables concernant les informations de l'utilisateur (récupérées de la bdd)
			String loginBdd = "";
			String passwordBdd = "";

			String annonceExists = "";//Existance d'une annonce
			String codeUpdate = "";//Code de l'annonce à modifier ou supprimer (entrée au clavier)
			String codeUpdateBdd = "";//Code de l'annonce à modifier (récupérée de la bdd)
			String codeDelete = "";//Code de l'annonce à supprimer (entrée au clavier)
			String codeDeleteBdd = "";//Code de l'annonce à supprimer (récupérée de la bdd)
			String updateYN = "";//Le client veut modifier (Yes) ou non (No)
			String sqlRequest = "";//Requête SQL
			String champUpdate = "";//Champ de l'annonce à modifier

			String connexionSuccess = "";//Etat de la connexion avec la base de données

			while (!connexionSuccess.equals("CONNECTED")) {//Test sur la validité des identifiants saisis

				//Entrées de l'utilisateur pour établir une connexion
				login = buffRead.readLine();//Lecture du login
				password = buffRead.readLine();//Lecture du mot de passe

				try {
					//Création de la connexion avec la base de données
					state = Connexion.connexionBD().createStatement();

					//Requête SQL
					sqlRequest= "SELECT * FROM `clients`";

					//Exécution de la requête SQL
					resultSet = state.executeQuery(sqlRequest);

					while (resultSet.next()){//Parcours de la base de données
						idClient = resultSet.getInt("id");//ID client
						loginBdd = resultSet.getString("username"); //login client
						passwordBdd = resultSet.getString("password");//password client

						if ( (loginBdd.equals(login)) && (passwordBdd.equals(password)) ) {//Test sur le login et le mot de passe saisis
							connexionSuccess = "CONNECTED";//Connexion réussie
							printWrite.println(connexionSuccess);//Envoie au client la confirmation de connexion
							break;
						}
					}//End While BDD

					if (connexionSuccess == "CONNECTED") {//Si le client est connecté, la while de validité est terminée
						break;
					}
					else {//Sinon, le serveur attend la saisie des bon identifiants
						printWrite.println("DENIED : Erreur dans le login ou le mot de passe !");
					}

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}//End While test de validité

			printWrite.println("\tBienvenue, vous êtes le client n° " + idClient);//Message de bienvenue

			while (!socketClient.isClosed()) {//Tant que la connexion avec le client n'est pas perdue

				request = buffRead.readLine();//Recupération la requête cliente

				switch(request.toUpperCase()) {//Traitement la requête selon le cas

				case "GETANNONCES" : {//Choix 1 : Affichage des annonces disponibles

					printWrite.println("\n*****Affichage des annonces*****");

					try {
						//Connexion avec la base de données
						state = Connexion.connexionBD().createStatement();

						//Requête SQL
						sqlRequest= "SELECT * FROM `annonces`";

						//Exécution de la requête
						resultSet = state.executeQuery(sqlRequest);

						while (resultSet.next()){ //Envoi des annonces à partir de la base de données au demandeur

							printWrite.println("Annonce n° : " + resultSet.getInt("id") 
							+ ", domaine : " + resultSet.getString("domaine") 
							+ ", prix : " + resultSet.getInt("prix") + "€ "
							+ ", descriptif : " + resultSet.getString("descriptif"));
						}

						printWrite.println("DISPLAYENDED");//Envoie au client que toutes les annonces ont été affichées

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					printWrite.println("*****Fin des annonces.*****");

					break;
				}//End GETANNONCES
				
				case "GETMYANNONCES" : {//Choix 2 : Affichage des annonces disponibles

					printWrite.println("\n*****Affichage de mes annonces*****");

					try {
						//Connexion avec la base de données
						state = Connexion.connexionBD().createStatement();

						//Requête SQL
						sqlRequest= "SELECT * FROM `annonces` WHERE `refCL` = " + idClient + ";";

						//Exécution de la requête
						resultSet = state.executeQuery(sqlRequest);

						while (resultSet.next()){ //Envoi des annonces à partir de la base de données au demandeur

							printWrite.println("Annonce n° : " + resultSet.getInt("id") 
							+ ", domaine : " + resultSet.getString("domaine") 
							+ ", prix : " + resultSet.getInt("prix") + "€ "
							+ ", descriptif : " + resultSet.getString("descriptif"));
						}

						printWrite.println("DISPLAYENDED");//Envoie au client que toutes les annonces ont été affichées

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					printWrite.println("*****Fin de mes annonces.*****");

					break;
				}//End GETANNONCES

				case "NEWANNONCE" : { //Choix 3 : Ajout d'une annonce

					printWrite.println("*****Création d'une annonce*****");

					//Lecture des données reçues du client
					domaine = buffRead.readLine();//Domaine de l'annonce
					prix = buffRead.readLine();//Prix de l'annonce
					descriptif = buffRead.readLine();//Descriptif de l'annonce

					try {
						//Connexion avec la base de données
						state = Connexion.connexionBD().createStatement();

						//Requête SQL
						sqlRequest = "INSERT INTO `annonces` (`domaine`, `prix`, `descriptif`, `refCL`) VALUES ('" + domaine + "', '" + prix + "', '" + descriptif + "' , '" + idClient + "');";

						//Exécution de la requête
						state.executeUpdate(sqlRequest);

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					printWrite.println("*****Saisie terminée.*****");
					printWrite.println("[SERVEUR] : Annonce créée avec succès !");

					break;
				}//End NEWANNONCE

				case "UPDATEANNONCE" : {//Choix 4 : Modification d'une annonce

					//Recupération du numéro de l'annoce à modifier
					codeUpdate = buffRead.readLine();

					try {

						//Connexion avec la base de données
						state = Connexion.connexionBD().createStatement();

						//Requête SQL
						sqlRequest= "SELECT * FROM `annonces`";

						//Exécution de la requête
						resultSet = state.executeQuery(sqlRequest);

						while (resultSet.next()) {//Parcours de la bdd à la recherche du code de l'annonce à modifier

							codeUpdateBdd = resultSet.getString("id");

							if (codeUpdateBdd.equals(codeUpdate)) {//Test sur l'existance de  l'annonce

								annonceExists = "EXIST";
								break;
							}
							else {
								annonceExists = "NOTEXIST";
							}
							//Dans tout les cas, le serveur envoie au client une réponse

						}

					} catch (SQLException e1) {
						e1.printStackTrace();
					}

					printWrite.println(annonceExists);//Envoi la réponse au client

					if (annonceExists == "EXIST") {//Si l'annonce existe

						//Le serveur envoie au client l'annoce à modifier
						try {
							//Connexion avec la base de données
							state = Connexion.connexionBD().createStatement();

							//Requête SQL
							sqlRequest= "SELECT `domaine`, `prix`, `descriptif` FROM `annonces` WHERE id = " + codeUpdate + " and `refCL` = " + idClient + ";";

							//Exécution de la requête
							resultSet = state.executeQuery(sqlRequest);

							while (resultSet.next()) {

								printWrite.println("le domaine : " + resultSet.getString("domaine") 
								+ ", le prix : " + resultSet.getInt("prix")
								+ ", descriptif : " + resultSet.getString("descriptif"));
							}

							printWrite.println("UPDISPLAYENDED");//L'annonce à modifier a été trouvée

							printWrite.println("*****Fin annonce.*****");

							updateYN = "oui";

							while (updateYN.equals("oui")) {//Tant que le client veut toujours modifier

								printWrite.println("Quel champs souhaitez-vous modifier dans l'annonce ?");

								champUpdate = buffRead.readLine();

								if (champUpdate.equals("domaine")) {

									printWrite.println("Saisissez les modifications sur le domaine : ");
									domaine = buffRead.readLine();

									//Requête SQL et exécution
									sqlRequest= "UPDATE `annonces` SET `domaine`= ? WHERE `id`=" + codeUpdate + " and `refCL` = " + idClient + ";";
									preparedState = Connexion.connexionBD().prepareStatement(sqlRequest);
									preparedState.setString(1, domaine);
									preparedState.execute();		

								}
								else 
									if (champUpdate.equals("prix")) {

										printWrite.println("Saisissez les modifications sur le prix : ");
										prix = buffRead.readLine();

										//Requête SQL et exécution
										sqlRequest= "UPDATE `annonces` SET `prix`= " + prix + " WHERE `id`=" + codeUpdate + " and `refCL` = " + idClient + ";";
										state.executeUpdate(sqlRequest);

									}
									else 
										if (champUpdate.equals("descriptif")) {

											printWrite.println("Saisissez les modifications sur le descriptif : ");
											descriptif = buffRead.readLine();

											//Requête SQL et exécution
											sqlRequest= "UPDATE `annonces` SET `descriptif`= ? WHERE id=" + codeUpdate + " and `refCL` = " + idClient + ";";
											preparedState = Connexion.connexionBD().prepareStatement(sqlRequest);
											preparedState.setString(1, descriptif);
											preparedState.execute();
										}

								printWrite.println("[SERVEUR] : Annonce modifiée avec succès !");

								printWrite.println("Souhaitez vous modifier autre chose dans cette annonce (oui / non) ? ");

								updateYN = buffRead.readLine();//Lecture de la réponse du client 

							}

						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					else {//Si l'annonce n'existe pas
						printWrite.println("\n[SERVEUR] : L'annoce demandée n'existe pas !");//envoi fin
					}

					break;
				}//End UPDATEANNONCE

				case "DELETEANNONCE" : {//Choix 5 : Suppression d'une annonce

					//Recupération du numéro de l'annoce à supprimer
					codeDelete = buffRead.readLine();
					System.out.println("codeDele === "+codeDelete);

					try {
						//Connexion avec la base de données
						state = Connexion.connexionBD().createStatement();

						//Requête SQL
						sqlRequest = "SELECT * FROM `annonces`";

						//Exécution de la requête
						resultSet = state.executeQuery(sqlRequest);

						while (resultSet.next()) {//Parcours de la bdd à la recherche du code de l'annonce à supprimer

							codeDeleteBdd = resultSet.getString("id");

							if (codeDeleteBdd.equals(codeDelete)) {

								//Connexion avec la base de données
								state = Connexion.connexionBD().createStatement();

								//Requête SQL
								sqlRequest= "DELETE FROM `annonces` WHERE id = "+codeDelete+" and `refCL` = "+idClient+" ;";

								//Exécution de la requête
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
						printWrite.println("\n[SERVEUR] : Annonce supprimé avec succès !");}

					printWrite.println("*****Fin de suppression.*****");

					break;
				}//End DELETEANNONCE

				case "EXIT" : {//Choix 6 : Quitter la conversation

					System.out.println("\nLe client " + this.idClient + " vient de terminer la communication. \n");
					printWrite.println("[SERVEUR] : Au revoir, à bientôt !");//Message de fin de communication

					//Fermeture des streams
					printWrite.close();
					buffRead.close();
					socketClient.close();// Fermeture de la socket cliente

					break;
				}//End EXIT

				default : {//Un choix autre que 1..6 a été demandé
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