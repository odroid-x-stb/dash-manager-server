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

public class Tnt extends HttpServlet {
	protected static Logger logger = Logger.getLogger("log.tnt");
	private String SCRIPT = "/var/dash/tntDash.sh ";
	private static final long serialVersionUID = -2264835597803006925L;

	public Tnt() {
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
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String channel = request.getParameter("channel");
		if (channel == null) {
			logger.log(Level.INFO, "No channel parameter given !!");
			out.println(404);
		} else {
			logger.log(Level.INFO, "Request channel :" + channel);
			Runtime runtime = Runtime.getRuntime();
			logger.log(Level.INFO, "Starting VOD script");
			runtime.exec(SCRIPT.concat(channel));
			out.println(200);
		}
		out.close();
	}
}
