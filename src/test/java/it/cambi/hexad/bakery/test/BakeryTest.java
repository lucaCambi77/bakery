/** */
package it.cambi.hexad.bakery.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import it.cambi.hexad.bakery.application.AppConfiguration;
import it.cambi.hexad.bakery.application.Application;
import it.cambi.hexad.bakery.enums.ItemType;
import it.cambi.hexad.bakery.model.ItemOrder;
import it.cambi.hexad.bakery.request.BakeryOrderReport;
import it.cambi.hexad.bakery.request.BakeryOrderRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author luca
 */
@SpringBootTest(
    classes = {Application.class, AppConfiguration.class},
    webEnvironment = WebEnvironment.RANDOM_PORT)
public class BakeryTest {

  @Autowired private TestRestTemplate restTemplate;

  @LocalServerPort private int port;

  private final Map<String, Integer> orderRequest =
      Map.of(ItemType.MB11.getCode(), 14, ItemType.VS5.getCode(), 10, ItemType.CF.getCode(), 13);

  private final Map<String, Map<Integer, Integer>> solutionMap =
      Map.of(
          ItemType.MB11.getCode(),
          Map.of(8, 1, 2, 3),
          ItemType.VS5.getCode(),
          Map.of(5, 2),
          ItemType.CF.getCode(),
          Map.of(5, 2, 3, 1));

  /** Test null order */
  @Test
  public void testRestBakeryNullOrder() {
    HttpEntity<BakeryOrderReport> request = new HttpEntity<>(new BakeryOrderReport());

    ResponseEntity<BakeryOrderReport> entity =
        restTemplate.postForEntity(
            "http://localhost:" + this.port + "/order", request, BakeryOrderReport.class);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
  }

  /** Test order that should be correct */
  @Test
  public void testRestBakeryOrder() {
    BakeryOrderRequest request = new BakeryOrderRequest(orderRequest);

    HttpEntity<BakeryOrderRequest> httpEntity = new HttpEntity<>(request);

    ResponseEntity<BakeryOrderReport> entity =
        restTemplate.postForEntity(
            "http://localhost:" + this.port + "/order", httpEntity, BakeryOrderReport.class);

    assertEquals(HttpStatus.OK, entity.getStatusCode());
  }

  /** Test wrong order */
  @Test
  public void testRestBakeryWrongOrder() {
    BakeryOrderRequest request = new BakeryOrderRequest(Map.of(ItemType.VS5.getCode(), 7));
    HttpEntity<BakeryOrderRequest> httpEntity = new HttpEntity<>(request);

    ResponseEntity<BakeryOrderReport> entity =
        restTemplate.postForEntity(
            "http://localhost:" + this.port + "/order", httpEntity, BakeryOrderReport.class);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
  }

  /**
   * Method to compare the response from the order service with expected solution
   *
   * @param report
   */
  private void testOrderResponse(BakeryOrderReport report) {
    List<ItemOrder> itemOrderList = report.getItemOrderList();

    solutionMap.forEach(
        (key, value) ->
            value.forEach(
                (key1, value1) -> {
                  ItemOrder itemOrder =
                      itemOrderList.stream()
                          .filter(i -> i.getItemPack().getItem().getItemCode().equals(key))
                          .filter(i -> i.getItemPack().getItemQuantity() == key1)
                          .findFirst()
                          .orElse(null);

                  assertNotNull(itemOrder);
                  assertEquals(itemOrder.getItemPackOrderQuantity(), value1);
                }));
  }
}
