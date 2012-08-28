package org.cloudfoundry.samples;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.cloudfoundry.samples.usage.CpuTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	static List<String> list = new LinkedList<String>();
	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);

	/**
	 * Prepares the Model with some metadata and the list of States retrieved
	 * from the DB. Then, selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		logger.info("Welcome home!");
		return "home";
	}

	@RequestMapping(value = "/increMemory", method = RequestMethod.POST)
	public void increMemory(HttpServletResponse response,
			@RequestParam int number) {
		String test = "a";
		for (int i = 0; i < number; i++) {
			list.add(test);
		}
		response.setContentType("text/plain");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.println("increace[" + number + "] bytes");
	}

	@RequestMapping(value = "/decreMemory", method = RequestMethod.POST)
	public void decreMemory(HttpServletResponse response,
			@RequestParam int number) {

		response.setContentType("text/plain");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (number > list.size()) {
			out.println("memory used is not more than[" + number + "] bytes");

			return;
		}
		for (int i = number; i >= 0; i--) {
			list.remove(i);
		}
		out.println("decreace[" + number + "] bytes");
	}

	@RequestMapping(value = "/increDisk", method = RequestMethod.POST)
	public void increDisk(HttpServletResponse response, @RequestParam int number) {

		response.setContentType("text/plain");
		PrintWriter out = null;
		FileWriter fw = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fw = new FileWriter("/tmp/disk", true);
			for (int i = number; i >= 0; i--) {
				fw.write("a");
			}
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		out.println("increace[" + number + "] bytes");
	}

	@RequestMapping(value = "/cpu", method = RequestMethod.POST)
	public void cpu(HttpServletResponse response, @RequestParam int number) {
		for (int i = 0; i < number; i++) {
			CpuTest t = new CpuTest();
			t.start();
		}
	}
}
