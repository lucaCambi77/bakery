/**
 * 
 */
package it.cambi.hexad.bakery.model;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author luca
 *
 */
@JsonSerialize(using = ItemPackSerializer.class)
public class ItemPack {

	private Pack pack;
	private Item item;
	private int itemQuantity;
	private Date dateStart;
	private Date dateEnd;
	private double itemPackPrice;
	private int packMinOrderQuantity;
	private int packMaxOrderQuantity;
	@JsonIgnore
	private Set<ItemOrder> itemOrderList;

	public Pack getPack() {
		return pack;
	}

	public void setPack(Pack pack) {
		this.pack = pack;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public int getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public int getPackMinOrderQuantity() {
		return packMinOrderQuantity;
	}

	public void setPackMinOrderQuantity(int packMinOrderQuantity) {
		this.packMinOrderQuantity = packMinOrderQuantity;
	}

	public int getPackMaxOrderQuantity() {
		return packMaxOrderQuantity;
	}

	public void setPackMaxOrderQuantity(int packMaxOrderQuantity) {
		this.packMaxOrderQuantity = packMaxOrderQuantity;
	}

	public double getItemPackPrice() {
		return itemPackPrice;
	}

	public void setItemPackPrice(double itemPackPrice) {
		this.itemPackPrice = itemPackPrice;
	}

	public Set<ItemOrder> getItemOrderList() {
		return itemOrderList;
	}

	public void setItemOrderList(Set<ItemOrder> itemOrderList) {
		this.itemOrderList = itemOrderList;
	}

}
