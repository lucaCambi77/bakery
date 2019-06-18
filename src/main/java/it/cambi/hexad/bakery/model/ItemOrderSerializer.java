/**
 * 
 */
package it.cambi.hexad.bakery.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author luca
 *
 */
public class ItemOrderSerializer extends StdSerializer<ItemOrder> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1265786048973236060L;

	public ItemOrderSerializer() {
		this(null);
	}

	public ItemOrderSerializer(Class<ItemOrder> t) {
		super(t);
	}

	@Override
	public void serialize(ItemOrder value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		jgen.writeStartObject();
		jgen.writeStringField("itemCode", value.getItemPack().getItem().getItemCode());
		jgen.writeNumberField("partialOrderPrice", value.getPartialOrderPrice());
		jgen.writeStringField("itemQuantity",
				value.getItemPackOrderQuantity() + " x " + value.getItemPack().getItemQuantity());

		jgen.writeEndObject();
	}
}
