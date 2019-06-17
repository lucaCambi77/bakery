/**
 * 
 */
package it.cambi.hexad.bakery.model;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author luca
 *
 */
public class BakeryOrder {

	private Long orderId;
	private Date orderDate;

	@JsonIgnore
	private Set<ItemOrder> itemOrderList;

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

	public Set<ItemOrder> getItemOrderList() {
		return itemOrderList;
	}

	public void setItemOrderList(Set<ItemOrder> itemOrderList) {
		this.itemOrderList = itemOrderList;
	}

}
