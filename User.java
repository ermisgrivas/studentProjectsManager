package mainpackage;

import java.security.SecureRandom;
import java.util.Base64;
import jakarta.servlet.*;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;	
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import jakarta.servlet.RequestDispatcher;

public class User {

	private String name;
	private String surname;
	private String email;
	private String password;
	
	//setters
	public void setName(String name) {
        this.name = name;
    }
	public void setSurname(String surname) {
        this.surname = surname;
    }
	public void setPassword(String password) {
        this.password = password;
    }
	public void setEmail(String email) {
        this.email = email;
    }
    
	//getters
    public String getName(){
        return this.name;
    }
    public String getSurname(){
        return this.surname;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return this.password;
    }
    
	public User(String name, String surname, String email, String password) {
		this.setName(name);
		this.setSurname(surname);
		this.setEmail(email);
		this.setPassword(password);
	}
	
	public static String generateSalt() {
		SecureRandom random = new SecureRandom();

	    byte[] salt = new byte[16]; 
	    random.nextBytes(salt);

	    return Base64.getEncoder().encodeToString(salt);
	}
	
	public static String getSaltedPassword(String table, String email, String password) {
		String url = "jdbc:mysql://localhost:3306/tech_log";
        String user = "root";
        String db_password = "12345678";
        String select = "SELECT salt FROM "+ table + " WHERE email=?";
        
        try (Connection connection = DriverManager.getConnection(url, user, db_password);
	        	 PreparedStatement statement = connection.prepareStatement(select)) {
	        	
	        	statement.setString(1,email);
	        	ResultSet rs = statement.executeQuery();
	        	if(rs.next()) {
	        		byte[] realSalt = Base64.getDecoder().decode(rs.getString("salt"));
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
				    return hexString.toString();
	        	}
        }catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
    }
		return "";
	}
	
	public static boolean login(String userType, String email, String password, HttpSession session1) {
		String url = "jdbc:mysql://localhost:3306/tech_log";
        String user = "root";
        String db_password = "12345678";
		String salted_password = mainpackage.User.getSaltedPassword(userType, email, password);
		String select = "SELECT * FROM " + userType +" WHERE email=? AND password=?";
		
		try {
            System.out.println(Professor.class.getClassLoader().getResource("com/mysql/cj/jdbc/Driver.class"));
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully");
        } catch (Throwable t) {
            System.out.println("Driver load failed:");
            t.printStackTrace();
        }
		
		try (Connection connection = DriverManager.getConnection(url, user, db_password);
	        	 PreparedStatement statement = connection.prepareStatement(select)) {
	        	
	        	statement.setString(1,email);
	        	statement.setString(2,salted_password);
	        	ResultSet rs = statement.executeQuery();
	        	if(rs.next()) {
	        		//if a result exists (found the registered user)
	        		session1.setAttribute("name", rs.getString("name"));
	        		session1.setAttribute("surname", rs.getString("surname"));
	        		session1.setAttribute("email", rs.getString("email"));
	        		if("professor".equals(userType)) {
	        			session1.setAttribute("subject", rs.getString("subject"));
	        		}else if("student".equals(userType)) {
		        		session1.setAttribute("am", rs.getString("am"));
	        		}
	        		connection.close();
	        		return true;
	        		
	        	}else {
	        		//if the specific user doesnt exist
	        		connection.close();
	        		return false;
	        	}
	        	
		        } catch (SQLException e) {
		            System.out.println("Connection failed!");
		            e.printStackTrace();
		    }
		return false;
	}
	public void logout() {}
	public static void deleteAccount(String email, String table) {
		String url = "jdbc:mysql://localhost:3306/tech_log";
		String user = "root";
		String db_password = "12345678";
		System.out.println("deleted account " + email + " " + table);
		
		//deleting the user
		String deleteUser = "DELETE FROM " + table + " WHERE email=?";
		try (Connection connection = DriverManager.getConnection(url, user, db_password);
	        	PreparedStatement statement = connection.prepareStatement(deleteUser)) {
				statement.setString(1, email);
	        	int update = statement.executeUpdate();
		        } catch (SQLException e) {
		            System.out.println("delete failed!");
		            e.printStackTrace();
		        }
	}
}