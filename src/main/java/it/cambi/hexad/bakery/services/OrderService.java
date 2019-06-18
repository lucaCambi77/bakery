/**
 * 
 */
package it.cambi.hexad.bakery.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.cambi.hexad.bakery.exception.BakeryException;
import it.cambi.hexad.bakery.model.BakeryOrder;
import it.cambi.hexad.bakery.model.ItemOrder;
import it.cambi.hexad.bakery.model.ItemPack;
import it.cambi.hexad.bakery.report.BakeryOrderReport;

/**
 * @author luca
 *
 */
public class OrderService {

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	private LinkedList<ItemPack> orderItemList;
	private AtomicLong count = new AtomicLong();

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private List<ItemPack> itemPackList;

	/**
	 * Method to process the order.
	 * 
	 * Every product is processed , all possible packaging are evaluated and finally
	 * the minimal number of pack is preferred. We create the order and also a final
	 * report with all the information about prices and item packaging
	 * 
	 * @param orderRequest
	 * @throws JsonProcessingException
	 */
	public BakeryOrderReport setBakeryOrder(Map<String, Integer> orderRequest) throws JsonProcessingException {

		if (null == orderRequest)
			throw new BakeryException("Order cannot be empty!");

		Date orderDate = new Date();

		LinkedList<ItemOrder> itemOrderList = new LinkedList<>();

		double finalPrice = 0;

		for (Entry<String, Integer> o : orderRequest.entrySet()) {
			orderItemList = itemPackList.stream().filter(i -> i.getItem().getItemCode().equals(o.getKey()))
					.sorted(Comparator.comparingInt(ItemPack::getItemQuantity).reversed())
					.collect(Collectors.toCollection(LinkedList::new));

			Map<Integer, Integer> map;

			Map<Integer, Integer> mapQueue = packagingWithQueue(o.getValue(), orderItemList);
			Map<Integer, Integer> mapStack = packagingWithStack(o.getValue(), orderItemList);

			if (mapQueue.size() == 0 && mapStack.size() == 0)
				throw new RuntimeException("Order is not possible. No packaging available");

			/**
			 * If both maps have size greater than zero, i'll get the smallest. Otherwise
			 * i'll get the one that has size greater than zero
			 */
			if (mapQueue.size() > 0 && mapStack.size() > 0) {
				map = mapQueue.size() < mapStack.size() ? mapQueue : mapStack;
			} else if (mapQueue.size() == 0 && mapStack.size() > 0) {
				map = mapStack;
			} else {
				map = mapQueue;

			}

			for (Entry<Integer, Integer> m : map.entrySet()) {
				ItemPack itemPack = orderItemList.stream().filter(p -> p.getItemQuantity() == m.getKey()).findFirst()
						.orElse(null);

				ItemOrder itemOrder = new ItemOrder();
				itemOrder.setItemPack(itemPack);
				itemOrder.setItemPackOrderQuantity(m.getValue());
				Double roundedPrice = new BigDecimal(m.getValue() * itemPack.getItemPackPrice())
						.setScale(2, RoundingMode.HALF_UP).doubleValue();
				itemOrder.setPartialOrderPrice(roundedPrice);

				itemOrderList.add(itemOrder);
				finalPrice += roundedPrice;
			}
		}

		BakeryOrder order = new BakeryOrder();

		order.setOrderId(count.incrementAndGet());
		order.setOrderPrice(new BigDecimal(finalPrice).setScale(2, RoundingMode.HALF_UP).doubleValue());
		order.setOrderDate(orderDate);
		order.setItemOrderList(new HashSet<ItemOrder>(itemOrderList));
		order.setOrderStatus("RECEIVED");
		order.setPaymentType("CREDITCARD");

		itemOrderList.forEach(i -> i.setOrder(order));

		Map<String, Integer> itemToCountMap = itemOrderList.stream()
				.collect(Collectors.groupingBy(c -> c.getItemPack().getItem().getItemCode(),
						Collectors.summingInt(c -> c.getItemPackOrderQuantity() * c.getItemPack().getItemQuantity())));

		BakeryOrderReport report = new BakeryOrderReport();
		report.setBakeryOrder(order);
		report.setItemOrderList(itemOrderList);
		report.setItemToCountMap(itemToCountMap);

		log.info(objectMapper.enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(report));
		return report;
	}

	/**
	 * @param key
	 * @param itemList
	 */
	private Map<Integer, Integer> packagingWithQueue(Integer key, LinkedList<ItemPack> itemList) {

		NavigableMap<Integer, Integer> map = new TreeMap<Integer, Integer>(Collections.reverseOrder());
		int i = 0;

		Integer itemOrderQuantity = key;

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
					itemOrderQuantity = key;
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
			 * start from next element. If no element are left, i was not able to make a package
			 */
			map.pollFirstEntry();

			if (map.size() == 0)
				return new HashMap<Integer, Integer>();

			currentQuantity = map.firstKey();
			itemOrderQuantity = key;

			i++;
		}

		return new HashMap<Integer, Integer>();
	}

	/**
	 * @param key
	 * @param itemList
	 */
	private Map<Integer, Integer> packagingWithStack(Integer key, LinkedList<ItemPack> itemList) {
		NavigableMap<Integer, Integer> map = new TreeMap<Integer, Integer>(Collections.reverseOrder());

		int i = 0;

		Integer itemOrderQuantity = key;

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
