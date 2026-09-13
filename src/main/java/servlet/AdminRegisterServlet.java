package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import util.DBConnection;

@WebServlet("/admin-register")
public class AdminRegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql = "INSERT INTO admins (name, email, password) VALUES (?, ?, ?)";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, password);

            statement.executeUpdate();
            connection.close();

            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head><meta charset='UTF-8'><title>Admin Registered</title><link rel='stylesheet' href='style.css'></head>");
            out.println("<body>");
            out.println("<div class='container'>");
            out.println("<div class='form-card' style='text-align: center;'>");
            out.println("<div class='alert alert-success'>Admin Account Created Successfully!</div>");
            out.println("<p>Administrator <strong>" + name + "</strong> is now registered.</p><br>");
            out.println("<a href='admin-login.html' class='btn btn-secondary btn-block'>Proceed to Admin Login</a>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body></html>");

        } catch (Exception e) {
            out.println("<!DOCTYPE html><html lang='en'><head><link rel='stylesheet' href='style.css'></head><body>");
            out.println("<div class='container'><div class='form-card' style='text-align: center;'>");
            out.println("<div class='alert alert-danger'>Admin Registration Failed</div>");
            out.println("<p>" + e.getMessage() + "</p><br>");
            out.println("<a href='admin-register.html' class='btn btn-outline btn-block'>Try Again</a>");
            out.println("</div></div></body></html>");
            e.printStackTrace();
        }
    }
}
