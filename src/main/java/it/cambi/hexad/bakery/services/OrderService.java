/** */
package it.cambi.hexad.bakery.services;

import it.cambi.hexad.bakery.enums.ItemType;
import it.cambi.hexad.bakery.exception.BakeryException;
import it.cambi.hexad.bakery.model.Item;
import it.cambi.hexad.bakery.model.ItemOrder;
import it.cambi.hexad.bakery.model.ItemPack;
import it.cambi.hexad.bakery.request.BakeryOrderReport;
import it.cambi.hexad.bakery.request.BakeryOrderRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author luca
 */
public class OrderService {

  /**
   * Method to process the order.
   *
   * <p>Every product is processed , all possible packaging are evaluated and finally the minimal
   * number of packs is preferred. We create the order and also a final report with all the
   * information about prices and item packaging
   *
   * @param orderRequest
   */
  public BakeryOrderReport bakeryOrder(BakeryOrderRequest orderRequest) {

    if (null == orderRequest) {
      throw new BakeryException("Order can't be empty!");
    }

    List<ItemPack> itemPackList = getItemPackList();

    LinkedList<ItemOrder> itemOrderList = new LinkedList<>();

    for (Entry<String, Integer> o : orderRequest.getItemToCountMap().entrySet()) {
      LinkedList<ItemPack> orderItemList =
          itemPackList.stream()
              .filter(i -> i.getItem().getItemCode().equals(o.getKey()))
              .sorted(Comparator.comparingInt(ItemPack::getItemQuantity).reversed())
              .collect(Collectors.toCollection(LinkedList::new));

      Map<Integer, Integer> mapQueue = packagingWithQueue(o.getValue(), orderItemList);
      Map<Integer, Integer> mapStack = packagingWithStack(o.getValue(), orderItemList);

      if (mapQueue.isEmpty() && mapStack.isEmpty()) {
        throw new BakeryException("Can't process order. No packaging available");
      }

      // If both maps have size greater than zero, i'll get the smallest. Otherwise, i'll get the
      // one, that has size greater than zero
      Map<Integer, Integer> map;

      if (!mapQueue.isEmpty() && !mapStack.isEmpty()) {
        map = mapQueue.size() < mapStack.size() ? mapQueue : mapStack;
      } else if (mapQueue.isEmpty()) {
        map = mapStack;
      } else {
        map = mapQueue;
      }

      for (Entry<Integer, Integer> m : map.entrySet()) {
        ItemPack itemPack =
            orderItemList.stream()
                .filter(p -> p.getItemQuantity() == m.getKey())
                .findFirst()
                .orElse(null);

        ItemOrder itemOrder = new ItemOrder();
        itemOrder.setItemPack(itemPack);
        itemOrder.setItemPackOrderQuantity(m.getValue());
        double roundedPrice =
            BigDecimal.valueOf(m.getValue() * itemPack.getItemPackPrice())
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        itemOrder.setPartialOrderPrice(roundedPrice);

        itemOrderList.add(itemOrder);
      }
    }

    BakeryOrderReport report = new BakeryOrderReport();
    report.setItemOrderList(itemOrderList);

    return report;
  }

  /**
   * @param key
   * @param itemList
   */
  private Map<Integer, Integer> packagingWithQueue(Integer key, LinkedList<ItemPack> itemList) {

    NavigableMap<Integer, Integer> map = new TreeMap<>(Collections.reverseOrder());
    int i = 0;

    Integer itemOrderQuantity = key;

    Integer currentQuantity = itemList.get(i).getItemQuantity();

    while (i < itemList.size()) {

      int rem = itemOrderQuantity % currentQuantity;
      int quota = itemOrderQuantity / currentQuantity;
      map.put(currentQuantity, quota);

      if (rem == 0) {
        return map;
      }

      itemOrderQuantity -= quota * currentQuantity;

      int tmp = i + 1;
      int nextQuantity = 0;
      if (tmp < itemList.size()) {
        nextQuantity = itemList.get(tmp).getItemQuantity();
        map.put(nextQuantity, 0);

        /**
         * check if next quantity is greater than remaining quantity
         *
         * <p>If we can't complete the order at this stage, we poll the queue in order to start from
         * next element
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

        if (itemOrderQuantity % nextQuantity == 0) {
          return map;
        }
      }
      /**
       * If we can't complete the order at this stage, we poll the queue in order to start from next
       * element. If no element are left, we are not able to make a package
       */
      map.pollFirstEntry();

      if (map.isEmpty()) {
        return new HashMap<>();
      }

      currentQuantity = map.firstKey();
      itemOrderQuantity = key;

      i++;
    }

    return new HashMap<>();
  }

  /**
   * @param key
   * @param itemList
   */
  private Map<Integer, Integer> packagingWithStack(Integer key, LinkedList<ItemPack> itemList) {
    NavigableMap<Integer, Integer> map = new TreeMap<>(Collections.reverseOrder());

    int i = 0;

    Integer itemOrderQuantity = key;

    while (i < itemList.size()) {

      Integer currentQuantity = itemList.get(i).getItemQuantity();
      int rem = itemOrderQuantity % currentQuantity;
      int quota = itemOrderQuantity / currentQuantity;
      map.put(currentQuantity, quota);

      if (rem == 0) {
        return map;
      }

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
         * <p>If we can't complete the order at this stage, we pop the stack in order to skip to
         * next next element
         */
        if (itemOrderQuantity < nextQuantity) {
          map.pollLastEntry();
          i++;
          i++;
          continue;
        }

        if (itemOrderQuantity % nextQuantity == 0) {
          return map;
        }
      }
      /**
       * If we can't complete the order at this stage, we pop the stack in order to skip to next
       * next element
       */
      map.pollLastEntry();
      i++;
      i++;
    }
    return new HashMap<>();
  }

  public List<ItemPack> getItemPackList() {

    List<ItemPack> itemPackList = new ArrayList<>();

    for (ItemType item : ItemType.values()) {

      Item aItem = new Item();
      aItem.setDescription(item.getDescr());
      aItem.setItemCode(item.getCode());

      item.getPackToPrice()
          .forEach(
              (key, value) -> {
                ItemPack itemPack = new ItemPack();
                itemPack.setItemPackPrice(value);
                itemPack.setItemQuantity(key);
                itemPack.setItem(aItem);

                itemPackList.add(itemPack);
              });
    }

    return itemPackList;
  }
}
