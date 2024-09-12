package it.cambi.hexad.bakery.application;

import it.cambi.hexad.bakery.request.BakeryOrderReport;
import it.cambi.hexad.bakery.request.BakeryOrderRequest;
import it.cambi.hexad.bakery.services.OrderService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequiredArgsConstructor
public class Application {

  private static final Logger log = LoggerFactory.getLogger(Application.class);

  private final OrderService orderService;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @PostMapping(
      path = "/order",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public BakeryOrderReport order(@RequestBody BakeryOrderRequest order) {

    log.info("... new order request");

    return orderService.bakeryOrder(order);
  }
}
