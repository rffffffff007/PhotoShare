package com.example.photoshare.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PhotoService extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		doPost(req, res);
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		// Get readers and writers for operations
		PrintWriter out = res.getWriter();
		BufferedReader br = req.getReader();

		// Read request data
		String line;
		StringBuffer answer = new StringBuffer();
		while ((line = br.readLine()) != null) {
			answer.append(line);
		}
		try {
			if (answer.toString() == null || answer.toString().isEmpty()) {
				out.write("Empty");
				return;
			}
			// Write response data
			out.write(answer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
