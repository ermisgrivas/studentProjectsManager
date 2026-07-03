package servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Servlet implementation class CreateProjectServlet
 */
@WebServlet("/CreateProjectServlet")
public class CreateProjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateProjectServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//doGet will never be used so it just calls doPost
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Create_Project(request,response); 
	}
	
	private void Create_Project(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String url = "jdbc:mysql://localhost:3306/tech_log";
        String user = "root";
        String db_password = "12345678";
        RequestDispatcher view;
        
        try (Connection connection = DriverManager.getConnection(url, user, db_password);
           	PreparedStatement statement = connection.prepareStatement(mainpackage.Professor.createProject())) {
           	statement.setString(1, (String)request.getSession().getAttribute("subject"));
           	statement.setString(2, request.getParameter("name"));
           	statement.setString(3, request.getParameter("maxgrade"));
           	statement.setString(4, request.getParameter("duedate"));
           	statement.setString(5, request.getParameter("maxmembers"));
           	statement.setString(6, request.getParameter("instructions"));

           	int rows = statement.executeUpdate();
           	
           	view = request.getRequestDispatcher("/professorpage.jsp");
			view.forward(request, response);
           } catch (SQLException e) {
               System.out.println("Connection failed!");
               e.printStackTrace();
               view = request.getRequestDispatcher("/startpage.jsp");
			   view.forward(request, response);
           }
	}

}
