/**
 * 
 */
package it.cambi.hexad.bakery.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import it.cambi.hexad.bakery.model.ItemPack;

/**
 * @author luca
 *
 */
public class CollectionBeans {

	@Autowired
	private List<ItemPack> itemPackList;

	public List<ItemPack> getItemPackList() {
		return itemPackList;
	}

	public void setItemPackList(List<ItemPack> itemPackList) {
		this.itemPackList = itemPackList;
	}

}
