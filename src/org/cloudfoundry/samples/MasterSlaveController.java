package org.cloudfoundry.samples;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cloudfoundry.samples.exception.NoServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for the application home page.
 */
@Controller
public class MasterSlaveController {

	private static final Logger logger = LoggerFactory
			.getLogger(MasterSlaveController.class);

	private ReferenceDataRepository referenceRepository;

	/**
	 * Prepares the Model with some metadata and the list of States retrieved
	 * from the DB. Then, selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/switch", method = RequestMethod.POST)
	public String home(Model model, @RequestParam String type) {
		logger.info("Welcome " + type + " Database!");
		referenceRepository = new ReferenceDataRepository();
		referenceRepository.init(type);
		checkDB(type);
		model.addAttribute("dbinfo", referenceRepository.getDbInfo());
		model.addAttribute("states", referenceRepository.findAll());
		return "result";
	}

	@RequestMapping(value = "/getSession")
	public void getSession(HttpServletResponse response, HttpSession session) {
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();

			out.println(System.getenv("VCAP_APPLICATION"));
			out.println(session.getAttribute("user") + session.getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping("/env")
	public void env(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("System Environment:");
		out.println("System Environment:");
		for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
			out.println(envvar.getKey() + ": " + envvar.getValue());
		}
	}

	@RequestMapping(value = "/setSession")
	public void setSession(HttpServletResponse response, HttpSession session) {
		session.setAttribute("user", "nate");
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.println(System.getenv("VCAP_APPLICATION"));
			out.println("success" + session.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				// ignored
			}
		}
	}

	private static String exceCmd(String cmd) {
		StringBuffer sb = new StringBuffer();
		Process proc = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			proc = pb.start();
			isr = new InputStreamReader(proc.getInputStream());

			br = new BufferedReader(isr);

			String line = null;

			while ((line = br.readLine()) != null)
				sb.append(line).append("\r\n");
			proc.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (proc != null) {
				close(br);
				close(isr);
				close(proc.getErrorStream());
				close(proc.getOutputStream());
				proc.destroy();
			}
		}
		return sb.toString();
	}

	@RequestMapping(value = "/serviceHandle", method = RequestMethod.POST)
	public void serviceHandle(Model model, @RequestParam String type) {

	}

	public void checkDB(String type) {
		if (referenceRepository.getJdbcTemplate() == null) {
			throw new NoServiceException(type);
		}
	}

	@ExceptionHandler(NoServiceException.class)
	public String handleNoServiceException(NoServiceException ex,
			HttpServletRequest request) {
		request.setAttribute("error", ex.getMessage());
		return "exception";
	}
}
