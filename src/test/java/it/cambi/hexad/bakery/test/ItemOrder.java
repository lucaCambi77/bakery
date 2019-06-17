/**
 * 
 */
package it.cambi.hexad.bakery.test;

/**
 * @author luca
 *
 */
public class ItemOrder {

	private BakeryOrder order;
	private ItemPack itemPack;
	private Integer itemPackOrderQuantity;

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

}
