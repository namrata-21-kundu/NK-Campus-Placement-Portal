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

@WebServlet("/my-applications")
public class MyApplicationsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentId") == null) {
            response.sendRedirect("login.html");
            return;
        }

        int studentId = (Integer) session.getAttribute("studentId");
        String studentName = (String) session.getAttribute("studentName");

        String sql = "SELECT a.id, a.status, j.company_name, j.job_title, j.location, j.salary " +
                     "FROM applications a " +
                     "JOIN jobs j ON a.job_id = j.id " +
                     "WHERE a.student_id = ? " +
                     "ORDER BY a.id DESC";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, studentId);

            ResultSet resultSet = statement.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>My Applications - Placement Portal</title>");
            out.println("<link rel='stylesheet' href='style.css'>");
            out.println("</head>");
            out.println("<body>");

            // Navigation Bar
            out.println("<nav class='navbar'>");
            out.println("<a href='index.html' class='brand'>Campus Placement Portal</a>");
            out.println("<div class='nav-links'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("<a href='jobs'>Browse Jobs</a>");
            out.println("<a href='my-applications' style='color: var(--primary);'>My Applications</a>");
            out.println("<span style='color: var(--primary); font-weight: 600;'>Hi, " + studentName + "</span>");
            out.println("<a href='logout' class='btn btn-outline' style='padding: 0.35rem 0.8rem;'>Logout</a>");
            out.println("</div>");
            out.println("</nav>");

            out.println("<div class='container'>");
            out.println("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;'>");
            out.println("<div>");
            out.println("<h1>My Job Applications</h1>");
            out.println("<p style='color: var(--text-muted);'>Track the status of all placement drives you have applied for.</p>");
            out.println("</div>");
            out.println("<a href='jobs' class='btn btn-primary'>Apply to More Jobs</a>");
            out.println("</div>");

            boolean hasApplications = false;

            out.println("<div class='table-container'>");
            out.println("<table>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>App ID</th>");
            out.println("<th>Company</th>");
            out.println("<th>Job Title</th>");
            out.println("<th>Location</th>");
            out.println("<th>Salary</th>");
            out.println("<th>Application Status</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");

            while (resultSet.next()) {
                hasApplications = true;
                int appId = resultSet.getInt("id");
                String companyName = resultSet.getString("company_name");
                String jobTitle = resultSet.getString("job_title");
                String location = resultSet.getString("location");
                String salary = resultSet.getString("salary");
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
                out.println("<td><strong>" + companyName + "</strong></td>");
                out.println("<td>" + jobTitle + "</td>");
                out.println("<td>" + location + "</td>");
                out.println("<td>" + salary + "</td>");
                out.println("<td><span class='badge " + badgeClass + "'>" + status + "</span></td>");
                out.println("</tr>");
            }

            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");

            if (!hasApplications) {
                out.println("<div class='card' style='text-align: center; padding: 3rem;'>");
                out.println("<h2>No Applications Yet</h2>");
                out.println("<p style='color: var(--text-muted); margin: 0.5rem 0 1.5rem;'>You have not applied for any placement jobs yet.</p>");
                out.println("<a href='jobs' class='btn btn-primary'>Browse Available Jobs Now</a>");
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
}
