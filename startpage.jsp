<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%session.invalidate();%>    
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>E-projects</title>

<link rel="stylesheet" href="techlog_style.css">
</head>

<body class="body1">

    <!-- logo -->
    <div class="logo-container">
        <img src="logo.png" alt="Logo" class="logo">
    </div>

    <h1 class="welcome-text">Choose an action</h1>

    <!-- κεντρικό πλαίσιο -->
    <div class="frame" id="mainFrame">

        <div id="startMenu">
            <button onclick="showLogin()">Login</button>
            <button onclick="showRegister()">Register</button>
        </div>

    </div>

    <script>
        
        const frame = document.getElementById("mainFrame");

function showStartMenu() {

    frame.innerHTML = `
    
        <div id="startMenu">
            <button onclick="showLogin()">Login</button>
            <button onclick="showRegister()">Register</button>
        </div>

    `;
}

   //κουμπιά 

function showLogin() {

    //εντός του πλαισίου
    frame.innerHTML = `

    	<form action="startpageServlet" method="post" onsubmit="return checkLoginCredentials()">        
    	<div class="frame-title">Use Credentials</div>

        <select name="userType" id=loginType" onchange="updateLoginForm()">
            <option value="professor">Professor</option>
            <option value="student">Student</option>
            <option value="admin">Admin</option>
        </select>

        <input type="hidden" name="action" value="login">
        <input type="text" name="email" id="option" placeholder="Enter email" required>

        <input type="password" name="password" id="password" placeholder="Enter Password" required>

        <div class="button-group">
            <button type="submit">OK</button>
            <button type="button" onclick="showStartMenu()">Cancel</button>
        </div>
	</form>
    `;
}

function showRegister() {

    frame.innerHTML = `

    	<form action="startpageServlet" method="post" onsubmit="return checkRegisterCredentials()">
    
        <div class="frame-title">Register Now</div>

        
        <input type="hidden" name="action" value="register">
        
        <input type="text" name="name" id="name" placeholder="Name" required>

        <input type="text" name="surname" id="surname" placeholder="Surname" required>

        <input type="email" name="email" id="email" placeholder="Email" required>

        <input type="password" name="password" id="password" placeholder="Password" required>

        <select name="userType" id="registerType" onchange="updateRegisterForm()">
            <option value="professor">Professor</option>
            <option value="student">Student</option>
            <option value="admin">Admin</option>
        </select>

        <input type="text" name="subject" id="option" placeholder="Enter Subject" required>

        <div class="button-group">
            <button type="submit">OK</button>
            <button type="button" onclick="showStartMenu()">Cancel</button>
        </div>

        </form>
    `;
}

//όταν πατάει το οκ στο login-register ελέγχει ότι έχουν συμπληρωθεί στοιχεία για να προχωρήσει στην επόμενη σελίδα
//Προφανώς όταν προσθέσουμε βάση θα γίνονται έλεγχοι/προσθήκες σύμφωνα με τη ΒΔ
function checkLoginCredentials() {

    var t1 = document.getElementById("option").value;
    var t2 = document.getElementById("password").value;
    
    if ((t1.trim() == "") || (t2.trim() == "")) {
        alert("Fill out credentials");
        return false;
    }
    else {       
        return true;
    }

}


//Συνάρτηση που ενημερώνει το placeholder ανάλογα με τον τύπο χρήστη
//ή αφαιρεί το επιπλέον στοιχείο σε περίπτωση που ο χρήστης είναι admin
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

function updateLoginForm() {

            var val = document.getElementById("loginType").value;
            var input = document.getElementById("option");
            if (val == "student") {
                input.placeholder = "Enter AM";
            }
            else {
                input.placeholder = "Enter email";
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

    </script>

</body>

</html>