/**
 * 
 */
package it.cambi.hexad.bakery.test;

import java.util.List;
import java.util.Map;

/**
 * @author luca
 *
 */
public class BakeryOrderReport {

	private Map<String, Integer> itemToCountMap;
	private BakeryOrder bakeryOrder;
	private List<ItemOrder> itemOrderList;

	public Map<String, Integer> getItemToCountMap() {
		return itemToCountMap;
	}

	public void setItemToCountMap(Map<String, Integer> itemToCountMap) {
		this.itemToCountMap = itemToCountMap;
	}

	public BakeryOrder getBakeryOrder() {
		return bakeryOrder;
	}

	public void setBakeryOrder(BakeryOrder bakeryOrder) {
		this.bakeryOrder = bakeryOrder;
	}

	public List<ItemOrder> getItemOrderList() {
		return itemOrderList;
	}

	public void setItemOrderList(List<ItemOrder> itemOrderList) {
		this.itemOrderList = itemOrderList;
	}

}
