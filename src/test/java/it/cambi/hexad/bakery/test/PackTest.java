/**
 * 
 */
package it.cambi.hexad.bakery.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author luca
 *
 */
@TestMethodOrder(OrderAnnotation.class)
public class PackTest {

	private static final Logger log = LoggerFactory.getLogger(PackTest.class);
	private static List<ItemPack> itemPackList = new ArrayList<ItemPack>();
	private double finalPrice = 0;
	private LinkedList<ItemPack> orderItemList;

	@Test
	@Order(1)
	public void createItemPackList() {

		/**
		 * We assume for purpose of test the same packaging for every item
		 */
		Pack packTest = new Pack();
		packTest.setPackId(1L);
		packTest.setPackType("CARDBOARD");
		packTest.setWeight(11.02);

		Date dateStart = new Date();

		for (ItemType item : ItemType.values()) {
			log.info(item.name());

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

		assertEquals(8, itemPackList.size());
	}

	@SuppressWarnings("serial")
	@Test
	@Order(2)
	public void testBakerOrder() throws JsonProcessingException {

		Map<Integer, String> orderRequest = new HashMap<Integer, String>() {
			{
				put(14, ItemType.MB11.getCode());
			}
		};
		getBakeryOrder(orderRequest);

	}

	/**
	 * @param orderRequest
	 * @throws JsonProcessingException
	 */
	private void getBakeryOrder(Map<Integer, String> orderRequest) throws JsonProcessingException {
		Date orderDate = new Date();
		AtomicLong count = new AtomicLong();

		List<ItemOrder> itemOrderList = new ArrayList<>();

		orderRequest.entrySet().forEach(o -> {

			orderItemList = itemPackList.stream().filter(i -> i.getItem().getItemCode().equals(o.getValue()))
					.sorted(Comparator.comparingInt(ItemPack::getItemQuantity).reversed())
					.collect(Collectors.toCollection(LinkedList::new));

			Map<Integer, Integer> map;

			Map<Integer, Integer> mapQueue = packagingWithQueue(o, orderItemList);
			Map<Integer, Integer> mapStack = packagingWithStack(o, orderItemList);

			if (mapQueue.size() == 0 && mapStack.size() == 0)
				throw new RuntimeException("Order is not possible. No packaging available");

			map = mapQueue.size() < mapStack.size() ? mapQueue : mapStack;

			map.entrySet().stream().forEach(m -> {
				ItemPack itemPack = orderItemList.stream().filter(p -> p.getItemQuantity() == m.getKey()).findFirst()
						.orElse(null);

				ItemOrder itemOrder = new ItemOrder();
				itemOrder.setItemPack(itemPack);
				itemOrder.setItemPackOrderQuantity(m.getValue());

				itemOrderList.add(itemOrder);
				finalPrice += m.getValue() * itemPack.getItemPackPrice();
			});

		});

		BakeryOrder order = new BakeryOrder();

		order.setOrderId(count.incrementAndGet());
		order.setOrderPrice(finalPrice);
		order.setOrderDate(orderDate);
		order.setItemOrderList(new HashSet<ItemOrder>(itemOrderList));
		order.setOrderStatus("RECEIVED");
		order.setPaymentType("CREDITCARD");

		itemOrderList.forEach(i -> i.setOrder(order));

		log.info(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(itemOrderList));
	}

	/**
	 * @param o
	 * @param itemList
	 */
	private Map<Integer, Integer> packagingWithQueue(Entry<Integer, String> o, LinkedList<ItemPack> itemList) {

		NavigableMap<Integer, Integer> map = new TreeMap<Integer, Integer>(Collections.reverseOrder());
		int i = 0;

		Integer itemOrderQuantity = o.getKey();

		Integer currentQuantity = itemList.get(i).getItemQuantity();

		while (i < itemList.size()) {

			int rem = itemOrderQuantity % currentQuantity;
			int quota = itemOrderQuantity / currentQuantity;
			map.put(currentQuantity, quota);

			if (rem == 0)
				return map;

			itemOrderQuantity -= quota * currentQuantity;

			int tmp = i + 1;
			int nextQuantity = 0;
			if (tmp < itemList.size()) {
				nextQuantity = itemList.get(tmp).getItemQuantity();
				map.put(nextQuantity, 0);

				/**
				 * check if next quantity is greater than remaining quantity
				 * 
				 * If we can't complete the order at this stage, we poll the queue in order to
				 * start from next element
				 */
				if (itemOrderQuantity < nextQuantity) {
					map.pollFirstEntry();
					currentQuantity = map.firstKey();
					itemOrderQuantity = o.getKey();
					i++;
					continue;
				}

				quota = itemOrderQuantity / nextQuantity;
				map.put(nextQuantity, quota);

				if (itemOrderQuantity % nextQuantity == 0)
					return map;

			}
			/**
			 * If we can't complete the order at this stage, we poll the queue in order to
			 * start from next element
			 */
			map.pollFirstEntry();
			currentQuantity = map.firstKey();
			itemOrderQuantity = o.getKey();

			i++;
		}

		return new HashMap<Integer, Integer>();
	}

	/**
	 * @param o
	 * @param itemList
	 */
	private Map<Integer, Integer> packagingWithStack(Entry<Integer, String> o, LinkedList<ItemPack> itemList) {
		NavigableMap<Integer, Integer> map = new TreeMap<Integer, Integer>(Collections.reverseOrder());

		int i = 0;

		Integer itemOrderQuantity = o.getKey();

		while (i < itemList.size()) {

			Integer currentQuantity = itemList.get(i).getItemQuantity();
			int rem = itemOrderQuantity % currentQuantity;
			int quota = itemOrderQuantity / currentQuantity;
			map.put(currentQuantity, quota);

			if (rem == 0)
				return map;

			itemOrderQuantity -= quota * currentQuantity;

			int tmp = i + 1;
			int nextQuantity = 0;

			if (tmp < itemList.size()) {
				nextQuantity = itemList.get(tmp).getItemQuantity();
				quota = itemOrderQuantity / nextQuantity;
				map.put(nextQuantity, quota);

				/**
				 * Check if next quantity is greater than remaining quantity
				 * 
				 * If we can't complete the order at this stage, we pop the stack in order to
				 * skip to next next element
				 */
				if (itemOrderQuantity < nextQuantity) {
					map.pollLastEntry();
					i++;
					i++;
					continue;
				}

				if (itemOrderQuantity % nextQuantity == 0)
					return map;

			}
			/**
			 * If we can't complete the order at this stage, we pop the stack in order to
			 * skip to next next element
			 */
			map.pollLastEntry();
			i++;
			i++;

		}
		return new HashMap<Integer, Integer>();
	}
}
