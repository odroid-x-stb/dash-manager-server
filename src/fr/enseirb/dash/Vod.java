package fr.enseirb.dash;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Vod extends HttpServlet {
	String folderPath = "/var/www/VOD/";
	File mpd_folder = null;
	private static final long serialVersionUID = -239787959618612591L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/xml");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println("<vod>");

		mpd_folder = new File(folderPath);

		File[] mpd_list = mpd_folder.listFiles();
		String fileName = new String();
		String movieName = new String();
		System.out.print(mpd_list);
		for(int i=0;i<mpd_list.length;i++){ 
			if(mpd_list[i].isFile() && isMpd(mpd_list[i].getName())){
				fileName = mpd_list[i].getName();
				movieName.substring(0,movieName.length()-4);
				out.println("<movie link=\"".concat(fileName).concat("\">".concat(movieName).concat("<\\movie>")));
			} 
		}
		out.println("</vod>");


		out.close();
	}

	public static boolean isMpd(String fileName) {
		int posPoint = fileName.lastIndexOf('.');
		if (0 < posPoint && posPoint <= fileName.length() - 2) {
			fileName.substring(posPoint + 1);
			if(fileName =="mpd"){
				return true;
			}
		}
		return false;
	}
}
