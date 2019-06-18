package it.cambi.hexad.bakery.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cambi.hexad.bakery.report.BakeryOrderReport;

@SpringBootApplication
@RestController
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@GetMapping("/")
	public String home() {
		return "Hello World";
	}

	@Autowired
	private ObjectMapper objectMapper;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostMapping(path = "/order", consumes = "application/json", produces = "application/json")
	public BakeryOrderReport addMember(@RequestBody BakeryOrderReport order) {

		return new BakeryOrderReport();

	}
}
