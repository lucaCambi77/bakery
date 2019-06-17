/**
 * 
 */
package it.cambi.hexad.bakery.test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.annotation.Order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.cambi.hexad.bakery.application.Application;
import it.cambi.hexad.bakery.enums.ItemType;
import it.cambi.hexad.bakery.model.BakeryOrder;
import it.cambi.hexad.bakery.model.Item;
import it.cambi.hexad.bakery.model.ItemOrder;
import it.cambi.hexad.bakery.model.ItemPack;
import it.cambi.hexad.bakery.model.Pack;
import it.cambi.hexad.bakery.report.BakeryOrderReport;

/**
 * @author luca
 *
 */
@SpringBootTest(classes = { Application.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PackTest {

	private static final Logger log = LoggerFactory.getLogger(PackTest.class);
	private static List<ItemPack> itemPackList = new ArrayList<ItemPack>();
	private double finalPrice = 0;
	private LinkedList<ItemPack> orderItemList;

	@SuppressWarnings("serial")
	private static Map<String, Map<Integer, Integer>> solutionMap = new HashMap<String, Map<Integer, Integer>>() {
		{
			put(ItemType.MB11.getCode(), new HashMap<Integer, Integer>() {
				{
					put(8, 1);
					put(2, 3);

				}
			});

			put(ItemType.VS5.getCode(), new HashMap<Integer, Integer>() {
				{
					put(5, 2);

				}
			});

			put(ItemType.CF.getCode(), new HashMap<Integer, Integer>() {
				{
					put(5, 2);
					put(3, 1);

				}
			});
		}
	};

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

		Assert.assertEquals(8, itemPackList.size());
	}

	@SuppressWarnings("serial")
	@Test
	@Order(2)
	public void testBakerOrder() throws JsonProcessingException {

		NavigableMap<String, Integer> orderRequest = new TreeMap<String, Integer>() {
			{
				{
					{
						put(ItemType.MB11.getCode(), 14);
						put(ItemType.VS5.getCode(), 10);
						put(ItemType.CF.getCode(), 13);

					}
				}
			}
		};

		BakeryOrderReport report = setBakeryOrder(orderRequest);

		List<ItemOrder> itemOrderList = report.getItemOrderList();

		solutionMap.entrySet().forEach(m -> {
			m.getValue().entrySet().forEach(m1 -> {
				ItemOrder itemOrder = itemOrderList.stream()
						.filter(i -> i.getItemPack().getItem().getItemCode().equals(m.getKey()))
						.filter(i -> i.getItemPack().getItemQuantity() == m1.getKey()).findFirst().orElse(null);

				Assert.assertNotNull(itemOrder);
				Assert.assertEquals(itemOrder.getItemPackOrderQuantity(), m1.getValue());
			});
		});
	}

	/**
	 * @param orderRequest
	 * @throws JsonProcessingException
	 */
	private BakeryOrderReport setBakeryOrder(Map<String, Integer> orderRequest) throws JsonProcessingException {
		Date orderDate = new Date();
		AtomicLong count = new AtomicLong();

		LinkedList<ItemOrder> itemOrderList = new LinkedList<>();

		orderRequest.entrySet().forEach(o -> {

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

			map.entrySet().stream().forEach(m -> {
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
			});

		});

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

		log.info(new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(report));
		return report;
	}

	/**
	 * @param o
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
			 * start from next element
			 */
			map.pollFirstEntry();
			currentQuantity = map.firstKey();
			itemOrderQuantity = key;

			i++;
		}

		return new HashMap<Integer, Integer>();
	}

	/**
	 * @param o
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
