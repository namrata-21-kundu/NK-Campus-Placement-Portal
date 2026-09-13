package servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import util.DBConnection;

@WebServlet("/update-status")
public class UpdateStatusServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("adminId") == null) {
            response.sendRedirect("admin-login.html");
            return;
        }

        String appIdParam = request.getParameter("application_id");
        String status = request.getParameter("status");

        if (appIdParam != null && status != null) {
            try {
                int applicationId = Integer.parseInt(appIdParam);

                Connection connection = DBConnection.getConnection();
                String sql = "UPDATE applications SET status = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.setString(1, status);
                statement.setInt(2, applicationId);

                statement.executeUpdate();
                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        response.sendRedirect("admin-applications");
    }
}
