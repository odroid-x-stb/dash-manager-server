package fr.enseirb.dash;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class UploadThread extends Thread {

	private String fileName;

	public UploadThread(String fileName) {
		this.fileName = fileName;
	}

	public void run() {

		System.out.println("Initializing");
		int read = 0;
		ServerSocket serveur = null;
		Socket serviceSocket = null;
		OutputStream out = null;
		InputStream input = null;

		try {
			System.out.println("Creating :" + fileName);
			out = new FileOutputStream("/home/sopwith/android/" + fileName);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			System.out.println("Opening Socket");

			serveur = new ServerSocket(5088);
			serviceSocket = serveur.accept();
			input = serviceSocket.getInputStream();

			BufferedInputStream inBuffer = new BufferedInputStream(input);
			BufferedOutputStream outBuffer = new BufferedOutputStream(out);

			// Reçoit du client
			read = inBuffer.read();
			System.out.println("Receiving");
			while (read > -1) {
				outBuffer.write(read);
				read = inBuffer.read();
			}
			outBuffer.write(read);

			outBuffer.flush();
			System.out.println("File received");
			outBuffer.close();
			inBuffer.close();
			serveur.close();
			out.flush();
			out.close();
			input.close();
			serviceSocket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
