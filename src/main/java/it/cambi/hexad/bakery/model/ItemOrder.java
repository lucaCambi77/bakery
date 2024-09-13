/** */
package it.cambi.hexad.bakery.model;

import java.util.List;

/**
 * @author luca
 */
public record ItemOrder(String item, int quantity, double totalPrice, List<OrderPack> packs) {}
