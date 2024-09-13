/** */
package it.cambi.hexad.bakery.request;

import java.util.Map;

/**
 * @author luca
 */
public record OrderRequest(Map<String, Integer> items) {}
