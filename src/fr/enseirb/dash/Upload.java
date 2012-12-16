package fr.enseirb.dash;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Upload extends HttpServlet {
	int port = 5088;
	private UploadThread uploadThread = null;

	private static final long serialVersionUID = 3718648161687397934L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("name");
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println(port);
		out.close();
		uploadThread = new UploadThread(name);
		uploadThread.start();
	}
}
