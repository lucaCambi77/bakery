/** */
package it.cambi.hexad.bakery.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author luca
 */
@Getter
@Setter
public class Item {

  private String itemCode;
  private double price;
  private String description;
}
