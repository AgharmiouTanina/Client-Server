package backup.VersionArretScan;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;


public class ServerMT extends Thread{


	int cpt = 0;
	int numClient = 0;

	List<Socket> cc = new ArrayList<Socket>();
	File file = null;

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
		boolean exist = false;
		File folder = new File("Files/");
		File[] listOfFiles = folder.listFiles();
		

		int code = listOfFiles.length;

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
				PrintWriter pw = null;
				pw = new PrintWriter(os, true);

				BufferedReader brFile = null;

				String ip = socket.getRemoteSocketAddress().toString();
				System.out.println("Connexion du client n°" + this.num + " IP : "+ ip);

				pw.println("\tWelcome, you are client n° " + this.num + ".  \n");//W1

				String req ="";
				String prix = "";
				String des = "";
				String domaine = "";
				String codeAnnonce = "";

				//			System.out.println("nb files === "+listOfFiles.length);

				while(!socket.isClosed()) {

					req = br.readLine();//recupère la requête cliente R1

					switch(req.toUpperCase()) {//traite la requête selon le cas

					case "POST" :{
						/*for(File f : listOfFiles) {  	TRAITEMENT DU CODE SELON LE NUMERO CLIENT
							//code = f.l
									//listOfFiles.length;
							String a = f.getName().substring(6, 7);
							if(Integer.parseInt(a) == (cpt)) {
								code = Integer.parseInt(f.getName().substring(8, 9));

							}
						}*/
						
						
						
						System.out.println("code ====  "+code);
						pw.println("*****POST*****");
						String fileName = "Files/fileCl"+cpt+"-"+code+".txt";//Création de l'annonce 
						file = new File(fileName);//Génération du fichier de sortie RS

						
						
						
						if((file.exists())) {
							exist = true;
							/*String nameFile = file.getName(); //fileCl1-0.txt
							String[] partAnnonce = nameFile.split(".");
							String nameAnnonce = partAnnonce[0]; // fileCl1-0
							String[] partNameAnnonce = nameAnnonce.split("-");
							String codeClient = partNameAnnonce[0]; // fileCl1
							codeAnnonce = partNameAnnonce[1]; // 0*/
						}


						//		System.out.println("exist ==="+exist+"\n");

						if(exist) {
							//		System.out.println("incrément du code ");
							code++;
							System.out.println("code annonce ==== "+code);
							String fileNameN = "Files/fileCl"+cpt+"-"+code+".txt";//Création de l'annonce
							file = new File(fileNameN);//Génération du fichier de sortie RS
						}
						exist = false;
						//	System.out.println("\ncode === "+code);
						file.createNewFile();//Réinitialise le fichier RS

						FileWriter writer = new FileWriter(file.getAbsoluteFile());
						writer.write("fileCl"+cpt+"-"+code+"\n");

						domaine = br.readLine();//R2
						writer.write("Domaine : " + domaine + "\n");

						prix = br.readLine();//R3
						writer.write("Prix : " + prix + "$\n");

						des = br.readLine();//R4
						writer.write("Description : " + des + "\n");

						pw.println("[SERVEUR] : File ajoutée !");//W2
						writer.close();
						code++;
						pw.println("*****END POST*****");
						break;
					}
					case "DISPLAY":{
						//	pw.println("*****DISPLAY*****");
						listOfFiles = folder.listFiles();

						for (File file : listOfFiles) {
							pw.println("***********");//WD1
							if (file.isFile())  {
								//System.out.println(file.getName());
								pw.println(file.getName());//Renvoie le nom de l'annonce WD2

								brFile = new BufferedReader(new FileReader(file)); //Lecture du fichier 
								System.out.println(brFile.readLine());
								String lit = ""; 

								while ((lit = brFile.readLine()) != null) {
									pw.println(lit); //WD3
								} 

							}
							pw.println("***********");//WD4
						}
						brFile.close();
						pw.println("ENDFILES");


						//	pw.println("*****END DISPLAY*****");
						break;
					}
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

					case "UPDATE":{//update
						//Recuperer le numero de l'annoce à modifier
						String cd = br.readLine();

						//envoyer l'annoce à modifier au client
						String filePATH = "Files/fileCl"+cpt+"-"+cd+".txt";
						BufferedReader brFil = new BufferedReader(new FileReader(filePATH)); //Lecture du fichier 
						System.out.println(brFil.readLine());
						String lit = ""; 
						while ((lit = brFil.readLine()) != null) {
							pw.println(lit); //W4
						} 
						System.out.println("fin annonce");
						pw.println("STOP");

						//reçoit les modification
						file = new File("Files/fileCl"+cpt+"-"+cd+".txt");


						file.createNewFile();
						FileWriter writer = new FileWriter(file.getAbsoluteFile());

						BufferedWriter b = new BufferedWriter(writer);

						b.write("fileCl"+cpt+"-"+cd+"\n");

						domaine = br.readLine();//R2
						b.write("Domaine : " + domaine + "\n");

						prix = br.readLine();//R3
						b.write("Prix : " + prix + "\n");

						des = br.readLine();//R4
						b.write("Description : " + des + "\n");

						pw.println("[SERVEUR] : File modifié !");//W2
						b.close();
						brFil.close();
						break;

					} case "DELETE":{
						//Recuperer le numero de l'annoce à modifier
						String cd = br.readLine();
						System.out.println("cd ===== "+cd);
						//SUPPRIMER l'annonce
						file = new File("Files/fileCl"+cpt+"-"+cd+".txt");
						file.delete();

						pw.println("[SERVEUR] : File supprimé !");//W2
						pw.println("*****END DELETE*****");

						break;
					}
					default :{
						System.out.println("[SERVEUR] : try again .....");
						break;
					}




					}

				}

				if(socket.isClosed()){
					System.out.println("SERVER ::: socket closed !!");
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
