/** */
package it.cambi.hexad.bakery.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author luca
 */
@JsonSerialize(using = ItemOrderSerializer.class)
@Data
public class ItemOrder {

  private ItemPack itemPack;
  private Integer itemPackOrderQuantity;
  private double partialOrderPrice;
}
