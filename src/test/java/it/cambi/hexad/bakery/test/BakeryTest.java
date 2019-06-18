/**
 * 
 */
package it.cambi.hexad.bakery.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.cambi.hexad.bakery.application.AppConfiguration;
import it.cambi.hexad.bakery.application.Application;
import it.cambi.hexad.bakery.enums.ItemType;
import it.cambi.hexad.bakery.model.ItemOrder;
import it.cambi.hexad.bakery.model.ItemPack;
import it.cambi.hexad.bakery.report.BakeryOrderReport;
import it.cambi.hexad.bakery.services.OrderService;

/**
 * @author luca
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class, AppConfiguration.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("serial")
public class BakeryTest {

	@Autowired
	private List<ItemPack> itemPackList;

	@Autowired
	private OrderService orderService;

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private static Map<String, Map<Integer, Integer>> solutionMap = new HashMap<String, Map<Integer, Integer>>() {
		{
			put(ItemType.MB11.getCode(), new HashMap<Integer, Integer>() {
				{
					put(8, 1);
					put(2, 3);

				}
			});

			put(ItemType.VS5.getCode(), new HashMap<Integer, Integer>() {
				{
					put(5, 2);

				}
			});

			put(ItemType.CF.getCode(), new HashMap<Integer, Integer>() {
				{
					put(5, 2);
					put(3, 1);

				}
			});
		}
	};

	@Test
	public void createItemPackList() {

		Assert.assertEquals(8, itemPackList.size());
	}

	@Test
	public void testBakeryOrder() throws JsonProcessingException {

		NavigableMap<String, Integer> orderRequest = new TreeMap<String, Integer>() {
			{
				{
					{
						put(ItemType.MB11.getCode(), 14);
						put(ItemType.VS5.getCode(), 10);
						put(ItemType.CF.getCode(), 13);

					}
				}
			}
		};

		BakeryOrderReport report = orderService.setBakeryOrder(orderRequest);

		testOrderResponse(orderRequest, report);
	}

	@Test
	public void testRestGreeting() throws Exception {
		ResponseEntity<String> entity = restTemplate.getForEntity("http://localhost:" + this.port + "/", String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
	}

	/**
	 * Test null order
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRestBakeryNullOrder() throws Exception {
		HttpEntity<BakeryOrderReport> request = new HttpEntity<BakeryOrderReport>(new BakeryOrderReport());

		ResponseEntity<BakeryOrderReport> entity = restTemplate
				.postForEntity("http://localhost:" + this.port + "/order", request, BakeryOrderReport.class);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
	}

	/**
	 * Test order
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRestBakeryOrder() throws Exception {
		BakeryOrderReport report = new BakeryOrderReport();
		NavigableMap<String, Integer> orderRequest = new TreeMap<String, Integer>() {
			{
				{
					{
						put(ItemType.MB11.getCode(), 14);
						put(ItemType.VS5.getCode(), 10);
						put(ItemType.CF.getCode(), 13);

					}
				}
			}
		};

		report.setItemToCountMap(orderRequest);

		HttpEntity<BakeryOrderReport> request = new HttpEntity<BakeryOrderReport>(report);

		ResponseEntity<BakeryOrderReport> entity = restTemplate
				.postForEntity("http://localhost:" + this.port + "/order", request, BakeryOrderReport.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());

		// testOrderResponse(orderRequest, entity.getBody());

	}

	/**
	 * @param orderRequest
	 * @param report
	 */
	private void testOrderResponse(NavigableMap<String, Integer> orderRequest, BakeryOrderReport report) {
		List<ItemOrder> itemOrderList = report.getItemOrderList();

		solutionMap.entrySet().forEach(m -> {
			m.getValue().entrySet().forEach(m1 -> {
				ItemOrder itemOrder = itemOrderList.stream()
						.filter(i -> i.getItemPack().getItem().getItemCode().equals(m.getKey()))
						.filter(i -> i.getItemPack().getItemQuantity() == m1.getKey()).findFirst().orElse(null);

				Assert.assertNotNull(itemOrder);
				Assert.assertEquals(itemOrder.getItemPackOrderQuantity(), m1.getValue());
			});
		});

		Assert.assertEquals(orderRequest, report.getItemToCountMap());
	}
}
