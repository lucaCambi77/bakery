/**
 * 
 */
package it.cambi.hexad.bakery.application;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cambi.hexad.bakery.services.OrderService;

/**
 * @author luca
 *
 */
@Configuration
@ComponentScan(basePackages = { "it.cambi.hexad.bakery.services" })
public class AppConfiguration {

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public OrderService getTwitterServiceRunnable() {
		return new OrderService();
	}
}
