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
import java.util.HashSet;
import java.util.Set;

import util.DBConnection;

@WebServlet("/jobs")
public class JobsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        Integer studentId = null;
        String studentName = null;

        if (session != null && session.getAttribute("studentId") != null) {
            studentId = (Integer) session.getAttribute("studentId");
            studentName = (String) session.getAttribute("studentName");
        }

        // Fetch jobs student has already applied for to display "Applied" badge
        Set<Integer> appliedJobIds = new HashSet<>();
        if (studentId != null) {
            try {
                Connection connection = DBConnection.getConnection();
                PreparedStatement appStmt = connection.prepareStatement("SELECT job_id FROM applications WHERE student_id = ?");
                appStmt.setInt(1, studentId);
                ResultSet appRs = appStmt.executeQuery();
                while (appRs.next()) {
                    appliedJobIds.add(appRs.getInt("job_id"));
                }
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String sql = "SELECT id, company_name, job_title, location, eligibility, salary, description FROM jobs ORDER BY id DESC";

        try {
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Available Jobs - Placement Portal</title>");
            out.println("<link rel='stylesheet' href='style.css'>");
            out.println("</head>");
            out.println("<body>");

            // Navigation Bar
            out.println("<nav class='navbar'>");
            out.println("<a href='index.html' class='brand'>Campus Placement Portal</a>");
            out.println("<div class='nav-links'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("<a href='jobs'>Browse Jobs</a>");
            if (studentId != null) {
                out.println("<a href='my-applications'>My Applications</a>");
                out.println("<span style='color: var(--primary); font-weight: 600;'>Hi, " + studentName + "</span>");
                out.println("<a href='logout' class='btn btn-outline' style='padding: 0.35rem 0.8rem;'>Logout</a>");
            } else {
                out.println("<a href='login.html'>Student Login</a>");
                out.println("<a href='admin-login.html'>Admin Login</a>");
            }
            out.println("</div>");
            out.println("</nav>");

            out.println("<div class='container'>");
            out.println("<div style='display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem;'>");
            out.println("<div>");
            out.println("<h1>Available Placement Drives</h1>");
            out.println("<p style='color: var(--text-muted);'>Explore open job roles from top hiring companies.</p>");
            out.println("</div>");
            if (studentId != null) {
                out.println("<a href='my-applications' class='btn btn-outline'>View My Applications</a>");
            }
            out.println("</div>");

            boolean hasJobs = false;

            out.println("<div class='table-container'>");
            out.println("<table>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>Company</th>");
            out.println("<th>Job Title</th>");
            out.println("<th>Location</th>");
            out.println("<th>Eligibility</th>");
            out.println("<th>Salary</th>");
            out.println("<th>Description</th>");
            out.println("<th>Action</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");

            while (resultSet.next()) {
                hasJobs = true;
                int jobId = resultSet.getInt("id");
                String companyName = resultSet.getString("company_name");
                String jobTitle = resultSet.getString("job_title");
                String location = resultSet.getString("location");
                String eligibility = resultSet.getString("eligibility");
                String salary = resultSet.getString("salary");
                String description = resultSet.getString("description");
                if (description == null) description = "-";

                out.println("<tr>");
                out.println("<td><strong>" + companyName + "</strong></td>");
                out.println("<td>" + jobTitle + "</td>");
                out.println("<td>" + location + "</td>");
                out.println("<td>" + eligibility + "</td>");
                out.println("<td>" + salary + "</td>");
                out.println("<td style='font-size: 0.9rem; color: var(--text-muted); max-width: 250px;'>" + description + "</td>");
                out.println("<td>");

                if (studentId != null) {
                    if (appliedJobIds.contains(jobId)) {
                        out.println("<span class='badge badge-applied'>Applied</span>");
                    } else {
                        out.println("<form action='apply' method='post' style='margin:0;'>");
                        out.println("<input type='hidden' name='job_id' value='" + jobId + "'>");
                        out.println("<button type='submit' class='btn btn-primary' style='padding: 0.4rem 0.9rem; font-size: 0.85rem;'>Apply</button>");
                        out.println("</form>");
                    }
                } else {
                    out.println("<a href='login.html' class='btn btn-outline' style='padding: 0.4rem 0.9rem; font-size: 0.85rem;'>Login to Apply</a>");
                }

                out.println("</td>");
                out.println("</tr>");
            }

            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");

            if (!hasJobs) {
                out.println("<div class='card' style='text-align: center; padding: 3rem;'>");
                out.println("<p style='color: var(--text-muted); font-size: 1.1rem;'>No job listings available right now. Please check back later!</p>");
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
            out.println("<div class='container'><div class='alert alert-danger'>Failed to load jobs: " + e.getMessage() + "</div></div>");
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
