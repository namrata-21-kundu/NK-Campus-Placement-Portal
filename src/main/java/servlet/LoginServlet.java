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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql = "SELECT id, name, email FROM students WHERE email = ? AND password = ?";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int studentId = resultSet.getInt("id");
                String studentName = resultSet.getString("name");

                // Store student details in session
                HttpSession session = request.getSession();
                session.setAttribute("studentId", studentId);
                session.setAttribute("studentName", studentName);
                session.setAttribute("role", "student");

                connection.close();
                response.sendRedirect("jobs");
                return;
            } else {
                connection.close();

                out.println("<!DOCTYPE html>");
                out.println("<html lang='en'>");
                out.println("<head><meta charset='UTF-8'><title>Login Failed</title><link rel='stylesheet' href='style.css'></head>");
                out.println("<body>");
                out.println("<div class='container'>");
                out.println("<div class='form-card' style='text-align: center;'>");
                out.println("<div class='alert alert-danger'>Invalid email or password!</div>");
                out.println("<p>Please check your credentials and try again.</p>");
                out.println("<br>");
                out.println("<a href='login.html' class='btn btn-primary btn-block'>Back to Login</a>");
                out.println("</div>");
                out.println("</div>");
                out.println("</body></html>");
            }

        } catch (Exception e) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head><meta charset='UTF-8'><title>Login Error</title><link rel='stylesheet' href='style.css'></head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<div class='form-card' style='text-align: center;'>");
            out.println("<div class='alert alert-danger'>An error occurred during login!</div>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("<br>");
            out.println("<a href='login.html' class='btn btn-outline btn-block'>Try Again</a>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body></html>");
            e.printStackTrace();
        }
    }
}
