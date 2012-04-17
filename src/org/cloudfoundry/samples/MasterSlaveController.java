package org.cloudfoundry.samples;

import javax.servlet.http.HttpServletRequest;

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
