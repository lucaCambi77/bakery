/** */
package it.cambi.hexad.bakery.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import it.cambi.hexad.bakery.services.OrderService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author luca
 */
@Configuration
public class AppConfiguration {

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();

    /** Object mapper serialization properties */
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH);
    objectMapper.setDateFormat(dateFormat);

    return objectMapper;
  }

  @Bean
  public OrderService getOrderService() {
    return new OrderService();
  }
}
