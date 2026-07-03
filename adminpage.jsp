<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%
String url = "jdbc:mysql://localhost:3306/tech_log";
String user = "root";
String db_password = "12345678"; %>

<%! 
public String escapeHtml(String value) {
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
public String reverseEscapeHtml(String value) {
	if (value == null) {
		return "";
	}
	
	return value
	.replace("&amp;", "&")
	.replace("&lt;", "<")
	.replace("&gt;", ">")
	.replace("&quot;", "\"")
	.replace("&#39;", "'");
	}
private Connection getConnection() throws SQLException {
	String url = "jdbc:mysql://localhost:3306/tech_log";
	String user = "root";
	String db_password = "12345678";
    return DriverManager.getConnection(url, user, db_password);
}
%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Menu</title>

    <link rel="stylesheet" href="techlog_style.css">
</head>

<body class="body2">

    <div class="logo-container">
        <img src="logo.png" alt="Logo" class="logo">
    </div>

    <h1 class="welcome-text"  id="welcome-text">Welcome <%= session.getAttribute("name") + " " + session.getAttribute("surname") + "!"%></h1>

    <div class="menu-layout">

        <!-- αριστερό πλαίσιο ενεργειών κλπ -->
        <div class="side-menu">

            <button class="menu-button" onclick="goToMainMenu()">
                Main Menu
            </button>
            
            <button class="menu-button" onclick="studentRequests()">
                Student requests
            </button>

            <button class="menu-button" onclick="teacherRequests()">
                Professor requests
            </button>

            <button class="menu-button" onclick="addUser()">
                Add User
            </button>

            <button class="menu-button" onclick="deleteUser()">
                Delete User
            </button>
            
            <button class="menu-button" onclick="showStudents()">
                Student Chat
            </button>

        </div>

        <!-- βασικό πλαίσιο -->
        <div class="content-frame" id="content-frame">

            <h2 class="frame-title">Welcome <%= (String)session.getAttribute("name") + " " + session.getAttribute("surname") + "!"%> </h2>

            <p class="placeholder-text">
                As an <b>admin</b> you can approve requests from teachers and students that want to delete their accounts.<br>	
                You can also add new users into the system. You can choose the different actions from the menu on the left.
            </p>

        </div>

    </div>

    <button onclick="location.href='startpage.jsp'" class="logout-button">
        Logout
    </button>

    <script>
   
	   
        let selectedRow = null;

        function selectRow(row) {

            // deselection
            if (selectedRow === row) {
                row.classList.remove("selected");
                selectedRow = null;
                return;
            }

            // remove previous selected row
            document.querySelectorAll(".grid-table tr.selected").forEach(r => {
                r.classList.remove("selected");
            });

            // new row
            row.classList.add("selected");
            selectedRow = row;
            
            document.getElementById("selectedEmail").value = row.cells[1].innerText;
            document.getElementById("selectedType").value = row.cells[2].innerText;
            
        }
        
        function selectRowRequest(row) {

            // deselection
            if (selectedRow === row) {
                row.classList.remove("selected");
                selectedRow = null;
                return;
            }

            // remove previous selected row
            document.querySelectorAll(".grid-table tr.selected").forEach(r => {
                r.classList.remove("selected");
            });

            // new row
            row.classList.add("selected");
            selectedRow = row;
            
            document.getElementById("selectedEmail").value = row.cells[1].innerText;
            document.getElementById("requestId").value = row.cells[2].innerText;            
        }

        const frame = document.getElementById("content-frame")
        const wltext = document.getElementById("welcome-text")
        
        function goToMainMenu() {

            wltext.innerHTML = "Welcome <%= session.getAttribute("name") + " " + session.getAttribute("surname") + "!"%>"
            frame.innerHTML = `

            <h2 class="frame-title">Welcome <%= session.getAttribute("name") + " " + session.getAttribute("surname") + "!"%></h2>

            <p class="placeholder-text" id="placeholder-text">
            As an <b>admin</b> you can approve requests from teachers and students that want to delete their accounts.<br>	
            You can also add new users into the system. You can choose the different actions from the menu on the left.
            </p>

    `;
        }
        
        function deleteSelectedRow() {

            if (!selectedRow) {
                alert("Select a row first");
                return;
            }

            selectedRow.remove();
            selectedRow = null;
        }

        function addUser() {

            wltext.innerHTML = "Add a user manually to the system:"
            frame.innerHTML = `
            <form action="adminServlet" method="post" onsubmit="return checkRegisterCredentials()">
            
           	<input type="hidden" name="action" value="register">
           	
            <input type="text" name="name" id="name" placeholder="Name" required><br>

            <input type="text" name="surname" id="surname" placeholder="Surname" required><br>

            <input type="email" name="email" id="email" placeholder="Email" required><br>

            <input type="password" name="password" id="password" placeholder="Password" required><br>

            <select name="registerType" id="registerType" onchange="updateRegisterForm()">
                <option value="teacher">Teacher</option>
                <option value="student">Student</option>
                <option value="admin">Admin</option>
            </select><br>

            <input type="text" name="subject" id="option" placeholder="Enter Subject" required><br>

            <div class="button-group">
                <button type="submit">OK</button>
                <button onclick="goToMainMenu()">Cancel</button>
            </div>
            </form>

    `;
        }

        function updateRegisterForm() {

        	var val = document.getElementById("registerType").value;
            var input = document.getElementById("option");
            if (val == "student") {
            	input.required = true;
                input.style.display = "block";
                input.placeholder = "Enter AM";
            }
            else if (val == "professor") {
            	input.required = true;
                input.style.display = "block";
                input.placeholder = "Enter subject";
            } 
            else {
                input.style.display = "none";
                input.required=false;
            }
        }

        function checkRegisterCredentials() {

        	var t1 = document.getElementById("email").value;
            var t2 = document.getElementById("password").value;
            var t3 = document.getElementById("name").value;
            var t4 = document.getElementById("surname").value;
            var t5 = document.getElementById("option").value;
            var userType = document.getElementById("registerType").value;
            
            if ((t1.trim() == "") || (t2.trim() == "") || (t3.trim() == "")  || (t4.trim() == "") || ((t5.trim() == "") && (userType != "admin"))) {
                alert("Fill out credentials");
                return false;
            }
            else {
                return true;
            }

        }
			function showStudents() {
				
				<% if (request.getAttribute("contentHtml") != null && !((String)request.getAttribute("contentHtml")).isEmpty()) { %>
	            wltext.innerHTML = ""
				frame.innerHTML = `
			   		<h2 class='content-title'>Student Chats</h2>
			   		
			   		
			   			
			   			<% String students =   "SELECT name, surname FROM student WHERE email=?" ;
			    		try (Connection connection = DriverManager.getConnection(url, user, db_password);
			    	        	PreparedStatement statement = connection.prepareStatement(students)) {
			    				statement.setString(1,(String)request.getAttribute("contentHtml"));
			    	        	ResultSet rs = statement.executeQuery();
			    	        	if(rs.next()) { %>
			    	        	<p class='placeholder-text'> <%= rs.getString("name") + " " + rs.getString("surname") %> </p>
			    	        	<%
			    	        	}
			    		        } catch (SQLException e) {
			    		            System.out.println("Connection failed!");
			    		            e.printStackTrace();
			    		        } %>
			   			
			   			
			   			
			   		
			   		<div class='chat-box'>
			   		
			   		<% String chatSql =
			   			"SELECT sender_name, sender_role, content, sent_at " +
			   			"FROM chat_messages " +
			   			"WHERE student_email = ? AND admin_email = ? " +
			   			"ORDER BY sent_at ASC, message_id ASC";
			   		
			   		try (Connection con = getConnection();
			   		PreparedStatement ps = con.prepareStatement(chatSql)) {
			   		
			   			ps.setString(1, escapeHtml((String)request.getAttribute("contentHtml")));
			   			ps.setString(2, (String) session.getAttribute("email"));
			   			
			   			try (ResultSet rs = ps.executeQuery()) {
			   				boolean found = false;
			   				
			   				while (rs.next()) {
			   				  found = true; %>
			   				
			   				  <div class='chat-message'>
			   				      <b> <%= escapeHtml(rs.getString("sender_name")) %>:</b> 
			   				      <%=escapeHtml(rs.getString("content"))%>
			   				      </div>
			   				<%}
			   				
			   				if (!found) { %>
			   				  <div class='chat-message'>No messages yet.</div>
			   					<% }
			   			}
			   		}
			   	%>
			   		
			   		</div>
			   			<form action='adminServlet' method='post'>
			   			<input type='hidden' name='studentEmail' value='<%=((String)request.getAttribute("contentHtml"))%>'>
			   			<input type='hidden' name='adminEmail' value='<%=escapeHtml((String)session.getAttribute("email"))%>'>
			   			<input type='hidden' name='adminName' value='<%=escapeHtml((String)session.getAttribute("name"))%>'>
			   			<textarea name='messageText' placeholder='Write message'></textarea>
			   			<div class='action-buttons'>
			   				<button type='submit' name='action' value='sendStudentMessage'>Send</button>
			   				<button type ='submit' name='action' value='backToStudentSelection'>Back</button>
			   			</div>
			   			</form>
			   		
			   		`;
				<%}else{ %>
	            wltext.innerHTML = "Select a student to send a message:"
				frame.innerHTML = `
					
		            <h2 class="content-title">Students</h2>
		            
		            <form id="selectForm" action="adminServlet" method="post">     
					
		            <input type="hidden" name="action" id="action" value="selectStudent">
		            <input type="hidden" name="email" id="selectedEmail">
		            <input type="hidden" name="type" id="selectedType">
		            
		            <div class="notification-section">

			            <h3>Select:</h3>
			
			            <table class="grid-table">
			
			            <tr>
			            <th>Name</th>
			            <th>Email</th>
			            <th>Type</th>
			            </tr>
			
			            <%
			            ArrayList<String[]> Students = new ArrayList<>();
			            String students =   "SELECT email, name, 'Student' AS type FROM student " ;
			    		try (Connection connection = DriverManager.getConnection(url, user, db_password);
			    	        	PreparedStatement statement = connection.prepareStatement(students)) {
			    	        	ResultSet rs = statement.executeQuery();
			    	        	while(rs.next()) {
			    	        		Students.add(new String[] {rs.getString("name"), rs.getString("email"), rs.getString("type")});
			    	        	}
			    		        } catch (SQLException e) {
			    		            System.out.println("Connection failed!");
			    		            e.printStackTrace();
			    		        }
				    
				    	if (Students != null) {
				    	    for (String[] s : Students) {
				    	%>
				    	<tr onclick="selectRow(this)">
				    	    <td><%= s[0] %></td>
				    	    <td><%= s[1] %></td>
				    	    <td><%= s[2] %></td>
				    	</tr>
				    	<%
				    	    }
				    	}
				    	%>
			            </table>
			
			            <div class="action-buttons">
			
				            <button type="submit" >Select</button>
					
			            </div>

		            </div>
		            </form>
		            `;
				<%}%>
				
			}
			
			
            function deleteUser() {

            frame.innerHTML = `

            <h2 class="content-title">Users</h2>
            
            <form id="deleteForm" action="adminServlet" method="post">     
			
            <input type="hidden" name="action" id="action" value="deleteUser">
            <input type="hidden" name="email" id="selectedEmail">
            <input type="hidden" name="type" id="selectedType">
            
            <div class="notification-section">

	            <h3>Delete:</h3>
	
	            <table class="grid-table">
	
	            <tr>
	            <th>Name</th>
	            <th>Email</th>
	            <th>Type</th>
	            </tr>
	
	            <%
	            ArrayList<String[]> Users = new ArrayList<>();
	            String users =   "SELECT email, name, 'Student' AS type FROM student " +
	            	    "UNION ALL " +
	            	    "SELECT email, name, 'Professor' AS type FROM professor " +
	            	    "UNION ALL " +
	            	    "SELECT email, name, 'Admin' AS type FROM admin";;
	    		try (Connection connection = DriverManager.getConnection(url, user, db_password);
	    	        	PreparedStatement statement = connection.prepareStatement(users)) {
	    	        	ResultSet rs = statement.executeQuery();
	    	        	while(rs.next()) {
	    	        		Users.add(new String[] {rs.getString("name"), rs.getString("email"), rs.getString("type")});
	    	        	}
	    		        } catch (SQLException e) {
	    		            System.out.println("Connection failed!");
	    		            e.printStackTrace();
	    		        }
		    
		    	if (Users != null) {
		    	    for (String[] u : Users) {
		    	%>
		    	<tr onclick="selectRow(this)">
		    	    <td><%= u[0] %></td>
		    	    <td><%= u[1] %></td>
		    	    <td><%= u[2] %></td>
		    	</tr>
		    	<%
		    	    }
		    	}
		    	%>
	            </table>
	
	            <div class="action-buttons">
	
		            <button type="submit">Delete</button>
			
	            </div>

            </div>
            </form>

    `;
        }

        function studentRequests() {

            wltext.innerHTML = "The following requests by students require processing:"
            frame.innerHTML = `

            <h2 class="content-title">Requests</h2>

            <div class="notification-section">

            <h3>List of requests:</h3>
            <form id="studentForm" action="adminServlet" method="post">
            <input type="hidden" name="selectedEmail" id="selectedEmail">
            <input type="hidden" name="requestId" id="requestId">
            <table class="grid-table">

            <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Id</th>
            </tr>

            <%
            ArrayList<String[]> StudentRequests = new ArrayList<>();
            String studentRequests =   "SELECT request_id, name, email FROM requests WHERE role='Student'";
    		try (Connection connection = DriverManager.getConnection(url, user, db_password);
    	        	PreparedStatement statement = connection.prepareStatement(studentRequests)) {
    	        	ResultSet rs = statement.executeQuery();
    	        	while(rs.next()) {
    	        		StudentRequests.add(new String[] {rs.getString("name"), rs.getString("email"), rs.getString("request_id")});
    	        	}
    		        } catch (SQLException e) {
    		            System.out.println("Connection failed!");
    		            e.printStackTrace();
    		        }
	    
	    	if (StudentRequests != null) {
	    	    for (String[] r : StudentRequests) {
	    	%>
	    	<tr onclick="selectRowRequest(this)">
	    	    <td><%= r[0] %></td>
	    	    <td><%= r[1] %></td>
	    	    <td><%= r[2] %></td>
	    	</tr>
	    	<%
	    	    }
	    	}
	    	%>

            </table>

            <div class="action-buttons">

            <button type="submit" name="action" value="deleteStudent">Accept</button>

            <button type="submit" name="action" value="rejectStudent">Reject</button>

            </div>

            </div>
			</form>
    `;
        }

        function teacherRequests() {

            wltext.innerHTML = "The following requests by professor require processing:"
            frame.innerHTML = `

            <h2 class="content-title">Requests</h2>

            <form id="professorForm" action="adminServlet" method="post">
            <input type="hidden" name="selectedEmail" id="selectedEmail">
            <input type="hidden" name="requestId" id="requestId">
            <div class="notification-section">

            <h3>List of requests:</h3>

            <table class="grid-table">

            <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Id</th>
            </tr>

            <%
            ArrayList<String[]> ProfRequests = new ArrayList<>();
            String profRequests =   "SELECT request_id, name, email FROM requests WHERE role='Professor'";
    		try (Connection connection = DriverManager.getConnection(url, user, db_password);
    	        	PreparedStatement statement = connection.prepareStatement(profRequests)) {
    	        	ResultSet rs = statement.executeQuery();
    	        	while(rs.next()) {
    	        		ProfRequests.add(new String[] {rs.getString("name"), rs.getString("email"), rs.getString("request_id")});
    	        	}
    		        } catch (SQLException e) {
    		            System.out.println("Connection failed!");
    		            e.printStackTrace();
    		        }
	    
	    	if (ProfRequests != null) {
	    	    for (String[] r : ProfRequests) {
	    	%>
	    	<tr onclick="selectRowRequest(this)">
	    	    <td><%= r[0] %></td>
	    	    <td><%= r[1] %></td>
	    	    <td><%= r[2] %></td>
	    	</tr>
	    	<%
	    	    }
	    	}
	    	%>

            </table>

            <div class="action-buttons">

            <button type="submit" name="action" value="deleteProfessor">Accept</button>

            <button type="submit" name="action" value="rejectProfessor">Reject</button>

            </div>

            </div>

    `;
        }
        
        function showChat() {
    		
   		 
       }
    
    </script>

</body>

</html>