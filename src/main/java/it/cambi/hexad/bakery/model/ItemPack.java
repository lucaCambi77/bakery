/** */
package it.cambi.hexad.bakery.model;

import lombok.Data;

/**
 * @author luca
 */
@Data
public class ItemPack {

  private Item item;
  private int itemQuantity;
  private double itemPackPrice;
}
