package com.cq.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebDataSaveServlet extends HttpServlet {

	/**
	 * 
	 */
	Properties properties = new Properties();
	
	
	private static final long serialVersionUID = 1L;

	/**
		 * Constructor of the object.
	 * @throws IOException 
		 */
	public WebDataSaveServlet() throws IOException {
		super();
		
		
	}
	
	
	
	

	/**
		 * Destruction of the servlet. <br>
		 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
		 * The doGet method of the servlet. <br>
		 *
		 * This method is called when a form has its tag value method equals to get.
		 * 
		 * @param request the request send by the client to the server
		 * @param response the response send by the server to the client
		 * @throws ServletException if an error occurred
		 * @throws IOException if an error occurred
		 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		properties.load(getServletContext().getResourceAsStream("/WEB-INF/token.properties"));
		String token = properties.getProperty("token");
		String urlToken = request.getParameter("token");
		if(token.equals(urlToken)) {
			response.setContentType("text/html;charset=gbk");
			PrintWriter out = response.getWriter();
			//WebDataSave mWebDataSave = new WebDataSave();
			out.print(WebDataSave.DataJson);
			out.flush();
			out.close();
		}else {
			response.setContentType("text/html;charset=gbk");
			PrintWriter out = response.getWriter();
			//WebDataSave mWebDataSave = new WebDataSave();
			out.print("权限不足,请求错误");
			out.flush();
			out.close();
		}
		
	}

	/**
		 * The doPost method of the servlet. <br>
		 *
		 * This method is called when a form has its tag value method equals to post.
		 * 
		 * @param request the request send by the client to the server
		 * @param response the response send by the server to the client
		 * @throws ServletException if an error occurred
		 * @throws IOException if an error occurred
		 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
		 * Initialization of the servlet. <br>
		 *
		 * @throws ServletException if an error occurs
		 */
	public void init() throws ServletException {
		// Put your code here
	}

}
