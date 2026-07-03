package servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Servlet implementation class ProfessorDeleteServlet
 */
@WebServlet("/ProfessorDeleteServlet")
public class ProfessorDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProfessorDeleteServlet() {
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
		try {
			requestToDelete(request, response, (String)request.getSession().getAttribute("email"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void requestToDelete(HttpServletRequest request, HttpServletResponse response, String email) throws SQLException, ServletException, IOException {

              HttpSession session = request.getSession(false);
              String url = "jdbc:mysql://localhost:3306/tech_log";
              String user = "root";
              String db_password = "12345678";
              RequestDispatcher rd;

              String name = session.getAttribute("name") == null
               ? ""
               : session.getAttribute("name").toString();

               String sql =
                 "INSERT INTO requests(name, email, role) " +
                 "VALUES (?, ?, ?)";

                  try (Connection connection = DriverManager.getConnection(url, user, db_password);
                  PreparedStatement ps = connection.prepareStatement(sql)) {

                     ps.setString(1, name);
                     ps.setString(2, email);
                     ps.setString(3, "professor");

                     ps.executeUpdate();
                     rd = request.getRequestDispatcher("/professorpage.jsp");
         			 rd.forward(request, response);
                      } catch (Exception e) {
                    	  e.printStackTrace();
                    	  rd = request.getRequestDispatcher("/startpage.jsp");
              			  rd.forward(request, response);
                      }
                  
                  

	}
}
