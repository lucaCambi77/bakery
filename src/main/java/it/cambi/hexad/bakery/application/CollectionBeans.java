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

}
