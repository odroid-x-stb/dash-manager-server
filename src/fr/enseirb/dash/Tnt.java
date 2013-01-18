package fr.enseirb.dash;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Tnt extends HttpServlet {
	private final static String PORT = "8888";
	protected static Logger logger = Logger.getLogger("log.tnt");
	private String SCRIPT = "/var/dash/tnt/tnt ";
	String ip_server = null;
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
		ip_server = getIP();
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
			String tntPath = ip_server.concat(":").concat(PORT);
			runtime.exec(SCRIPT
					.concat(channel)
					.concat(" ")
					.concat(tntPath));
			out.println();
		}
		out.close();
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
