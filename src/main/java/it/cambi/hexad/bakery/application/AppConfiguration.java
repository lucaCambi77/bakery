/**
 * 
 */
package it.cambi.hexad.bakery.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cambi.hexad.bakery.enums.ItemType;
import it.cambi.hexad.bakery.model.Item;
import it.cambi.hexad.bakery.model.ItemPack;
import it.cambi.hexad.bakery.model.Pack;
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

	@Bean
	public CollectionBeans getCollectionsBean() {
		return new CollectionBeans();
	}

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public List<ItemPack> getItemPackList() {

		List<ItemPack> itemPackList = new ArrayList<ItemPack>();

		/**
		 * We assume for purpose of test the same packaging for every item
		 */
		Pack packTest = new Pack();
		packTest.setPackId(1L);
		packTest.setPackType("CARDBOARD");
		packTest.setWeight(11.02);

		Date dateStart = new Date();

		for (ItemType item : ItemType.values()) {

			Item aItem = new Item();
			aItem.setDescription(item.getDescr());
			aItem.setItemCode(item.getCode());

			item.getPackToPrice().entrySet().stream().forEach(i -> {

				ItemPack itemPack = new ItemPack();
				itemPack.setDateStart(dateStart);
				itemPack.setItemPackPrice(i.getValue());
				itemPack.setItemQuantity(i.getKey());
				itemPack.setItem(aItem);
				itemPack.setPack(packTest);

				itemPackList.add(itemPack);
			});

		}

		return itemPackList;
	}
}
