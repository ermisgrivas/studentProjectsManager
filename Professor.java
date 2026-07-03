package mainpackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Professor extends User{

	private String subject;
	
	public Professor(String name, String surname, String email, String password, String subject, String salt) {
		super(name, surname, email, password);
		this.setSubject(subject);
		this.addProfessor(name, surname, email, password, subject, salt);
	}
	
	public String getSubject() {
		return this.subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private void addProfessor(String name, String surname, String email, String password, String subject, String salt) {
    	String url = "jdbc:mysql://localhost:3306/tech_log";
        String user = "root";
        String db_password = "12345678";
        String insert = "INSERT INTO professor (email,password,name,surname,SUBJECT,salt) VALUES (?,?,?,?,?,?)";
        
        try {
            System.out.println(Professor.class.getClassLoader()
                .getResource("com/mysql/cj/jdbc/Driver.class"));

            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Driver loaded successfully");
        } catch (Throwable t) {
            System.out.println("Driver load failed:");
            t.printStackTrace();
        }
        
        try (Connection connection = DriverManager.getConnection(url, user, db_password);
        	 PreparedStatement statement = connection.prepareStatement(insert)) {
        	statement.setString(1, email);
        	statement.setString(2, password);
        	statement.setString(3, name);
        	statement.setString(4, surname);
        	statement.setString(5, subject);
        	statement.setString(6, salt);

        	int rows = statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
	
	public static String createProject() {
		return "INSERT INTO project (subject, name, max_grade, date, team_size, description) VALUES(?,?,?,?,?,?)";
	}
	
	public static String viewTeams() {
		return "SELECT team_id, GROUP_CONCAT(email SEPARATOR ', ') AS members FROM student_team GROUP BY team_id";
	}
	
	public static String viewProjects() {
		return "SELECT s.team_id, s.file_name, s.submit_date FROM submissions AS s JOIN teams AS t ON s.team_id = t.id WHERE t.grade IS NULL";
	}
	
	public static String gradeProject() {
		return "UPDATE teams SET grade = ? WHERE id = ?";
	}
}
