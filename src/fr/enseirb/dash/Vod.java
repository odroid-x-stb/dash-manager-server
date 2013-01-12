package fr.enseirb.dash;

import java.io.File;
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

public class Vod extends HttpServlet {
	protected static Logger logger = Logger.getLogger("log.uploadthread");
	String HTTP_SERVER_PATH = "/var/www/";
	String VODfolder = "VOD/";
	File mpd_folder = null;
	String ip_server = null;
	private static final long serialVersionUID = -239787959618612591L;

	public Vod() {
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
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println("<vod>");

		mpd_folder = new File(HTTP_SERVER_PATH.concat(VODfolder));

		File[] mpd_list = mpd_folder.listFiles();
		String fileName = new String();
		String movieName = new String();
		for (int i = 0; i < mpd_list.length; i++) {
			if (mpd_list[i].isFile() && isMpd(mpd_list[i].getName())) {
				fileName = mpd_list[i].getName();
				movieName = fileName.substring(0, fileName.length() - 4);
				out.println("<movie link=\"http://".concat(ip_server)
						.concat("/").concat(VODfolder).concat(fileName)
						.concat("\">".concat(movieName).concat("</movie>")));
			}
		}
		out.println("</vod>");
		logger.log(Level.INFO, "VOD xml generated");

		out.close();
	}

	public static boolean isMpd(String fileName) {
		int posPoint = fileName.lastIndexOf('.');
		if (0 < posPoint && posPoint <= fileName.length() - 2) {
			fileName = fileName.substring(posPoint + 1);

			if (fileName.equals("mpd")) {
				return true;
			}
		}
		return false;
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
