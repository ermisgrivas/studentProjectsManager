package servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/studentPageServlet")
@MultipartConfig(maxFileSize = 20 * 1024 * 1024) // 20MB
public class studentpageServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/tech_log";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "12345678";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("email") == null) {
            response.sendRedirect(request.getContextPath() + "/startpage.jsp");
            return;
        }

        String studentEmail = (String) session.getAttribute("email");
        String action = request.getParameter("action");

        if (action == null) {
            action = "home";
        }

        try {                   /*σενάρια για τις διαθέσιμες λειτουργίες*/ 
            switch (action) {
                case "teamCreation":
                case "loadAvailableStudents":
                    showTeamCreation(request, response, studentEmail, null);
                    break;

                case "createTeamRequests":
                    createTeamRequests(request, response, studentEmail);
                    break;

                case "todoProjects":
                case "projectDetails":
                    showTodoProjects(request, response, studentEmail, null);
                    break;

                case "deleteAccountPage":
                    showDeleteAccountPage(request,response);
                    break;

                case "requestDeleteAccount":
                    requestDeleteAccount(request,response,studentEmail);
                    break;    

                case "submitProjectPage":
                    showSubmitProjectPage(request, response, studentEmail, null);
                    break;

                case "submitProject":
                    submitProject(request, response, studentEmail);
                    break;

                case "notifications":
                    showNotifications(request, response, studentEmail, null);
                    break;

                case "deleteAccountRequest":
                    requestDeleteAccount(request, response, studentEmail);
                    break;

                case "acceptRequest":
                    acceptRequest(request, response, studentEmail);
                    break;

                case "rejectRequest":
                    rejectRequest(request, response, studentEmail);
                    break;

                case "contactAdmin":
                    showContactAdmin(request, response, studentEmail, null);
                    break;

                case "openAdminChat":
                    showContactAdmin(request, response, studentEmail, null);
                    break;

                case "sendAdminMessage":
                    sendAdminMessage(request, response, studentEmail);
                    break;

                case "logout":
                    request.getSession().invalidate();
                    response.sendRedirect(request.getContextPath() + "/startpage.jsp");
                    break;

                default:
                    forwardWithContent(request, response,
                            "<h2 class='content-title'>Student Menu</h2><p class='placeholder-text'>Choose an action</p>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            forwardWithContent(request, response,
                    "<h2 class='content-title'>Database Error</h2><p class='placeholder-text'>" +
                            escapeHtml(e.getMessage()) + "</p>");
        }
    }

    /*
     * TEAM CREATION
     */
    private void showTeamCreation(HttpServletRequest request, HttpServletResponse response,
                                  String studentEmail, String message)
            throws SQLException, ServletException, IOException {

        Integer selectedProjectId = getIntParameter(request, "projectId");
        StringBuilder html = new StringBuilder();

        html.append("<h2 class='content-title'>Team Creation</h2>");

        if (message != null) {
            html.append("<p class='placeholder-text'>").append(escapeHtml(message)).append("</p>");
        }

        html.append("<div class='grid-container'>")
                .append("<table class='grid-table'>")
                .append("<tr><th>ID</th><th>Project Name</th><th>Deadline</th><th>Subject</th><th>Team Size</th></tr>");

        String projectsSql = "SELECT id, name, date, subject, team_size FROM project ORDER BY date";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(projectsSql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                html.append("<tr>")
                        .append("<td>").append(rs.getInt("id")).append("</td>")
                        .append("<td>").append(escapeHtml(rs.getString("name"))).append("</td>")
                        .append("<td>").append(rs.getTimestamp("date")).append("</td>")
                        .append("<td>").append(escapeHtml(rs.getString("subject"))).append("</td>")
                        .append("<td>").append(rs.getInt("team_size")).append("</td>")
                        .append("</tr>");
            }
        }

        html.append("</table></div>");

        html.append("<div class='details-panel'>")
                .append("<h3>Choose Project</h3>")
                .append("<form action='studentPageServlet' method='post'>")
                .append("<input type='hidden' name='action' value='loadAvailableStudents'>")
                .append("<select name='projectId' required>")
                .append("<option value=''>Choose Project ID</option>");

        String optionSql = "SELECT id, name, team_size FROM project ORDER BY id";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(optionSql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int projectId = rs.getInt("id");
                html.append("<option value='").append(projectId).append("'");

                if (selectedProjectId != null && selectedProjectId == projectId) {
                    html.append(" selected");
                }

                html.append(">")
                        .append(projectId)
                        .append(" - ")
                        .append(escapeHtml(rs.getString("name")))
                        .append(" (")
                        .append(rs.getInt("team_size"))
                        .append(" members)")
                        .append("</option>");
            }
        }

        html.append("</select>")
                .append("<div class='action-buttons'><button type='submit'>Show Available Students</button></div>")
                .append("</form>");

        if (selectedProjectId != null) {
            int teamSize = getProjectTeamSize(selectedProjectId);
            int studentsToChoose = teamSize - 1;

            html.append("<h3>Available Students</h3>")
                    .append("<p>You must choose exactly <b>")
                    .append(studentsToChoose)
                    .append("</b> student(s). Total team size for this project is <b>")
                    .append(teamSize)
                    .append("</b>, including you.</p>")
                    .append("<form action='studentPageServlet' method='post'>")
                    .append("<input type='hidden' name='action' value='createTeamRequests'>")
                    .append("<input type='hidden' name='projectId' value='").append(selectedProjectId).append("'>")
                    .append("<div class='member-list'>")
                    .append("<table class='grid-table'>")
                    .append("<tr><th>Select</th><th>Name</th><th>Surname</th><th>Email</th><th>AM</th></tr>");

            String availableSql =
                    "SELECT s.name, s.surname, s.email, s.am " +
                    "FROM student s " +
                    "WHERE s.email <> ? " +
                    "AND s.email NOT IN ( " +
                    "   SELECT st.email " +
                    "   FROM student_team st " +
                    "   JOIN teams t ON st.team_id = t.id " +
                    "   WHERE t.project_id = ? " +
                    ") " +
                    "AND s.email NOT IN ( " +
                    "   SELECT receiver_email " +
                    "   FROM team_requests " +
                    "   WHERE project_id = ? AND status = 'Pending' " +
                    ") " +
                    "ORDER BY s.surname, s.name";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(availableSql)) {

                ps.setString(1, studentEmail);
                ps.setInt(2, selectedProjectId);
                ps.setInt(3, selectedProjectId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String email = rs.getString("email");

                        html.append("<tr>")
                                .append("<td><input type='checkbox' name='members' value='")
                                .append(escapeHtml(email))
                                .append("'></td>")
                                .append("<td>").append(escapeHtml(rs.getString("name"))).append("</td>")
                                .append("<td>").append(escapeHtml(rs.getString("surname"))).append("</td>")
                                .append("<td>").append(escapeHtml(email)).append("</td>")
                                .append("<td>").append(escapeHtml(rs.getString("am"))).append("</td>")
                                .append("</tr>");
                    }
                }
            }

            html.append("</table></div>")
                    .append("<div class='action-buttons'><button type='submit'>Create Team Requests</button></div>")
                    .append("</form>");
        }

        html.append("</div>");

        forwardWithContent(request, response, html.toString());
    }

    private void createTeamRequests(HttpServletRequest request, HttpServletResponse response,
                                    String studentEmail)
            throws SQLException, ServletException, IOException {

        Integer projectId = getIntParameter(request, "projectId");
        String[] members = request.getParameterValues("members");

        if (projectId == null || members == null || members.length == 0) {
            showTeamCreation(request, response, studentEmail, "Choose project and at least one student.");
            return;
        }

        if (!projectExists(projectId)) {
            showTeamCreation(request, response, studentEmail, "Project does not exist.");
            return;
        }

        int teamSize = getProjectTeamSize(projectId);
        int requiredMembers = teamSize - 1;

        if (members.length != requiredMembers) {
            showTeamCreation(request, response, studentEmail,
                    "For this project, team size is " + teamSize +
                    ". You must choose " + requiredMembers + " student(s).");
            return;
        }

        if (studentAlreadyInTeamForProject(studentEmail, projectId)) {
            showTeamCreation(request, response, studentEmail, "You already belong to a team for this project.");
            return;
        }

        String insertSql =
                "INSERT INTO team_requests(sender_email, receiver_email, status, project_id) " +
                "VALUES (?, ?, 'Pending', ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(insertSql)) {

            for (String memberEmail : members) {
                if (!studentExists(memberEmail)) {
                    continue;
                }

                if (studentAlreadyInTeamForProject(memberEmail, projectId)) {
                    continue;
                }

                ps.setString(1, studentEmail);
                ps.setString(2, memberEmail);
                ps.setInt(3, projectId);
                ps.addBatch();
            }

            ps.executeBatch();
        }

        showNotifications(request, response, studentEmail, "Team requests sent.");
    }

    /*
     * TO-DO PROJECTS
     */
    private void showTodoProjects(HttpServletRequest request, HttpServletResponse response,
                                  String studentEmail, String message)
            throws SQLException, ServletException, IOException {

        Integer selectedProjectId = getIntParameter(request, "projectId");
        StringBuilder html = new StringBuilder();

        html.append("<h2 class='content-title'>To-Do Projects</h2>");

        if (message != null) {
            html.append("<p class='placeholder-text'>").append(escapeHtml(message)).append("</p>");
        }

        html.append("<table class='grid-table'>")
                .append("<tr><th>ID</th><th>Project</th><th>Deadline</th><th>Status</th><th>Details</th></tr>");

        String sql =
                "SELECT p.id, p.name, p.date, " +
                "CASE WHEN EXISTS ( " +
                "   SELECT 1 " +
                "   FROM submissions sub " +
                "   JOIN teams t2 ON sub.team_id = t2.id " +
                "   JOIN student_team st2 ON st2.team_id = t2.id " +
                "   WHERE st2.email = ? AND sub.project_id = p.id " +
                ") THEN 'Submitted' ELSE 'Pending' END AS status " +
                "FROM project p ORDER BY p.date";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int projectId = rs.getInt("id");

                    html.append("<tr>")
                            .append("<td>").append(projectId).append("</td>")
                            .append("<td>").append(escapeHtml(rs.getString("name"))).append("</td>")
                            .append("<td>").append(rs.getTimestamp("date")).append("</td>")
                            .append("<td>").append(escapeHtml(rs.getString("status"))).append("</td>")
                            .append("<td>")
                            .append("<form action='studentPageServlet' method='post'>")
                            .append("<input type='hidden' name='action' value='projectDetails'>")
                            .append("<input type='hidden' name='projectId' value='").append(projectId).append("'>")
                            .append("<button type='submit'>View Details</button>")
                            .append("</form>")
                            .append("</td>")
                            .append("</tr>");
                }
            }
        }

        html.append("</table>");

        if (selectedProjectId != null) {
            appendProjectDetailsWithTeam(html, selectedProjectId, studentEmail);
        }

        forwardWithContent(request, response, html.toString());
    }

    private void appendProjectDetailsWithTeam(StringBuilder html, int projectId, String studentEmail)
            throws SQLException {

        String detailsSql =
                "SELECT p.name, p.subject, p.date, p.max_grade, p.description, " +
                "       t.id AS team_id, t.grade " +
                "FROM project p " +
                "LEFT JOIN student_team st ON st.email = ? " +
                "LEFT JOIN teams t ON t.id = st.team_id AND t.project_id = p.id " +
                "WHERE p.id = ?";

        Integer teamId = null;
        Integer grade = null;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(detailsSql)) {

            ps.setString(1, studentEmail);
            ps.setInt(2, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int possibleTeamId = rs.getInt("team_id");

                    if (!rs.wasNull()) {
                        teamId = possibleTeamId;
                    }

                    int possibleGrade = rs.getInt("grade");

                    if (!rs.wasNull()) {
                        grade = possibleGrade;
                    }

                    html.append("<div class='details-panel'>")
                            .append("<h3>Project Details</h3>")
                            .append("<p><b>Name:</b> ").append(escapeHtml(rs.getString("name"))).append("</p>")
                            .append("<p><b>Subject:</b> ").append(escapeHtml(rs.getString("subject"))).append("</p>")
                            .append("<p><b>Deadline:</b> ").append(rs.getTimestamp("date")).append("</p>")
                            .append("<p><b>Max Grade:</b> ").append(rs.getInt("max_grade")).append("</p>")
                            .append("<p><b>Description:</b></p>")
                            .append("<p>").append(escapeHtml(rs.getString("description"))).append("</p>");

                    if (teamId != null) {
                        html.append("<h3>Team Info</h3>")
                                .append("<p><b>Team ID:</b> ").append(teamId).append("</p>")
                                .append("<p><b>Grade:</b> ")
                                .append(grade == null ? "Not graded yet" : grade)
                                .append("</p>")
                                .append("<p><b>Other members:</b></p>")
                                .append("<ul>");

                        appendOtherTeamMembers(html, teamId, studentEmail);

                        html.append("</ul>");
                    } else {
                        html.append("<h3>Team Info</h3>")
                                .append("<p>No team has been created for you for this project (yet).</p>");
                    }

                    html.append("</div>");
                }
            }
        }
    }

    private void appendOtherTeamMembers(StringBuilder html, int teamId, String studentEmail)
            throws SQLException {

        String membersSql =
                "SELECT s.name, s.surname, s.email, s.am " +
                "FROM student_team st " +
                "JOIN student s ON s.email = st.email " +
                "WHERE st.team_id = ? AND st.email <> ? " +
                "ORDER BY s.surname, s.name";

        boolean found = false;

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(membersSql)) {

            ps.setInt(1, teamId);
            ps.setString(2, studentEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    found = true;

                    html.append("<li>")
                            .append(escapeHtml(rs.getString("name")))
                            .append(" ")
                            .append(escapeHtml(rs.getString("surname")))
                            .append(" - ")
                            .append(escapeHtml(rs.getString("email")))
                            .append(" / AM: ")
                            .append(escapeHtml(rs.getString("am")))
                            .append("</li>");
                }
            }
        }

        if (!found) {
            html.append("<li>No other members.</li>");
        }
    }

    /*
     * SUBMIT PROJECT
     */
    private void showSubmitProjectPage(HttpServletRequest request, HttpServletResponse response,
                                       String studentEmail, String message)
            throws SQLException, ServletException, IOException {

        StringBuilder html = new StringBuilder();

        html.append("<h2 class='content-title'>Submit Project</h2>");

        if (message != null) {
            html.append("<p class='placeholder-text'>").append(escapeHtml(message)).append("</p>");
        }

        html.append("<table class='grid-table'>")
                .append("<tr><th>ID</th><th>Project</th><th>Deadline</th><th>Team ID</th><th>Status</th></tr>");

        String sql =
                "SELECT p.id AS project_id, p.name, p.date, t.id AS team_id, " +
                "CASE WHEN EXISTS (SELECT 1 FROM submissions sub WHERE sub.team_id = t.id AND sub.project_id = p.id) " +
                "THEN 'Submitted' ELSE 'Pending' END AS status " +
                "FROM project p " +
                "JOIN teams t ON t.project_id = p.id " +
                "JOIN student_team st ON st.team_id = t.id " +
                "WHERE st.email = ? " +
                "ORDER BY p.date";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    html.append("<tr>")
                            .append("<td>").append(rs.getInt("project_id")).append("</td>")
                            .append("<td>").append(escapeHtml(rs.getString("name"))).append("</td>")
                            .append("<td>").append(rs.getTimestamp("date")).append("</td>")
                            .append("<td>").append(rs.getInt("team_id")).append("</td>")
                            .append("<td>").append(escapeHtml(rs.getString("status"))).append("</td>")
                            .append("</tr>");
                }
            }
        }

        html.append("</table>");

        html.append("<div class='details-panel'>")
                .append("<h3>Submission Form</h3>")
                .append("<form action='studentPageServlet' method='post' enctype='multipart/form-data'>")
                .append("<input type='hidden' name='action' value='submitProject'>")
                .append("<select name='projectId' required>")
                .append("<option value=''>Choose Project ID</option>");

        String optionSql =
                "SELECT p.id, p.name " +
                "FROM project p " +
                "JOIN teams t ON t.project_id = p.id " +
                "JOIN student_team st ON st.team_id = t.id " +
                "WHERE st.email = ? " +
                "ORDER BY p.id";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(optionSql)) {

            ps.setString(1, studentEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    html.append("<option value='")
                        .append(rs.getInt("id"))
                        .append("'>")
                        .append(rs.getInt("id"))
                        .append(" - ")
                        .append(escapeHtml(rs.getString("name")))
                        .append("</option>");
                }
            }
        }

        html.append("</select>")
                .append("<input type='file' name='zipFile' accept='.zip' required>")
                .append("<textarea name='message' placeholder='Enter comments'></textarea>")
                .append("<div class='action-buttons'><button type='submit'>Submit Project</button></div>")
                .append("</form>")
                .append("</div>");

        forwardWithContent(request, response, html.toString());
    }

    private void submitProject(HttpServletRequest request, HttpServletResponse response,
                               String studentEmail)
            throws SQLException, ServletException, IOException {

        Integer projectId = getIntParameter(request, "projectId");
        String message = request.getParameter("message");
        Part filePart = request.getPart("zipFile");

        if (projectId == null || filePart == null || filePart.getSize() == 0) {
            showSubmitProjectPage(request, response, studentEmail, "Choose project and ZIP file.");
            return;
        }

        String fileName = getSubmittedFileName(filePart);

        if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
            showSubmitProjectPage(request, response, studentEmail, "Only .zip files are allowed.");
            return;
        }

        Integer teamId = findStudentTeamForProject(studentEmail, projectId);

        if (teamId == null) {
            showSubmitProjectPage(request, response, studentEmail, "You do not belong to a team for this project.");
            return;
        }

        String sql =
                "INSERT INTO submissions(team_id, project_id, file_name, file_data, message, submit_date) " +
                "VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             InputStream input = filePart.getInputStream()) {

            ps.setInt(1, teamId);
            ps.setInt(2, projectId);
            ps.setString(3, fileName);
            ps.setBlob(4, input);
            ps.setString(5, message == null ? "" : message);
            ps.executeUpdate();
        }

        showSubmitProjectPage(request, response, studentEmail, "Project submitted successfully.");
    }


    /*
     * DELETE ACCOUNT REQUEST
     */
    private void requestDeleteAccount(HttpServletRequest request,
            HttpServletResponse response,
            String studentEmail)
              throws SQLException, ServletException, IOException {

              HttpSession session = request.getSession(false);

              String name = session.getAttribute("name") == null
               ? ""
               : session.getAttribute("name").toString();

               String sql =
                 "INSERT INTO requests(name, email, role) " +
                 "VALUES (?, ?, ?)";

                  try (Connection con = getConnection();
                  PreparedStatement ps = con.prepareStatement(sql)) {

                     ps.setString(1, name);
                     ps.setString(2, studentEmail);
                     ps.setString(3, "student");

                     ps.executeUpdate();
                      }

                   forwardWithContent(request, response,
                        "<h2 class='content-title'>Delete My Account</h2>" +
                        "<p class='placeholder-text'>Your deletion request has been sent to the admins.</p>");
                   }

            private void showDeleteAccountPage(HttpServletRequest request,
            HttpServletResponse response)
              throws ServletException, IOException {

               String html =
                    "<h2 class='content-title'>Delete My Account</h2>" +
                    "<div class='details-panel'>" +
                    "<p>Are you sure you want to request deletion of your account?</p>" +
                    "<p>Your account will remain active until an administrator accepts the request.</p>" +

                    "<form action='studentPageServlet' method='post'>" +
                    "<input type='hidden' name='action' value='requestDeleteAccount'>" +

                    "<div class='action-buttons'>" +
                    "<button type='submit'>Yes, Send Delete Request</button>" +
                    "</div>" +

                    "</form>" +
                    "</div>";

                    forwardWithContent(request, response, html);
                   }


    /*
     * NOTIFICATIONS
     */
    private void showNotifications(HttpServletRequest request, HttpServletResponse response,
                                   String studentEmail, String message)
            throws SQLException, ServletException, IOException {

        StringBuilder html = new StringBuilder();

        html.append("<h2 class='content-title'>Notifications</h2>");

        if (message != null) {
            html.append("<p class='placeholder-text'>").append(escapeHtml(message)).append("</p>");
        }

        html.append("<div class='notification-section'>")
                .append("<h3>Incoming Requests</h3>")
                .append("<table class='grid-table'>")
                .append("<tr><th>Request ID</th><th>Student</th><th>Project</th><th>Status</th><th>Actions</th></tr>");

        String incomingSql =
                "SELECT tr.request_id, tr.sender_email, tr.status, p.name AS project_name " +
                "FROM team_requests tr " +
                "JOIN project p ON p.id = tr.project_id " +
                "WHERE tr.receiver_email = ? " +
                "ORDER BY tr.request_id DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(incomingSql)) {

            ps.setString(1, studentEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int requestId = rs.getInt("request_id");
                    String status = rs.getString("status");

                    html.append("<tr>")
                            .append("<td>").append(requestId).append("</td>")
                            .append("<td>").append(escapeHtml(rs.getString("sender_email"))).append("</td>")
                            .append("<td>").append(escapeHtml(rs.getString("project_name"))).append("</td>")
                            .append("<td>").append(escapeHtml(status)).append("</td>")
                            .append("<td>");

                    if ("Pending".equals(status)) {
                        html.append("<form style='display:inline' action='studentPageServlet' method='post'>")
                                .append("<input type='hidden' name='action' value='acceptRequest'>")
                                .append("<input type='hidden' name='requestId' value='").append(requestId).append("'>")
                                .append("<button type='submit'>Accept</button>")
                                .append("</form>")
                                .append("<form style='display:inline' action='studentPageServlet' method='post'>")
                                .append("<input type='hidden' name='action' value='rejectRequest'>")
                                .append("<input type='hidden' name='requestId' value='").append(requestId).append("'>")
                                .append("<button type='submit'>Reject</button>")
                                .append("</form>");
                    }

                    html.append("</td></tr>");
                }
            }
        }

        html.append("</table></div>");

        html.append("<div class='notification-section'>")
                .append("<h3>Sent Requests</h3>")
                .append("<table class='grid-table'>")
                .append("<tr><th>Request ID</th><th>Student</th><th>Project</th><th>Status</th></tr>");

        String sentSql =
                "SELECT tr.request_id, tr.receiver_email, tr.status, p.name AS project_name " +
                "FROM team_requests tr " +
                "JOIN project p ON p.id = tr.project_id " +
                "WHERE tr.sender_email = ? " +
                "ORDER BY tr.request_id DESC";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sentSql)) {

            ps.setString(1, studentEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    html.append("<tr>")
                            .append("<td>").append(rs.getInt("request_id")).append("</td>")
                            .append("<td>").append(escapeHtml(rs.getString("receiver_email"))).append("</td>")
                            .append("<td>").append(escapeHtml(rs.getString("project_name"))).append("</td>")
                            .append("<td>").append(escapeHtml(rs.getString("status"))).append("</td>")
                            .append("</tr>");
                }
            }
        }

        html.append("</table></div>");

        forwardWithContent(request, response, html.toString());
    }

    private void acceptRequest(HttpServletRequest request, HttpServletResponse response,
                               String studentEmail)
            throws SQLException, ServletException, IOException {

        Integer requestId = getIntParameter(request, "requestId");

        if (requestId == null) {
            showNotifications(request, response, studentEmail, "Invalid request.");
            return;
        }

        String senderEmail;
        int projectId;

        String selectSql =
                "SELECT sender_email, project_id " +
                "FROM team_requests " +
                "WHERE request_id = ? AND receiver_email = ? AND status = 'Pending'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(selectSql)) {

            ps.setInt(1, requestId);
            ps.setString(2, studentEmail);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    showNotifications(request, response, studentEmail, "Request not found.");
                    return;
                }

                senderEmail = rs.getString("sender_email");
                projectId = rs.getInt("project_id");
            }
        }

        String updateSql =
                "UPDATE team_requests SET status = 'Accepted' " +
                "WHERE request_id = ? AND receiver_email = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(updateSql)) {

            ps.setInt(1, requestId);
            ps.setString(2, studentEmail);
            ps.executeUpdate();
        }

        if (allRequestsAnsweredAndAccepted(senderEmail, projectId)) {
            createTeamFromAcceptedRequests(senderEmail, projectId);
        }

        showNotifications(request, response, studentEmail, "Request accepted.");
    }

    private void rejectRequest(HttpServletRequest request, HttpServletResponse response,
                               String studentEmail)
            throws SQLException, ServletException, IOException {

        Integer requestId = getIntParameter(request, "requestId");

        if (requestId == null) {
            showNotifications(request, response, studentEmail, "Invalid request.");
            return;
        }

        String sql =
                "UPDATE team_requests SET status = 'Rejected' " +
                "WHERE request_id = ? AND receiver_email = ? AND status = 'Pending'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.setString(2, studentEmail);
            ps.executeUpdate();
        }

        showNotifications(request, response, studentEmail, "Request rejected.");
    }

    /*
     * CONTACT ADMIN CHAT
     */
    private void showContactAdmin(HttpServletRequest request, HttpServletResponse response,
                                  String studentEmail, String message)
            throws SQLException, ServletException, IOException {

        String selectedAdminEmail = request.getParameter("adminEmail");
        StringBuilder html = new StringBuilder();

        html.append("<h2 class='content-title'>Contact Admin</h2>");

        if (message != null) {
            html.append("<p class='placeholder-text'>").append(escapeHtml(message)).append("</p>");
        }

        html.append("<h3>Choose Admin</h3>")
                .append("<form action='studentPageServlet' method='post'>")
                .append("<input type='hidden' name='action' value='openAdminChat'>")
                .append("<select name='adminEmail' required>")
                .append("<option value=''>Choose Admin</option>");

        String adminsSql = "SELECT email, name, surname FROM admin ORDER BY surname, name";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(adminsSql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String adminEmail = rs.getString("email");

                html.append("<option value='")
                        .append(escapeHtml(adminEmail))
                        .append("'");

                if (adminEmail.equals(selectedAdminEmail)) {
                    html.append(" selected");
                }

                html.append(">")
                        .append(escapeHtml(rs.getString("name")))
                        .append(" ")
                        .append(escapeHtml(rs.getString("surname")))
                        .append(" - ")
                        .append(escapeHtml(adminEmail))
                        .append("</option>");
            }
        }

        html.append("</select>")
                .append("<div class='action-buttons'><button type='submit'>Open Chat</button></div>")
                .append("</form>");

        if (selectedAdminEmail != null && !selectedAdminEmail.trim().isEmpty()) {
            html.append("<div class='chat-box'>");

            String chatSql =
                    "SELECT sender_name, sender_role, content, sent_at " +
                    "FROM chat_messages " +
                    "WHERE student_email = ? AND admin_email = ? " +
                    "ORDER BY sent_at ASC, message_id ASC";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(chatSql)) {

                ps.setString(1, studentEmail);
                ps.setString(2, selectedAdminEmail);

                try (ResultSet rs = ps.executeQuery()) {
                    boolean found = false;

                    while (rs.next()) {
                        found = true;

                        html.append("<div class='chat-message'>")
                                .append("<b>")
                                .append(escapeHtml(rs.getString("sender_name")))
                                .append(":</b> ")
                                .append(escapeHtml(rs.getString("content")))
                                .append("</div>");
                    }

                    if (!found) {
                        html.append("<div class='chat-message'>No messages yet.</div>");
                    }
                }
            }

            html.append("</div>")
                    .append("<form action='studentPageServlet' method='post'>")
                    .append("<input type='hidden' name='action' value='sendAdminMessage'>")
                    .append("<input type='hidden' name='adminEmail' value='")
                    .append(escapeHtml(selectedAdminEmail))
                    .append("'>")
                    .append("<textarea class='chat-input' name='messageText' placeholder='Write message' required></textarea>")
                    .append("<div class='action-buttons'><button type='submit'>Send</button></div>")
                    .append("</form>");
        }

        forwardWithContent(request, response, html.toString());
    }

    private void sendAdminMessage(HttpServletRequest request, HttpServletResponse response,
                                  String studentEmail)
            throws SQLException, ServletException, IOException {

        HttpSession session = request.getSession(false);
        String studentName = session.getAttribute("name") == null
                ? studentEmail
                : session.getAttribute("name").toString();

        String adminEmail = request.getParameter("adminEmail");
        String messageText = request.getParameter("messageText");

        if (adminEmail == null || adminEmail.trim().isEmpty()
                || messageText == null || messageText.trim().isEmpty()) {
            showContactAdmin(request, response, studentEmail, "Choose admin and write a message.");
            return;
        }

        String sql =
                "INSERT INTO chat_messages(student_email, admin_email, sender_email, sender_name, sender_role, content, sent_at) " +
                "VALUES (?, ?, ?, ?, 'student', ?, NOW())";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, studentEmail);
            ps.setString(2, adminEmail);
            ps.setString(3, studentEmail);
            ps.setString(4, studentName);
            ps.setString(5, messageText);
            ps.executeUpdate();
        }

        request.setAttribute("adminEmail", adminEmail);
        showContactAdmin(request, response, studentEmail, "Message sent.");
    }

    /*
     * TEAM CREATION - ΒΟΗΘΗΤΙΚΕΣ ΣΥΝΑΡΤΗΣΕΙΣ
     */
    private boolean allRequestsAnsweredAndAccepted(String senderEmail, int projectId)
            throws SQLException {

        String sql =
                "SELECT COUNT(*) AS bad_count " +
                "FROM team_requests " +
                "WHERE sender_email = ? AND project_id = ? AND status <> 'Accepted'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, senderEmail);
            ps.setInt(2, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("bad_count") == 0;
            }
        }
    }

    private void createTeamFromAcceptedRequests(String senderEmail, int projectId)
            throws SQLException {

        if (studentAlreadyInTeamForProject(senderEmail, projectId)) {
            return;
        }

        List<String> members = new ArrayList<>();
        members.add(senderEmail);

        String membersSql =
                "SELECT receiver_email " +
                "FROM team_requests " +
                "WHERE sender_email = ? AND project_id = ? AND status = 'Accepted'";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(membersSql)) {

            ps.setString(1, senderEmail);
            ps.setInt(2, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    members.add(rs.getString("receiver_email"));
                }
            }
        }

        int teamSize = getProjectTeamSize(projectId);

        if (members.size() != teamSize) {
            return;
        }

        String insertTeamSql =
                "INSERT INTO teams(project_id, grade) VALUES (?, NULL)";

        String insertMemberSql =
                "INSERT INTO student_team(email, team_id) VALUES (?, ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement teamPs = con.prepareStatement(insertTeamSql, Statement.RETURN_GENERATED_KEYS)) {
                teamPs.setInt(1, projectId);
                teamPs.executeUpdate();

                int teamId;

                try (ResultSet keys = teamPs.getGeneratedKeys()) {
                    if (!keys.next()) {
                        con.rollback();
                        return;
                    }

                    teamId = keys.getInt(1);
                }

                try (PreparedStatement memberPs = con.prepareStatement(insertMemberSql)) {
                    for (String email : members) {
                        memberPs.setString(1, email);
                        memberPs.setInt(2, teamId);
                        memberPs.addBatch();
                    }

                    memberPs.executeBatch();
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    private int getProjectTeamSize(int projectId) throws SQLException {
        String sql = "SELECT team_size FROM project WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("team_size");
                }
            }
        }

        return 1;
    }

    private boolean projectExists(int projectId) throws SQLException {
        String sql = "SELECT id FROM project WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean studentExists(String email) throws SQLException {
        String sql = "SELECT email FROM student WHERE email = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean studentAlreadyInTeamForProject(String email, int projectId)
            throws SQLException {

        String sql =
                "SELECT 1 " +
                "FROM student_team st " +
                "JOIN teams t ON st.team_id = t.id " +
                "WHERE st.email = ? AND t.project_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Integer findStudentTeamForProject(String email, int projectId)
            throws SQLException {

        String sql =
                "SELECT t.id " +
                "FROM teams t " +
                "JOIN student_team st ON st.team_id = t.id " +
                "WHERE st.email = ? AND t.project_id = ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, projectId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return null;
    }

    /*
     * ΠΙΟ ΓΕΝΙΚΕΣ ΒΟΗΘΗΤΙΚΕΣ ΣΥΝΑΡΤΗΣΕΙΣ
     */
    private Integer getIntParameter(HttpServletRequest request, String name) {
        try {
            String value = request.getParameter(name);

            if (value == null || value.trim().isEmpty()) {
                return null;
            }

            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getSubmittedFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");

        if (contentDisposition == null) {
            return null;
        }

        for (String token : contentDisposition.split(";")) {
            token = token.trim();

            if (token.startsWith("filename")) {
                String fileName = token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
                fileName = fileName.replace("\\", "/");
                return fileName.substring(fileName.lastIndexOf('/') + 1);
            }
        }

        return null;
    }

    private void forwardWithContent(HttpServletRequest request, HttpServletResponse response,
                                    String contentHtml)
            throws ServletException, IOException {

        request.setAttribute("contentHtml", contentHtml);

        RequestDispatcher rd = request.getRequestDispatcher("/studentpage.jsp");
        rd.forward(request, response);
    }

    private String escapeHtml(String value) {
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
}
