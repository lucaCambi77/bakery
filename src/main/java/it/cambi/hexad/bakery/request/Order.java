/** */
package it.cambi.hexad.bakery.request;

import it.cambi.hexad.bakery.model.ItemOrder;
import java.util.List;

/**
 * @author luca
 */
public record Order(List<ItemOrder> order) {}
