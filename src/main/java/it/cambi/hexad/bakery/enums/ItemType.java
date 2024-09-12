/** */
package it.cambi.hexad.bakery.enums;

import it.cambi.hexad.bakery.model.Pack;
import java.util.List;
import lombok.Getter;

/**
 * @author luca
 *     <p>Item prototype for order simulation
 */
@Getter
public enum ItemType {
  VS5(List.of(new Pack(5, 8.99), new Pack(3, 6.99)), "VS5", "Vegemite Scroll"),
  MB11(
      List.of(new Pack(8, 24.95), new Pack(5, 16.95), new Pack(2, 9.95)),
      "MB11",
      "Blueberry Muffin"),
  CF(List.of(new Pack(9, 16.99), new Pack(5, 9.95), new Pack(3, 5.95)), "CF", "Croissant");

  private final List<Pack> packs;
  private final String code;
  private final String description;

  ItemType(List<Pack> packToPrice, String code, String descr) {
    this.packs = packToPrice;
    this.code = code;
    this.description = descr;
  }
}
