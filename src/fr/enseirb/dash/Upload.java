package fr.enseirb.dash;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Upload extends HttpServlet {
	protected static Logger logger = Logger.getLogger("log.uploadthread");
	int port = 5088;
	private UploadThread uploadThread = null;

	private static final long serialVersionUID = 3718648161687397934L;

	public Upload() {
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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(port);
		out.close();
		logger.log(Level.INFO, "Start Upload thread");
		uploadThread = new UploadThread(name);
		uploadThread.start();
	}
}
