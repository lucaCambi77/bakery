/**
 * 
 */
package it.cambi.hexad.bakery.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Function;
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

		Pack packTest = new Pack();
		packTest.setPackId(1L);
		packTest.setPackType("CARDBOARD");
		packTest.setWeight(200.40);

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

		List<ItemPack> orderItemPackList = new ArrayList<>();
		List<ItemOrder> itemOrderList = new ArrayList<>();

		orderRequest.entrySet().forEach(o -> {

			BakeryOrder order = new BakeryOrder();

			LinkedList<ItemPack> itemList = itemPackList.stream()
					.filter(i -> i.getItem().getItemCode().equals(o.getValue()))
					.sorted(Comparator.comparingInt(ItemPack::getItemQuantity).reversed())
					.collect(Collectors.toCollection(LinkedList::new));

			double finalPrice = 0;

			// packagingWithQueue(o, itemList);
			packagingWithStack(o, itemList);

			/*
			 * if (itemOrderQuantity > 0) throw new RuntimeException();
			 */

			order.setOrderPrice(finalPrice);
			order.setItemPackList(orderItemPackList);
			order.setOrderDate(orderDate);
			order.setOrderStatus("RECEIVED");
			order.setPaymentType("CREDITCARD");
		});

	}

	/**
	 * @param o
	 * @param itemList
	 */
	private void packagingWithQueue(Entry<Integer, String> o, LinkedList<ItemPack> itemList) {
		Queue<Integer> queue = new LinkedList<Integer>();

		int i = 0;

		Integer itemOrderQuantity = o.getKey();

		Integer currentQuantity = itemList.get(i).getItemQuantity();

		while (i < itemList.size()) {

			int rem = itemOrderQuantity % currentQuantity;

			if (rem == 0) {
				break;
			}

			// stack.add(currentQuantity);
			queue.add(currentQuantity);
			int quota = itemOrderQuantity / currentQuantity;
			itemOrderQuantity -= quota * currentQuantity;

			int tmp = i + 1;

			if (tmp < itemList.size()) {
				int nextQuantity = itemList.get(tmp).getItemQuantity();
				queue.add(nextQuantity);

				/**
				 * If next quantity is greater than remaining quantity, we skip the element
				 * 
				 * If we can't complete the order at this stage, we poll the queue in order to
				 * start from next element
				 */
				if (itemOrderQuantity < nextQuantity) {
					queue.poll();
					currentQuantity = queue.peek();
					itemOrderQuantity = o.getKey();
					i++;
					continue;
				}

				int rem1 = itemOrderQuantity % nextQuantity;

				if (rem1 == 0) {
					break;
				}

			}

			itemOrderQuantity = o.getKey();
			/**
			 * If we can't complete the order at this stage, we poll the queue in order to
			 * start from next element
			 */
			queue.poll();
			currentQuantity = queue.peek();
			i++;
		}
	}

	/**
	 * @param o
	 * @param itemList
	 */
	private void packagingWithStack(Entry<Integer, String> o, LinkedList<ItemPack> itemList) {
		Stack<Integer> stack = new Stack<Integer>();

		int i = 0;

		Integer itemOrderQuantity = o.getKey();

		while (i < itemList.size()) {

			Integer currentQuantity = itemList.get(i).getItemQuantity();
			int rem = itemOrderQuantity % currentQuantity;

			if (rem == 0) {
				break;
			}

			// stack.add(currentQuantity);
			stack.add(currentQuantity);
			int quota = itemOrderQuantity / currentQuantity;
			itemOrderQuantity -= quota * currentQuantity;

			int tmp = i + 1;

			if (tmp < itemList.size()) {
				int nextQuantity = itemList.get(tmp).getItemQuantity();
				stack.add(nextQuantity);

				/**
				 * If next quantity is greater than remaining quantity, we skip the element
				 * 
				 * If we can't complete the order at this stage, we pop the stack in order to
				 * skip to next next element
				 */
				if (itemOrderQuantity < nextQuantity) {
					stack.pop();
					i++;
					i++;
					continue;
				}

				int rem1 = itemOrderQuantity % nextQuantity;

				if (rem1 == 0) {
					break;
				}

			}
			/**
			 * If we can't complete the order at this stage, we pop the stack in order to
			 * skip to next next element
			 */
			stack.pop();
			i++;
			i++;

		}
	}
}
