package fr.enseirb.dash;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Tnt extends HttpServlet {
	private static final long serialVersionUID = -2264835597803006925L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String channel = request.getParameter("channel");
		
		Runtime runtime = Runtime.getRuntime();
		runtime.exec("touch /home/sopwith/".concat(channel));
		System.out.println("touch /home/sopwith/ ".concat(channel));
		
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(200);
		out.close();
	}
}
