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
        """
        14 MB11 $54.80
        3 x 2 $29.85
        1 x 8 $24.95
        10 VS5 $17.98
        2 x 5 $17.98
        13 CF $25.85
        1 x 3 $5.95
        2 x 5 $19.90
        """,
        response);
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
