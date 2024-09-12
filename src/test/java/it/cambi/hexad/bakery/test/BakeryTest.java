/** */
package it.cambi.hexad.bakery.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.cambi.hexad.bakery.application.AppConfiguration;
import it.cambi.hexad.bakery.application.Application;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author luca
 */
@SpringBootTest(classes = {Application.class, AppConfiguration.class})
@AutoConfigureMockMvc
public class BakeryTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private WebApplicationContext context;

  private final MediaType mediaType = MediaType.APPLICATION_JSON;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  /** Test null order */
  @Test
  public void testRestBakeryNullOrder() throws Exception {
    mockMvc
        .perform(
            post("/order")
                .contentType(mediaType)
                .content(
                    """
            {
              "items": {}
            }
            """))
        .andExpect(status().isBadRequest());
  }

  /** Test order that should be correct */
  @Test
  public void testRestBakeryOrder() throws Exception {
    String response =
        mockMvc
            .perform(
                post("/order")
                    .contentType(mediaType)
                    .content(
                        """
      {
          "items": {
              "MB11": 14,
              "VS5": 10,
              "CF": 13
          }
      }
      """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertEquals(
        response,
        """
{"order":[{"item":"MB11","packs":[{"size":2,"price":9.95},{"size":2,"price":9.95},{"size":2,"price":9.95},{"size":8,"price":24.95}]},{"item":"VS5","packs":[{"size":5,"price":8.99},{"size":5,"price":8.99}]},{"item":"CF","packs":[{"size":3,"price":5.95},{"size":5,"price":9.95},{"size":5,"price":9.95}]}]}""");
  }

  /** Test wrong order */
  @Test
  public void testRestBakeryWrongOrder() throws Exception {
    mockMvc
        .perform(
            post("/order")
                .contentType(mediaType)
                .content(
                    """
      {
          "items": {
              "VS5": 7
          }
      }
      """))
        .andExpect(status().isBadRequest());
  }
}
