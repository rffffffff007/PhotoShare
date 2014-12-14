package com.example.photoshare.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		PrintWriter out = res.getWriter();	
		String path = getServletContext().getRealPath("/");
		out.write("project path:" + path + "\n");
	}
	
}
