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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luca
 *
 */
@TestMethodOrder(OrderAnnotation.class)
public class PackTest {

	private static final Logger log = LoggerFactory.getLogger(PackTest.class);
	private static List<ItemPack> itemPackList = new ArrayList<ItemPack>();

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
	public void testBakerOrder() {

		Map<Integer, String> orderRequest = new HashMap<Integer, String>() {
			{
				put(14, ItemType.MB11.getCode());
			}
		};
		Date orderDate = new Date();

		List<ItemOrder> itemOrderList = new ArrayList<>();

		orderRequest.entrySet().forEach(o -> {

			LinkedList<ItemPack> itemList = itemPackList.stream()
					.filter(i -> i.getItem().getItemCode().equals(o.getValue()))
					.sorted(Comparator.comparingInt(ItemPack::getItemQuantity).reversed())
					.collect(Collectors.toCollection(LinkedList::new));

			double finalPrice = 0;
			Map<Integer, Integer> map;

			map = packagingWithQueue(o, itemList);

			if (null == map)
				map = packagingWithStack(o, itemList);

			if (null == map)
				throw new RuntimeException();

			BakeryOrder order = new BakeryOrder();

			order.setOrderId(1L);
			order.setOrderPrice(finalPrice);
			order.setOrderDate(orderDate);
			order.setOrderStatus("RECEIVED");
			order.setPaymentType("CREDITCARD");

			map.entrySet().stream().forEach(m -> {
				ItemPack itemPack = itemList.stream().filter(p -> p.getItemQuantity() == m.getKey()).findFirst()
						.orElse(null);

				ItemOrder itemOrder = new ItemOrder();
				itemOrder.setItemPack(itemPack);
				itemOrder.setOrder(order);
				itemOrder.setItemPackOrderQuantity(m.getValue());

				itemOrderList.add(itemOrder);
			});

		});

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

		return null;
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
		return null;
	}
}
