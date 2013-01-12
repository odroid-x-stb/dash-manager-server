package fr.enseirb.dash;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadThread extends Thread {

	protected static Logger logger = Logger.getLogger("log.uploadthread");
	private final static int PORT = 5088;
	private final static String folderPATH = "/var/www/uploads/"; 
	//espace pour la ligne de commande
	private final static String SCRIPT = "/var/dash/amine/Debug/amineDASH ";
	private String fileName;
	String ip_server = null;

	public UploadThread(String fileName) {
		this.fileName = fileName;
		ip_server = getIP();
		Handler fh = null;
		try {
			fh = new FileHandler("myLog.log");
		} catch (SecurityException e) {
			System.out.println("Logging loading failed");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Logging loading failed");
			e.printStackTrace();
		}
		logger.addHandler(fh);
	}

	public void run() {
		logger.log(Level.INFO, "Initializing");
		int read = 0;
		ServerSocket serveur = null;
		Socket serviceSocket = null;
		OutputStream out = null;
		InputStream input = null;

		try {
			logger.log(Level.INFO, "Creating :" + fileName);
			out = new FileOutputStream(folderPATH + fileName);
		} catch (FileNotFoundException e1) {
			logger.log(Level.INFO, "Failed to create :" + fileName);
			e1.printStackTrace();
		}
		try {
			// Ouverture de socket
			serveur = new ServerSocket(PORT);
			serviceSocket = serveur.accept();
			logger.log(Level.INFO, "Opening Socket");
			input = serviceSocket.getInputStream();

			BufferedInputStream inBuffer = new BufferedInputStream(input);
			BufferedOutputStream outBuffer = new BufferedOutputStream(out);

			// Reçoit du client
			read = inBuffer.read();
			logger.log(Level.INFO, "Receiving");
			while (read > -1) {
				outBuffer.write(read);
				read = inBuffer.read();
			}
			outBuffer.write(read);

			outBuffer.flush();
			logger.log(Level.INFO, "File received");
			Runtime runtime = Runtime.getRuntime();

			// Lancement du script

			logger.log(Level.INFO, "Lauching script");
			// String [] cmdArgs = { "/bin/sh", "-c",
			// SCRIPT.concat(folderPATH).concat(fileName).concat("> file.txt")
			// }; // Very important to keep the first 2 args.
			logger.log(Level.INFO, fileName);

			String cmd = SCRIPT.concat("http://")
					.concat(ip_server).concat("/VOD/")
					.concat(" ")
					.concat(folderPATH)
					.concat(" ")
					.concat(fileName.substring(0, fileName.lastIndexOf("."))
					.concat(" ")
					.concat(fileName.substring(fileName.lastIndexOf(".")))
					.concat(" > file.txt"));
			logger.log(Level.INFO, cmd);
			Process child = Runtime.getRuntime().exec(cmd);
			try {
				child.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // <-- You need this
			int exitValue = child.exitValue();
			// Now check exitValue and see if your command succeeded or not.
			logger.log(Level.INFO, "Exiting script");
			outBuffer.close();
			inBuffer.close();
			serveur.close();
			out.flush();
			out.close();
			input.close();
			serviceSocket.close();
		} catch (BindException e) {

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getIP() {
		Enumeration<NetworkInterface> interfaces = null;
		try {
			interfaces = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		while (interfaces.hasMoreElements() && interfaces != null) {
			NetworkInterface current = interfaces.nextElement();
			Enumeration<InetAddress> addresses = current.getInetAddresses();
			while (addresses.hasMoreElements()) {
				InetAddress current_addr = addresses.nextElement();
				if (current_addr.isLoopbackAddress())
					continue;
				if (current_addr instanceof Inet4Address)
					ip_server = current_addr.getHostAddress();
			}
		}
		return ip_server;
	}
}