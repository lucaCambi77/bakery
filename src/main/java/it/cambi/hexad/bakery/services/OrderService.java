/** */
package it.cambi.hexad.bakery.services;

import it.cambi.hexad.bakery.enums.ItemType;
import it.cambi.hexad.bakery.exception.BakeryException;
import it.cambi.hexad.bakery.model.Item;
import it.cambi.hexad.bakery.model.ItemOrder;
import it.cambi.hexad.bakery.model.ItemPack;
import it.cambi.hexad.bakery.request.BakeryOrderReport;
import it.cambi.hexad.bakery.request.BakeryOrderRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

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

    int reminder = 0;
    int quantityLeft = 0;

    for (Entry<String, Integer> itemToQuantityOrder : orderRequest.getItemToCountMap().entrySet()) {
      List<ItemPack> orderItemList =
          itemPackList.stream()
              .filter(i -> i.getItem().getItemCode().equals(itemToQuantityOrder.getKey()))
              .sorted(Comparator.comparingInt(ItemPack::getItemQuantity).reversed())
              .toList();

      for (ItemPack itemPack : orderItemList) {

        int packCount =
            itemToQuantityOrder.getValue()
                / (reminder == 0 ? reminder = itemPack.getItemQuantity() : reminder);
        if (packCount > 0) {
          ItemOrder itemOrder = new ItemOrder();
          itemOrder.setItemPack(itemPack);
          itemOrder.setItemPackOrderQuantity(packCount);
          itemOrder.setPartialOrderPrice(itemPack.getItemPackPrice());
          itemOrderList.add(itemOrder);
        }
        reminder = reminder % itemPack.getItemQuantity();
        quantityLeft = itemToQuantityOrder.getValue() - (quantityLeft + itemPack.getItemQuantity());
        if (reminder == 0) {
          break;
        }
      }
    }

    if (quantityLeft > 0) {
      throw new BakeryException("Quantity exceeded!");
    }

    BakeryOrderReport report = new BakeryOrderReport();
    report.setItemOrderList(itemOrderList);

    return report;
  }

  public List<ItemPack> getItemPackList() {

    List<ItemPack> itemPackList = new ArrayList<>();

    for (ItemType item : ItemType.values()) {

      Item aItem = new Item();
      aItem.setDescription(item.getDescription());
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
