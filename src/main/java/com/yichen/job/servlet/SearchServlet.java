package com.yichen.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yichen.job.db.MySQLConnection;
import com.yichen.job.entity.Item;
import com.yichen.job.entity.ResultResponse;
import com.yichen.job.external.GitHubClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();

        // session validation by checking if there exists a session matching current user
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
        }

        // parse lat and lon in the url_request from client
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));
        // get keyword from url_request
        String keyword = request.getParameter("keyword");
        // get user_id from url_request
        String userId = request.getParameter("user_id");

        // get user_id's favorite itemIds
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoriteItems = connection.getFavoriteItemIds(userId);
        connection.close();

        // search postings on GitHub using GitHubClient based on lat, lon, and keyword
        GitHubClient client = new GitHubClient();
        List<Item> items = client.search(lat, lon, null);

        // Set each item in items if it is favorite by userId (favoriteItems contains item)
        for (Item item : items) {
            item.setFavorite(favoriteItems.contains(item.getId()));
        }

        response.setContentType("application/json");

        response.getWriter().print(mapper.writeValueAsString(items));
    }
}
