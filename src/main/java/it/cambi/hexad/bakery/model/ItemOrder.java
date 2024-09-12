/** */
package it.cambi.hexad.bakery.model;

import java.util.List;
import lombok.Data;

/**
 * @author luca
 */
@Data
public class ItemOrder {

  private String item;
  private List<Pack> packs;
}
