<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Menu</title>

    <link rel="stylesheet" href="techlog_style.css">
</head>

<body class="body2">

    <div class="logo-container">
        <img src="logo.png" alt="Logo" class="logo">
    </div>

    <h1 class="welcome-text">Welcome <%= session.getAttribute("name") != null ? session.getAttribute("name") : "" %></h1>

    <div class="menu-layout">

        <!-- αριστερό πλαίσιο ενεργειών κλπ -->
        <div class="side-menu">

          
            <form action="studentPageServlet" method="post">
                <input type="hidden" name="action" value="teamCreation">
                <button class="menu-button" type="submit">Team Creation</button>
            </form>

            <form action="studentPageServlet" method="post">
                <input type="hidden" name="action" value="todoProjects">
                <button class="menu-button" type="submit">To-Do Projects</button>
            </form>

            <form action="studentPageServlet" method="post">
                <input type="hidden" name="action" value="submitProjectPage">
                <button class="menu-button" type="submit">Submit Project</button>
            </form>

            
            <form action="studentPageServlet" method="post">
                <input type="hidden" name="action" value="contactAdmin">
                <button class="menu-button" type="submit">Contact Admin</button>
            </form>

            <form action="studentPageServlet" method="post">
                 <input type="hidden" name="action" value="notifications">
                <button class="menu-button" type="submit">Notifications</button>
            </form>
            
            <form action="studentPageServlet" method="post">
                 <input type="hidden" name="action" value="deleteAccountPage">
                 <button class="menu-button" type="submit">
                          Delete Account
                 </button>
            </form>


            
        </div>

        <!-- βασικό πλαίσιο -->
        <div class="content-frame" id="content-frame">

            
            <%= request.getAttribute("contentHtml") != null ? request.getAttribute("contentHtml") : "<h2 class='frame-title'>You can navigate through E-projects via the menu buttons on the left.</h2><p class='placeholder-text'>Choose one of the available options</p>" %>

        </div>

    </div>

    <form action="studentPageServlet" method="post">
    <input type="hidden" name="action" value="logout">
    <button class="logout-button" type="submit">Logout</button>
</form>

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


</script>

</body>

</html>