/** */
package it.cambi.hexad.bakery.application;

import it.cambi.hexad.bakery.services.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author luca
 */
@Configuration
public class AppConfiguration {

  @Bean
  public OrderService getOrderService() {
    return new OrderService();
  }
}
