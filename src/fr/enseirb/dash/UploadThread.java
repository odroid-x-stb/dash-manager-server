package fr.enseirb.dash;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;

public class UploadThread extends Thread {

	protected static Logger logger = Logger.getLogger("log.uploadthread");
	private int PORT = 5088;
	private String folderPATH = "/var/www/uploads/";
	private String SCRIPT = "/var/dash/vodDash.sh ";
	private String fileName;
	
	public UploadThread(String fileName) {
		this.fileName = fileName;
		
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
			logger.log(Level.INFO, "Opening Socket");

			serveur = new ServerSocket(PORT);
			serviceSocket = serveur.accept();
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
			// runtime.exec(SCRIPT.concat(folderPATH).concat(fileName));

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
}
