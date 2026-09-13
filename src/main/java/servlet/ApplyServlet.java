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

@WebServlet("/apply")
public class ApplyServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
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
        String jobIdParam = request.getParameter("job_id");

        if (jobIdParam == null || jobIdParam.trim().isEmpty()) {
            response.sendRedirect("jobs");
            return;
        }

        int jobId = Integer.parseInt(jobIdParam);

        try {
            Connection connection = DBConnection.getConnection();

            // Check if student already applied for this job
            String checkSql = "SELECT id FROM applications WHERE student_id = ? AND job_id = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setInt(1, studentId);
            checkStmt.setInt(2, jobId);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                connection.close();
                out.println("<!DOCTYPE html>");
                out.println("<html lang='en'>");
                out.println("<head><meta charset='UTF-8'><title>Already Applied</title><link rel='stylesheet' href='style.css'></head>");
                out.println("<body>");
                out.println("<div class='container'>");
                out.println("<div class='form-card' style='text-align: center;'>");
                out.println("<div class='alert alert-warning' style='background-color: var(--warning-bg); color: var(--warning); padding: 1rem; border-radius: 8px; margin-bottom: 1rem;'>You have already applied for this job!</div>");
                out.println("<p>Check your application status under My Applications.</p><br>");
                out.println("<a href='my-applications' class='btn btn-primary btn-block'>Go to My Applications</a>");
                out.println("<br><a href='jobs' class='btn btn-outline btn-block'>Back to Jobs</a>");
                out.println("</div></div></body></html>");
                return;
            }

            // Insert new application
            String insertSql = "INSERT INTO applications (student_id, job_id, status) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = connection.prepareStatement(insertSql);
            insertStmt.setInt(1, studentId);
            insertStmt.setInt(2, jobId);
            insertStmt.setString(3, "Applied");

            insertStmt.executeUpdate();
            connection.close();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head><meta charset='UTF-8'><title>Application Submitted</title><link rel='stylesheet' href='style.css'></head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<div class='form-card' style='text-align: center;'>");
            out.println("<div class='alert alert-success'>Application Submitted Successfully!</div>");
            out.println("<p>Good luck, <strong>" + studentName + "</strong>! Your application status is now <strong>Applied</strong>.</p><br>");
            out.println("<a href='my-applications' class='btn btn-primary btn-block'>View My Applications</a>");
            out.println("<br><a href='jobs' class='btn btn-outline btn-block'>Browse More Jobs</a>");
            out.println("</div></div></body></html>");

        } catch (Exception e) {
            out.println("<!DOCTYPE html><html lang='en'><head><link rel='stylesheet' href='style.css'></head><body>");
            out.println("<div class='container'><div class='form-card' style='text-align: center;'>");
            out.println("<div class='alert alert-danger'>Application Submission Failed</div>");
            out.println("<p>" + e.getMessage() + "</p><br>");
            out.println("<a href='jobs' class='btn btn-outline btn-block'>Back to Jobs</a>");
            out.println("</div></div></body></html>");
            e.printStackTrace();
        }
    }
}
