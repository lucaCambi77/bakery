/** */
package it.cambi.hexad.bakery.request;

import it.cambi.hexad.bakery.model.ItemOrder;
import it.cambi.hexad.bakery.model.OrderPack;
import java.util.List;

/**
 * @author luca
 */
public record Order(List<ItemOrder> order) {

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for (ItemOrder itemOrder : this.order) {
      // Append quantity, product code, and total price
      sb.append(itemOrder.quantity())
          .append(" ")
          .append(itemOrder.item())
          .append(" $")
          .append(String.format("%.2f", itemOrder.totalPrice()))
          .append("\n");

      // Append breakdown details (pack size and pack price)
      for (OrderPack breakdown : itemOrder.packs()) {
        sb.append(breakdown.count())
            .append(" x ")
            .append(breakdown.size())
            .append(" $")
            .append(String.format("%.2f", breakdown.price()))
            .append("\n");
      }
    }

    return sb.toString();
  }
}
