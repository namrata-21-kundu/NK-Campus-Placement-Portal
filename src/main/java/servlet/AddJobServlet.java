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

import util.DBConnection;

@WebServlet("/add-job")
public class AddJobServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect("admin-login.html");
            return;
        }

        response.sendRedirect("add-job.html");
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect("admin-login.html");
            return;
        }

        String companyName = request.getParameter("company_name");
        String jobTitle = request.getParameter("job_title");
        String location = request.getParameter("location");
        String eligibility = request.getParameter("eligibility");
        String salary = request.getParameter("salary");
        String description = request.getParameter("description");

        String sql = "INSERT INTO jobs (company_name, job_title, location, eligibility, salary, description) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, companyName);
            statement.setString(2, jobTitle);
            statement.setString(3, location);
            statement.setString(4, eligibility);
            statement.setString(5, salary);
            statement.setString(6, description);

            statement.executeUpdate();
            connection.close();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head><meta charset='UTF-8'><title>Job Published</title><link rel='stylesheet' href='style.css'></head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<div class='form-card' style='text-align: center;'>");
            out.println("<div class='alert alert-success'>Job Opening Published Successfully!</div>");
            out.println("<p><strong>" + jobTitle + "</strong> at <strong>" + companyName + "</strong> is now live for students to apply.</p><br>");
            out.println("<a href='jobs' class='btn btn-primary btn-block'>View in Job Listings</a>");
            out.println("<br><a href='add-job.html' class='btn btn-outline btn-block'>+ Add Another Job</a>");
            out.println("<br><a href='admin-dashboard' style='color: var(--secondary); font-size: 0.9rem;'>Back to Dashboard</a>");
            out.println("</div></div></body></html>");

        } catch (Exception e) {
            out.println("<!DOCTYPE html><html lang='en'><head><link rel='stylesheet' href='style.css'></head><body>");
            out.println("<div class='container'><div class='form-card' style='text-align: center;'>");
            out.println("<div class='alert alert-danger'>Failed to Publish Job</div>");
            out.println("<p>" + e.getMessage() + "</p><br>");
            out.println("<a href='add-job.html' class='btn btn-outline btn-block'>Try Again</a>");
            out.println("</div></div></body></html>");
            e.printStackTrace();
        }
    }
}
