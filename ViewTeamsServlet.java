package servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * Servlet implementation class ViewTeamsServlet
 */
@WebServlet("/ViewTeamsServlet")
public class ViewTeamsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewTeamsServlet() {
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
		View_Teams(request, response);
	}
	
	private void View_Teams (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String url = "jdbc:mysql://localhost:3306/tech_log";
        String user = "root";
        String db_password = "12345678";
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try (Connection connection = DriverManager.getConnection(url, user, db_password);
           	PreparedStatement statement = connection.prepareStatement(mainpackage.Professor.viewTeams());
           	ResultSet rs = statement.executeQuery()) {
        	           	
           	while (rs.next()) {
           	    out.println("<tr onclick='selectRow(this)'>");
           	    out.println("<td>" + rs.getInt("team_id") + "</td>");
           	    out.println("<td>" + rs.getString("members") + "</td>");
           	    out.println("</tr>");
           	}
           } catch (SQLException e) {
               System.out.println("Connection failed!");
               e.printStackTrace();
               RequestDispatcher view = request.getRequestDispatcher("/startpage.jsp");
			   view.forward(request, response);
           }
	}

}
