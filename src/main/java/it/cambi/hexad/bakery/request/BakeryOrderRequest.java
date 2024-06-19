/** */
package it.cambi.hexad.bakery.request;

import java.util.Map;
import lombok.Getter;

/**
 * @author luca
 */
@Getter
public class BakeryOrderRequest {
  private  Map<String, Integer> itemToCountMap;

  public BakeryOrderRequest() {}

  public BakeryOrderRequest(Map<String, Integer> itemToCountMap) {
    this.itemToCountMap = itemToCountMap;
  }
}
