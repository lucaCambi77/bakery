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
public class ItemPackSerializer extends StdSerializer<ItemPack> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1265786048973236060L;

	public ItemPackSerializer() {
		this(null);
	}

	public ItemPackSerializer(Class<ItemPack> t) {
		super(t);
	}

	@Override
	public void serialize(ItemPack value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {

		jgen.writeStartObject();
		jgen.writeStringField("itemCode", value.getItem().getItemCode());
		jgen.writeStringField("description", value.getItem().getDescription());
		jgen.writeNumberField("itemQuantity", value.getItemQuantity());
		jgen.writeNumberField("itemPackPrice", value.getItemPackPrice());

		jgen.writeEndObject();
	}
}
