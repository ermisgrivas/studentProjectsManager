<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Project</title>

    <link rel="stylesheet" href="techlog_style.css">
</head>

<body class="body2">

    <div class="logo-container">
        <img src="logo.png" alt="Logo" class="logo">
    </div>

    <h1 class="welcome-text" id="welcome-text">Welcome Professor</h1>

    <div class="menu-layout">

        <!-- αριστερό πλαίσιο ενεργειών κλπ -->
        <div class="side-menu">

            <button class="menu-button" onclick="goToMainMenu()">
                Main Menu
            </button>
            
            <button class="menu-button" onclick="goToProjectPage()">
                Create Project
            </button>

            <button class="menu-button" onclick="viewTeams()">
                View Student Teams
            </button>

            <button class="menu-button" onclick="gradeProjects()">
                Grade Projects
            </button>

            <button class="menu-button" onclick="contactAdmin()">
                Delete Account
            </button>

        </div>

        <!-- βασικό πλαίσιο -->
        <div class="content-frame" id="content-frame">

            <h2 class="frame-title">Professor Page</h2>

            <p class="placeholder-text" id="placeholder-text">
                Choose action:
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
        }

        const frame = document.getElementById("content-frame")
        const wltext = document.getElementById("welcome-text")

        function goToMainMenu() {

            wltext.innerHTML = "Welcome Professor"
            frame.innerHTML = `

            <h2 class="frame-title">Professor Page</h2>

            <p class="placeholder-text" id="placeholder-text">
                Choose action:
            </p>

    `;
        }
        
        function goToProjectPage() {

            wltext.innerHTML = "Fill in the form to create a project:"
            frame.innerHTML = `

            <form class="form1" action="CreateProjectServlet" method="post">

                <label for="name">Name: </label><br>
                <textarea class="instructions" id="name" name="name" required></textarea><br>
                <label for="duedate">Due date: </label><br>
                <input type="datetime-local" id="duedate" name="duedate" required><br>
                <label for="maxgrade">Max Grade: </label><br>
                <input type="number" id="maxgrade" name="maxgrade" value="1" min="1" max="10" required><br>
                <label for="maxmembers">Max Members: </label><br>
                <input type="number" id="maxmembers" name="maxmembers" value="1" min="1" required><br>
                <label for="instructions">Instructions: </label><br>
                <textarea class="instructions" id="instructions" name="instructions" required></textarea><br>
                <input type="submit" class="submit" id="submit" name="submit">
                <input type="reset" class="reset" id="reset" name="reset">

            </form>

    `;
        }

    function contactAdmin() {

            wltext.innerHTML = "Send a request to delete your account:"
            frame.innerHTML = `

                <h2 class='content-title'>Delete My Account</h2> 
                <div class='details-panel'>
                <p>Are you sure you want to request deletion of your account?</p> 
                <p>Your account will remain active until an administrator accepts the request.</p> 

                <form action='ProfessorDeleteServlet' method="post">

                <div class='action-buttons'> 
                <button type='submit'>Yes, Send Delete Request</button>
                </div>

                </form> 
                </div>

        `;
    }   

        function viewTeams() {
            
            wltext.innerHTML = "Currently assigned teams:"
            
            fetch("ViewTeamsServlet", {method: "POST"})
              .then(response => response.text())
              .then(rows => {
                  frame.innerHTML = `
                      <h2 class="content-title">Teams</h2>

                      <table class="grid-table">
                          <tr>
                              <th>ID</th>
                              <th>Members</th>
                          </tr>
                          `
                          + rows + 
                          `
                      </table> 
                  `;
              });
            
        }

    function gradeProjects() {
            
            wltext.innerHTML = "Currently completed projects:"
            
            	fetch("ViewProjectsServlet", {method: "POST"})
                .then(response => response.text())
                .then(rows => {
                    frame.innerHTML = `
                        <h2 class="content-title">Teams</h2>

                        <table class="grid-table">
                            <tr>
                                <th>Team ID</th>
                                <th>Project File</th>
                                <th>Submission Date
                            </tr>
                            `
                            + rows + 
                            `
                        </table><br><br>
                        
                        <form class="form1" method="post" action="GradeProjectsServlet">
                            
                            <label for="idForGrade">Select project by ID: </label><br>
                            <input type="number" id="idForGrade" name="idForGrade" value="1" min="1" required><br>
                            <label for="gradeAssigned">Assign grade: </label><br>
                            <input type="number" id="gradeAssigned" name="gradeAssigned" value="1" min="1" required><br>
                            <input type="submit" class="submit" id="submit" name="submit">
                            <input type="reset" class="reset" id="reset" name="reset">
                            
                        </form>    
                    `;
                });

        }    
    </script>

  

</body>

</html>