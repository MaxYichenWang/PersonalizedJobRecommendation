package com.yichen.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yichen.job.db.MySQLConnection;
import com.yichen.job.entity.LoginRequestBody;
import com.yichen.job.entity.LoginResponseBody;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    // User log in
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        LoginRequestBody body = mapper.readValue(request.getReader(), LoginRequestBody.class);
        MySQLConnection connection = new MySQLConnection();

        LoginResponseBody loginResponseBody;
        // if both user_id and password both match
        if (connection.verifyLogin(body.userId, body.password)) {

            // check if request has a session, if not, create a new session and save current user_id
            HttpSession session = request.getSession();
            session.setAttribute("user_id", body.userId);

            loginResponseBody = new LoginResponseBody("OK", body.userId, connection.getFullname(body.userId));
        } else {
            loginResponseBody = new LoginResponseBody("Login failed, user id and passcode do not exist.", null, null);
            // Status code 401 = "Unauthorized"
            response.setStatus(401);
        }
        connection.close();

        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), loginResponseBody);
    }

    // check if current user is logged in by checking if corresponding session exists
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        // get current session, "false" means do not create a new session
        HttpSession session = request.getSession(false);

        LoginResponseBody loginResponseBody;

        // user's session exists, means user already logged in
        if (session != null) {
            MySQLConnection connection = new MySQLConnection();
            // get current userId
            String userId = session.getAttribute("user_id").toString();
            loginResponseBody = new LoginResponseBody("OK", userId, connection.getFullname(userId));
            connection.close();
        } else {
            loginResponseBody = new LoginResponseBody("Session Invalid", null, null);
            // Status 403 Code: Forbidden
            response.setStatus(403);
        }

        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), loginResponseBody);
    }
}
