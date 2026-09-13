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

@WebServlet("/admin-dashboard")
public class AdminDashboardServlet extends HttpServlet {

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

        int totalJobs = 0;
        int totalApplications = 0;
        int totalStudents = 0;

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement jobStmt = connection.prepareStatement("SELECT COUNT(*) FROM jobs");
            ResultSet jobRs = jobStmt.executeQuery();
            if (jobRs.next()) totalJobs = jobRs.getInt(1);

            PreparedStatement appStmt = connection.prepareStatement("SELECT COUNT(*) FROM applications");
            ResultSet appRs = appStmt.executeQuery();
            if (appRs.next()) totalApplications = appRs.getInt(1);

            PreparedStatement stuStmt = connection.prepareStatement("SELECT COUNT(*) FROM students");
            ResultSet stuRs = stuStmt.executeQuery();
            if (stuRs.next()) totalStudents = stuRs.getInt(1);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Admin Dashboard - Placement Portal</title>");
        out.println("<link rel='stylesheet' href='style.css'>");
        out.println("</head>");
        out.println("<body>");

        // Navbar
        out.println("<nav class='navbar'>");
        out.println("<a href='admin-dashboard' class='brand'>Campus Placement Portal [Admin]</a>");
        out.println("<div class='nav-links'>");
        out.println("<a href='admin-dashboard' style='color: var(--primary);'>Dashboard</a>");
        out.println("<a href='add-job.html'>Add Job</a>");
        out.println("<a href='admin-applications'>View Applications</a>");
        out.println("<a href='jobs'>Job Listings</a>");
        out.println("<span style='color: var(--secondary); font-weight: 600;'>Admin: " + adminName + "</span>");
        out.println("<a href='logout' class='btn btn-outline' style='padding: 0.35rem 0.8rem;'>Logout</a>");
        out.println("</div>");
        out.println("</nav>");

        out.println("<div class='container'>");
        out.println("<div style='margin-bottom: 2rem;'>");
        out.println("<h1>Administrator Dashboard</h1>");
        out.println("<p style='color: var(--text-muted);'>Welcome back, <strong>" + adminName + "</strong>. Manage drives and candidate applications.</p>");
        out.println("</div>");

        // Stats Grid
        out.println("<div class='grid-2' style='grid-template-columns: repeat(3, 1fr); margin-bottom: 2rem;'>");

        out.println("<div class='card' style='text-align: center;'>");
        out.println("<h3 style='color: var(--text-muted); font-size: 0.95rem; text-transform: uppercase;'>Total Jobs</h3>");
        out.println("<p style='font-size: 2.2rem; font-weight: 700; color: var(--primary); margin: 0.5rem 0 0;'>" + totalJobs + "</p>");
        out.println("</div>");

        out.println("<div class='card' style='text-align: center;'>");
        out.println("<h3 style='color: var(--text-muted); font-size: 0.95rem; text-transform: uppercase;'>Total Applications</h3>");
        out.println("<p style='font-size: 2.2rem; font-weight: 700; color: var(--warning); margin: 0.5rem 0 0;'>" + totalApplications + "</p>");
        out.println("</div>");

        out.println("<div class='card' style='text-align: center;'>");
        out.println("<h3 style='color: var(--text-muted); font-size: 0.95rem; text-transform: uppercase;'>Registered Students</h3>");
        out.println("<p style='font-size: 2.2rem; font-weight: 700; color: var(--success); margin: 0.5rem 0 0;'>" + totalStudents + "</p>");
        out.println("</div>");

        out.println("</div>");

        // Action Cards Grid
        out.println("<div class='grid-2'>");

        out.println("<div class='card'>");
        out.println("<h2>Post New Placement Drive</h2>");
        out.println("<p>Create and publish new job openings for visiting companies with eligibility and salary details.</p>");
        out.println("<a href='add-job.html' class='btn btn-primary'>+ Add Job Opening</a>");
        out.println("</div>");

        out.println("<div class='card'>");
        out.println("<h2>Manage Student Applications</h2>");
        out.println("<p>Review student submissions, shortlist eligible candidates, and update selection outcomes.</p>");
        out.println("<a href='admin-applications' class='btn btn-secondary'>Review Applications (" + totalApplications + ")</a>");
        out.println("</div>");

        out.println("</div>");

        out.println("</div>");

        out.println("<footer>");
        out.println("<p>&copy; 2026 Student Placement Management System</p>");
        out.println("</footer>");

        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {
        doGet(request, response);
    }
}
