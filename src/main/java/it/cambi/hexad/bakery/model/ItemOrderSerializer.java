/** */
package it.cambi.hexad.bakery.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * @author luca
 */
public class ItemOrderSerializer extends StdSerializer<ItemOrder> {

  public ItemOrderSerializer() {
    this(null);
  }

  public ItemOrderSerializer(Class<ItemOrder> t) {
    super(t);
  }

  @Override
  public void serialize(ItemOrder value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {

    jgen.writeStartObject();
    jgen.writeStringField("itemCode", value.getItemPack().getItem().getItemCode());
    jgen.writeNumberField("partialOrderPrice", value.getPartialOrderPrice());
    jgen.writeStringField(
        "itemQuantity",
        value.getItemPackOrderQuantity() + " x " + value.getItemPack().getItemQuantity());

    jgen.writeEndObject();
  }
}
