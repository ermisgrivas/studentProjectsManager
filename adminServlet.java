package servlets;
import java.util.ArrayList;
import java.util.Base64;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.annotation.MultipartConfig;

import java.io.InputStream;
import java.sql.*;
import java.util.List;
/**
 * Servlet implementation class adminServlet
 */
@WebServlet("/adminServlet")
public class adminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
    public adminServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private void forwardWithContent(HttpServletRequest request, HttpServletResponse response, String contentHtml)throws ServletException, IOException {

		request.setAttribute("contentHtml", contentHtml);
		
		RequestDispatcher rd = request.getRequestDispatcher("/adminpage.jsp");
		rd.forward(request, response);
		}
		
	private String escapeHtml(String value) {
	if (value == null) {
		return "";
	}
	
	return value
	.replace("&", "&amp;")
	.replace("<", "&lt;")
	.replace(">", "&gt;")
	.replace("\"", "&quot;")
	.replace("'", "&#39;");
	}

    private Connection getConnection() throws SQLException {
    	String url = "jdbc:mysql://localhost:3306/tech_log";
		String user = "root";
		String db_password = "12345678";
        return DriverManager.getConnection(url, user, db_password);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if("register".equals(action))	{
			
			//add user
			
			String email= request.getParameter("email");
			String password = request.getParameter("password");
			String userType = request.getParameter("registerType");
			String name = request.getParameter("name");
			String surname = request.getParameter("surname");
			String salt = mainpackage.User.generateSalt();
			
			//System.out.print(email + password);
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
		    RequestDispatcher view = request.getRequestDispatcher("/adminpage.jsp");
			view.forward(request, response);
		}else if ("deleteUser".equals(action)) {
			
			//deleteUser from all users
			
			String email = request.getParameter("email");
			String table = request.getParameter("type").toLowerCase();
			mainpackage.User.deleteAccount(email, table);
			
			RequestDispatcher view = request.getRequestDispatcher("/adminpage.jsp");
			view.forward(request, response);
		}else if ("deleteStudent".equals(action)) {
			
			//deleteStudent from requests
			
			String email = request.getParameter("selectedEmail");
			mainpackage.User.deleteAccount(email, "student");
			RequestDispatcher view = request.getRequestDispatcher("/adminpage.jsp");
			view.forward(request, response);
		}else if ("rejectStudent".equals(action) || "rejectProfessor".equals(action)) {
			
			//reject a request 
			
			String req = request.getParameter("requestId");
			String url = "jdbc:mysql://localhost:3306/tech_log";
			String user = "root";
			String db_password = "12345678";
			System.out.println(req + " bla");
			//deleting any existing requests
			String deleteRequests = "DELETE FROM requests WHERE request_id=?";
			try (Connection connection = DriverManager.getConnection(url, user, db_password);
		        	PreparedStatement statement = connection.prepareStatement(deleteRequests)) {
					statement.setString(1, req);
		        	int update = statement.executeUpdate();
			        } catch (SQLException e) {
			            System.out.println("Connection failed!");
			            e.printStackTrace();
			        }
			RequestDispatcher view = request.getRequestDispatcher("/adminpage.jsp");
			view.forward(request, response);
		}else if ("deleteProfessor".equals(action)) {
			
			//deleteProfessor from requests

			String email = request.getParameter("selectedEmail");
			mainpackage.User.deleteAccount(email, "professor");
			RequestDispatcher view = request.getRequestDispatcher("/adminpage.jsp");
			view.forward(request, response);
		}else if ("selectStudent".equals(action)) {
			String email = request.getParameter("email");
			System.out.println(email);
			forwardWithContent(request, response, email);
		}else if ("sendStudentMessage".equals(action)) {
			String m =request.getParameter("messageText");
			String studentEmail = request.getParameter("studentEmail");
			String adminEmail = request.getParameter("adminEmail");
			String adminName = request.getParameter("adminName");
			System.out.println(studentEmail + " " + adminEmail);
			String sql =
					"INSERT INTO chat_messages(student_email, admin_email, sender_email, sender_name, sender_role, content, sent_at) " +
					"VALUES (?, ?, ?, ?, 'admin', ?, NOW())";
					
						try (Connection con = getConnection();
						PreparedStatement ps = con.prepareStatement(sql)) {
						
						ps.setString(1, studentEmail);
						ps.setString(2, adminEmail);
						ps.setString(3, adminEmail);
						ps.setString(4, adminName);
						ps.setString(5, m);
						
						ps.executeUpdate();
						}catch (SQLException e) {
						    e.printStackTrace();
						}
						forwardWithContent(request, response, studentEmail);
			
		}else if ("backToStudentSelection".equals(action)) {
			RequestDispatcher view = request.getRequestDispatcher("/adminpage.jsp");
			view.forward(request, response);
		}
	}

}