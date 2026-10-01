package com.example.lab1;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/controller")
public class ControllerServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String page = request.getParameter("page");

        if (!"1".equals(page) && !"2".equals(page)) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "The page parameter must be 1 or 2."
            );
            return;
        }

        String accept = request.getHeader("Accept");

        if ("text/plain".equalsIgnoreCase(accept)) {
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(page);
            return;
        }

        if ("1".equals(page)) {
            request.getRequestDispatcher("/page1.html")
                    .forward(request, response);

        } else {
            request.getRequestDispatcher("/page2.html")
                    .forward(request, response);
        }
    }
}
