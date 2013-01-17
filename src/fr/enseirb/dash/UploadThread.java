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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
			logger.log(Level.INFO, "Lauching script");
			//runtime.exec(SCRIPT.concat(folderPATH).concat(fileName));
			//execScript();
			String [] cmdArgs = { "/bin/sh", "-c", SCRIPT.concat(folderPATH).concat(fileName).concat("> file.txt") }; // Very important to keep the first 2 args.

			Process child = Runtime.getRuntime().exec(cmdArgs);
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

	public void execScript(){
		try {
			FileWriter outputfile = new FileWriter("toto.log", true); 
			PrintWriter log = new PrintWriter (outputfile);
			log.println (" string4");

			//	Runtime.getRuntime().exec("./doit3");

			ProcessBuilder pb = new ProcessBuilder("vodDash.sh");

			Map<String, String> env = pb.environment();

			// set up the working directory.
			pb.directory( new File( "/var/dash/" ) );

			// merge child's error and normal output streams.
			pb.redirectErrorStream( true );

			// Print out ProcessBuilder Information
			List<String> pblist = new ArrayList<String> ();
			pblist = pb.command ();
			log.println ("ProcessBuilder object contains:");
			for (String cmd : pblist) {
				log.println (cmd);
			}
			log.println ("Before pb.start");
			Process p = pb.start();		
			log.println ("After pb.start");
			log.close ();

		} catch (IOException e) {	
			e.printStackTrace();		
		} 
	}
}