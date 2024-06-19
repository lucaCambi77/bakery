/** */
package it.cambi.hexad.bakery.request;

import it.cambi.hexad.bakery.model.ItemOrder;
import java.util.List;
import lombok.Data;

/**
 * @author luca
 */
@Data
public class BakeryOrderReport {

  private List<ItemOrder> itemOrderList;
}
