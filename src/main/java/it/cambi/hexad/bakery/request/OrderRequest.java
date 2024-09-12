/** */
package it.cambi.hexad.bakery.request;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * @author luca
 */
@Getter
public class OrderRequest {
  private Map<String, Integer> items = new HashMap<>();

  public OrderRequest() {}

  public OrderRequest(Map<String, Integer> items) {
    this.items = items;
  }
}
