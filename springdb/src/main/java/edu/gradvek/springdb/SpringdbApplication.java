package edu.gradvek.springdb;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.gradvek.entity.Entity;

@SpringBootApplication
@RestController
public class SpringdbApplication {
	private static final Logger mLogger = Logger.getLogger(SpringApplication.class.getName());
	
	@PostMapping("/upload")
	@ResponseBody
	public ResponseEntity<Void> upload(
			@RequestBody Entity entity
	)
	{
		mLogger.info(entity.toString());
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
	
	@PostMapping("init")
	@ResponseBody
	public ResponseEntity<Void> init()
	{
		mLogger.info("Init");
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@RequestMapping("/info")
  	public String home() {
    	return "Hello Gradvec";
  	}


	public static void main(String[] args) {
		SpringApplication.run(SpringdbApplication.class, args);
	}

}
