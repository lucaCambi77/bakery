/** */
package it.cambi.hexad.bakery.services;

import it.cambi.hexad.bakery.enums.ItemType;
import it.cambi.hexad.bakery.exception.BakeryException;
import it.cambi.hexad.bakery.model.ItemOrder;
import it.cambi.hexad.bakery.model.Pack;
import it.cambi.hexad.bakery.request.Order;
import it.cambi.hexad.bakery.request.OrderRequest;
import java.util.ArrayList;
import java.util.Arrays;
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
  public Order bakeryOrder(OrderRequest orderRequest) {

    if (null == orderRequest || orderRequest.getItems().isEmpty()) {
      throw new BakeryException("Order can't be empty!");
    }

    LinkedList<ItemOrder> itemOrderList = new LinkedList<>();

    for (Entry<String, Integer> itemToQuantityOrder : orderRequest.getItems().entrySet()) {
      ItemType item =
          Arrays.stream(ItemType.values())
              .filter(i -> i.getCode().equals(itemToQuantityOrder.getKey()))
              .findFirst()
              .orElseThrow(
                  () -> new BakeryException("Item " + itemToQuantityOrder.getKey() + " not found"));

      List<Pack> packs = findMinimalPacks(itemToQuantityOrder.getValue(), item.getPacks());

      if (packs == null) {
        throw new BakeryException(
            "Quantity "
                + itemToQuantityOrder.getValue()
                + " is not possible to package for item "
                + item);
      }

      itemOrderList.add(new ItemOrder(item.getCode(), packs));
    }

    return new Order(itemOrderList);
  }

  /**
   * Loop from 0 to the total quantity by keeping track of the minimum packages we can use. It uses
   * dynamic programming to find the previous related packaging. Then, it adds the current packaging
   * and calculate the minimum configuration for the current slot given the combinations available.
   *
   * @param quantity how many items in the order
   * @param packs different packaging for an item
   * @return
   */
  public List<Pack> findMinimalPacks(int quantity, List<Pack> packs) {
    // dp[i] will store the minimal pack combination to fulfill the order of size 'i'
    List<Pack>[] dp = new ArrayList[quantity + 1];
    dp[0] = new ArrayList<>(); // Base case: 0 quantity needs 0 packs

    // Loop through each quantity from 1 to 'quantity'
    for (int i = 1; i <= quantity; i++) {
      for (Pack pack : packs) {
        if (i >= pack.size() && dp[i - pack.size()] != null) {
          int pos = i - pack.size();

          List<Pack> newPackCombination = new ArrayList<>(dp[pos]);
          newPackCombination.add(pack);

          if (dp[i] == null || newPackCombination.size() < dp[i].size()) {
            dp[i] = newPackCombination; // Update if we find fewer packs
          }
        }
      }
    }
    return dp[quantity]; // Returns null if there's no valid combination
  }
}
