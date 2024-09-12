/** */
package it.cambi.hexad.bakery.model;

import java.util.List;

/**
 * @author luca
 */
public record ItemOrder(String item, List<Pack> packs) {}
