package com.yichen.job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yichen.job.entity.ExampleBook;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ExampleBookServlet", urlPatterns = "/example_book")
public class ExampleBookServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // read request from client, convert request to a JSON Object
        JSONObject jsonRequest = new JSONObject(IOUtils.toString(request.getReader()));

        // read information from request
        String title = jsonRequest.getString("title");
        String author = jsonRequest.getString("author");
        String date = jsonRequest.getString("date");
        float price = jsonRequest.getFloat("price");
        String currency = jsonRequest.getString("currency");
        int pages = jsonRequest.getInt("pages");
        String series = jsonRequest.getString("series");
        String language = jsonRequest.getString("language");
        String isbn = jsonRequest.getString("isbn");

        //
        System.out.println("Title is: " + title);
        System.out.println("Author is: " + author);
        System.out.println("Date is: " + date);
        System.out.println("Price is: " + price);
        System.out.println("Currency is: " + currency);
        System.out.println("Pages is: " + pages);
        System.out.println("Series is: " + series);
        System.out.println("Language is: " + language);
        System.out.println("ISBN is: " + isbn);

        // create a new response JSON Object
        JSONObject jsonResponse = new JSONObject();
        // write info in response
        jsonResponse.put("status", "ok");
        response.getWriter().print(jsonResponse);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get parameters of the request from client
        String keyword = request.getParameter("keyword");
        String category = request.getParameter("category");

        // print request parameters in the console
        System.out.println("Keyword is: " + keyword);
        System.out.println("Category is: " + category);

        // implement an example response with type = JSON
        response.setContentType("application/json");  // ?? why application

        // create a json object as result
        JSONObject json = new JSONObject();
        json.put("title", "Harry Potter and the Sorcerer's Stone");
        json.put("author", "JK Rowling");
        json.put("date", "October 1, 1998");
        json.put("price", 11.99);
        json.put("currency", "USD");
        json.put("pages", 309);
        json.put("series", "Harry Potter");
        json.put("language", "en_US");
        json.put("isbn", "0590353403");
//        response.getWriter().print(json);

        ExampleBook book = new ExampleBook("Harry Potter and the Sorcerer's Stone",
                "JK Rowling", 309, "October 1, 1998", "USD",
                11.99,"en_US", "Harry Potter", "0590353403");
        // use Jackson to process response
        ObjectMapper mapper = new ObjectMapper();

        response.getWriter().write(mapper.writeValueAsString(book));
    }
}
