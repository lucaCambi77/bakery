/** */
package it.cambi.hexad.bakery.enums;

import java.util.Map;
import lombok.Getter;

/**
 * @author luca
 *     <p>Item prototype for order simulation
 */
@Getter
public enum ItemType {
  VS5(Map.of(3, 6.99, 5, 8.99), "VS5", "Vegemite Scroll"),
  MB11(Map.of(2, 9.95, 5, 16.95, 8, 24.95), "MB11", "Blueberry Muffin"),
  CF(Map.of(3, 5.95, 5, 9.95, 9, 16.99), "CF", "Croissant");

  private final Map<Integer, Double> packToPrice;
  private final String code;
  private final String descr;

  ItemType(Map<Integer, Double> packToPrice, String code, String descr) {
    this.packToPrice = packToPrice;
    this.code = code;
    this.descr = descr;
  }
}
