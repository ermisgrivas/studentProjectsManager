package mainpackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Student extends User{
	
	private String am;
	
	public Student(String name, String surname, String email, String password, String am, String salt) {
		super(name, surname, email, password);
		this.setAM(am);
		this.addStudent(name, surname, email, password, am, salt);
	}
	public String getAM() {
		return this.am;
	}
	public void setAM(String am) {
		this.am = am;
	}
	
	private void addStudent(String name, String surname, String email, String password, String am, String salt) {
    	String url = "jdbc:mysql://localhost:3306/tech_log";
        String user = "root";
        String db_password = "12345678";
        String insert = "INSERT INTO student (email,password,name,surname,am,salt) VALUES (?,?,?,?,?,?)";
        
        try {
            System.out.println(Student.class.getClassLoader()
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
        	statement.setString(5, am);
        	statement.setString(6, salt);

        	int rows = statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
    }
}
