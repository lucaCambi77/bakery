/**
 * 
 */
package it.cambi.hexad.bakery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author luca
 *
 */
@JsonSerialize(using = ItemOrderSerializer.class)
public class ItemOrder {

	@JsonIgnore
	private BakeryOrder order;

	private ItemPack itemPack;
	private Integer itemPackOrderQuantity;
	private double partialOrderPrice;

	public BakeryOrder getOrder() {
		return order;
	}

	public void setOrder(BakeryOrder order) {
		this.order = order;
	}

	public ItemPack getItemPack() {
		return itemPack;
	}

	public void setItemPack(ItemPack itemPack) {
		this.itemPack = itemPack;
	}

	public Integer getItemPackOrderQuantity() {
		return itemPackOrderQuantity;
	}

	public void setItemPackOrderQuantity(Integer itemPackOrderQuantity) {
		this.itemPackOrderQuantity = itemPackOrderQuantity;
	}

	public double getPartialOrderPrice() {
		return partialOrderPrice;
	}

	public void setPartialOrderPrice(double partialOrderPrice) {
		this.partialOrderPrice = partialOrderPrice;
	}

}
