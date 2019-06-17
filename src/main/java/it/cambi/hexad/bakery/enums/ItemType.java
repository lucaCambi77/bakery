/**
 * 
 */
package it.cambi.hexad.bakery.enums;

import java.util.LinkedHashMap;

/**
 * @author luca
 * 
 *         Item prototype for order simulation
 */

@SuppressWarnings("serial")
public enum ItemType {

	VS5(new LinkedHashMap<Integer, Double>() {
		{
			put(3, 6.99);
			put(5, 8.99);
		}
	}, "VS5", "Vegemite Scroll"), MB11(new LinkedHashMap<Integer, Double>() {
		{
			put(2, 9.95);
			put(5, 16.95);
			put(8, 24.95);
		}
	}, "MB11", "Blueberry Muffin"), CF(new LinkedHashMap<Integer, Double>() {
		{
			put(3, 5.95);
			put(5, 9.95);
			put(9, 16.99);

		}
	}, "CF", "Croissant");

	private LinkedHashMap<Integer, Double> packToPrice = new LinkedHashMap<Integer, Double>();
	private String code;
	private String descr;

	ItemType(LinkedHashMap<Integer, Double> packToPrice, String code, String descr) {
		this.packToPrice = packToPrice;
		this.code = code;
		this.descr = descr;
	}

	public LinkedHashMap<Integer, Double> getPackToPrice() {
		return packToPrice;
	}

	public String getCode() {
		return code;
	}

	public String getDescr() {
		return descr;
	}
}
