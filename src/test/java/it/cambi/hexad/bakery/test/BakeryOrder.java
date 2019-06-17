/**
 * 
 */
package it.cambi.hexad.bakery.test;

import java.util.Date;
import java.util.List;

/**
 * @author luca
 *
 */
public class BakeryOrder {

	private Long orderId;
	private Date orderDate;
	private List<ItemPack> itemPackList;
	private String paymentType;
	private String orderStatus;
	private double orderPrice;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public List<ItemPack> getItemPackList() {
		return itemPackList;
	}

	public void setItemPackList(List<ItemPack> itemPackList) {
		this.itemPackList = itemPackList;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public double getOrderPrice() {
		return orderPrice;
	}

	public void setOrderPrice(double orderPrice) {
		this.orderPrice = orderPrice;
	}

}
