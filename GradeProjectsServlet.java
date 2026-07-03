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
 * Servlet implementation class GradeProjectsServlet
 */
@WebServlet("/GradeProjectsServlet")
public class GradeProjectsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GradeProjectsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Grade_Project(request, response);
	}
	
private void Grade_Project(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String url = "jdbc:mysql://localhost:3306/tech_log";
        String user = "root";
        String db_password = "12345678";
        RequestDispatcher view;
        
        try (Connection connection = DriverManager.getConnection(url, user, db_password);
           	PreparedStatement statement = connection.prepareStatement(mainpackage.Professor.gradeProject())) {
           	statement.setString(1, request.getParameter("gradeAssigned"));
           	statement.setString(2, request.getParameter("idForGrade"));

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
