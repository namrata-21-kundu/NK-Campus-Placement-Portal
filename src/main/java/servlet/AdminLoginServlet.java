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

@WebServlet("/admin-login")
public class AdminLoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql = "SELECT id, name, email FROM admins WHERE email = ? AND password = ?";

        try {
            Connection connection = DBConnection.getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int adminId = resultSet.getInt("id");
                String adminName = resultSet.getString("name");

                // Store admin details in session
                HttpSession session = request.getSession();
                session.setAttribute("adminId", adminId);
                session.setAttribute("adminName", adminName);
                session.setAttribute("role", "admin");

                connection.close();
                response.sendRedirect("admin-dashboard");
                return;
            } else {
                connection.close();

                out.println("<!DOCTYPE html>");
                out.println("<html lang='en'>");
                out.println("<head><meta charset='UTF-8'><title>Admin Login Failed</title><link rel='stylesheet' href='style.css'></head>");
                out.println("<body>");
                out.println("<div class='container'>");
                out.println("<div class='form-card' style='text-align: center;'>");
                out.println("<div class='alert alert-danger'>Invalid Admin Credentials!</div>");
                out.println("<p>Please verify your administrator email and password.</p><br>");
                out.println("<a href='admin-login.html' class='btn btn-secondary btn-block'>Try Again</a>");
                out.println("</div>");
                out.println("</div>");
                out.println("</body></html>");
            }

        } catch (Exception e) {
            out.println("<!DOCTYPE html><html lang='en'><head><link rel='stylesheet' href='style.css'></head><body>");
            out.println("<div class='container'><div class='form-card' style='text-align: center;'>");
            out.println("<div class='alert alert-danger'>Admin Login Error</div>");
            out.println("<p>" + e.getMessage() + "</p><br>");
            out.println("<a href='admin-login.html' class='btn btn-outline btn-block'>Back</a>");
            out.println("</div></div></body></html>");
            e.printStackTrace();
        }
    }
}
