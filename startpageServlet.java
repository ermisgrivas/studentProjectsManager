package servlets;

import jakarta.servlet.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;	
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
/**
 * Servlet implementation class startpageServlet
 */
@WebServlet("/startpageServlet")
public class startpageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public startpageServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("im in post");
		String action = request.getParameter("action");
        
		if("login".equals(action)) {
			
			HttpSession session1 = request.getSession(true);//creating a new session 
			synchronized(session1)
			{
				String email= request.getParameter("email");
				String password = request.getParameter("password");
				String userType = request.getParameter("userType");
				
				if(mainpackage.User.login(userType, email, password, session1)) {
					//if login successful
					RequestDispatcher view = request.getRequestDispatcher("/" + userType +"page.jsp");
					view.forward(request, response);
				}else {
					//if login unsuccessful
					RequestDispatcher view = request.getRequestDispatcher("/startpage.jsp");
					view.forward(request, response);
				}
			}
		}else if ("register".equals(action)) {
			HttpSession session1 = request.getSession(true);//creating a new session 
			synchronized(session1)
			{
				String email= request.getParameter("email");
				String password = request.getParameter("password");
				String userType = request.getParameter("userType");
				String name = request.getParameter("name");
				String surname = request.getParameter("surname");
				String salt = mainpackage.User.generateSalt();
				
				byte[] realSalt = Base64.getDecoder().decode(salt);
				 StringBuilder hexString = new StringBuilder();
			    	try {
			            MessageDigest md = MessageDigest.getInstance("SHA-1");
			            md.update(realSalt);
			            byte[] inputBytes = md.digest(password.getBytes());

			            for (byte b : inputBytes) {
			        	    hexString.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));

			            }
			        } catch (NoSuchAlgorithmException e) {
			            System.err.println("Hash algorithm not found: " + e.getMessage());
			        }
			    String salted_password = hexString.toString();
			    
			    if("student".equals(userType)){
			    	String am = request.getParameter("subject");
			    	new mainpackage.Student(name, surname, email, salted_password, am, salt);
			    }else if("professor".equals(userType)) {
			    	String subject = request.getParameter("subject");
			    	new mainpackage.Professor(name, surname, email, salted_password,subject, salt);
			    }else if("admin".equals(userType)){
			    	new mainpackage.Admin(name, surname, email, salted_password, salt);
			    }
			    RequestDispatcher view = request.getRequestDispatcher("/" + userType +"page.jsp");
				view.forward(request, response);
			}
		}
	}

}
