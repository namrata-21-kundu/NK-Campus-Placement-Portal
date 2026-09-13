package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import util.DBConnection;

@WebServlet("/admin-applications")
public class AdminApplicationsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect("admin-login.html");
            return;
        }

        String adminName = (String) session.getAttribute("adminName");

        String sql = "SELECT a.id, a.status, s.name AS student_name, s.email AS student_email, " +
                     "j.company_name, j.job_title " +
                     "FROM applications a " +
                     "JOIN students s ON a.student_id = s.id " +
                     "JOIN jobs j ON a.job_id = j.id " +
                     "ORDER BY a.id DESC";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Manage Applications - Admin Portal</title>");
            out.println("<link rel='stylesheet' href='style.css'>");
            out.println("</head>");
            out.println("<body>");

            // Navbar
            out.println("<nav class='navbar'>");
            out.println("<a href='admin-dashboard' class='brand'>Campus Placement Portal [Admin]</a>");
            out.println("<div class='nav-links'>");
            out.println("<a href='admin-dashboard'>Dashboard</a>");
            out.println("<a href='add-job.html'>Add Job</a>");
            out.println("<a href='admin-applications' style='color: var(--primary);'>View Applications</a>");
            out.println("<a href='jobs'>Job Listings</a>");
            out.println("<span style='color: var(--secondary); font-weight: 600;'>Admin: " + adminName + "</span>");
            out.println("<a href='logout' class='btn btn-outline' style='padding: 0.35rem 0.8rem;'>Logout</a>");
            out.println("</div>");
            out.println("</nav>");

            out.println("<div class='container'>");
            out.println("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;'>");
            out.println("<div>");
            out.println("<h1>Student Job Applications</h1>");
            out.println("<p style='color: var(--text-muted);'>Review candidates and update application statuses in real-time.</p>");
            out.println("</div>");
            out.println("<a href='admin-dashboard' class='btn btn-outline'>&larr; Back to Dashboard</a>");
            out.println("</div>");

            boolean hasApplications = false;

            out.println("<div class='table-container'>");
            out.println("<table>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>App ID</th>");
            out.println("<th>Student Name</th>");
            out.println("<th>Student Email</th>");
            out.println("<th>Company</th>");
            out.println("<th>Job Title</th>");
            out.println("<th>Status</th>");
            out.println("<th>Change Status</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");

            while (resultSet.next()) {
                hasApplications = true;
                int appId = resultSet.getInt("id");
                String studentName = resultSet.getString("student_name");
                String studentEmail = resultSet.getString("student_email");
                String companyName = resultSet.getString("company_name");
                String jobTitle = resultSet.getString("job_title");
                String status = resultSet.getString("status");

                String badgeClass = "badge-applied";
                if ("Shortlisted".equalsIgnoreCase(status)) {
                    badgeClass = "badge-shortlisted";
                } else if ("Selected".equalsIgnoreCase(status)) {
                    badgeClass = "badge-selected";
                } else if ("Rejected".equalsIgnoreCase(status)) {
                    badgeClass = "badge-rejected";
                }

                out.println("<tr>");
                out.println("<td>#" + appId + "</td>");
                out.println("<td><strong>" + studentName + "</strong></td>");
                out.println("<td>" + studentEmail + "</td>");
                out.println("<td>" + companyName + "</td>");
                out.println("<td>" + jobTitle + "</td>");
                out.println("<td><span class='badge " + badgeClass + "'>" + status + "</span></td>");
                out.println("<td>");

                // Inline form to update status
                out.println("<form action='update-status' method='post' style='display: flex; gap: 0.5rem; align-items: center; margin: 0;'>");
                out.println("<input type='hidden' name='application_id' value='" + appId + "'>");
                out.println("<select name='status' style='padding: 0.35rem 0.5rem; font-size: 0.85rem; width: auto;'>");
                out.println("<option value='Applied'" + ("Applied".equalsIgnoreCase(status) ? " selected" : "") + ">Applied</option>");
                out.println("<option value='Shortlisted'" + ("Shortlisted".equalsIgnoreCase(status) ? " selected" : "") + ">Shortlisted</option>");
                out.println("<option value='Selected'" + ("Selected".equalsIgnoreCase(status) ? " selected" : "") + ">Selected</option>");
                out.println("<option value='Rejected'" + ("Rejected".equalsIgnoreCase(status) ? " selected" : "") + ">Rejected</option>");
                out.println("</select>");
                out.println("<button type='submit' class='btn btn-secondary' style='padding: 0.35rem 0.75rem; font-size: 0.85rem;'>Update</button>");
                out.println("</form>");

                out.println("</td>");
                out.println("</tr>");
            }

            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");

            if (!hasApplications) {
                out.println("<div class='card' style='text-align: center; padding: 3rem;'>");
                out.println("<h2>No Applications Found</h2>");
                out.println("<p style='color: var(--text-muted);'>No students have submitted applications yet.</p>");
                out.println("</div>");
            }

            out.println("</div>");

            out.println("<footer>");
            out.println("<p>&copy; 2026 Student Placement Management System</p>");
            out.println("</footer>");

            out.println("</body>");
            out.println("</html>");

            connection.close();

        } catch (Exception e) {
            out.println("<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'></head><body>");
            out.println("<div class='container'><div class='alert alert-danger'>Failed to load applications: " + e.getMessage() + "</div></div>");
            out.println("</body></html>");
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }
}
