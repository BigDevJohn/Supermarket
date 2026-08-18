package jv.supermarket;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = SupermarketApplication.class)
@AutoConfigureMockMvc
public class SupermarketApplicationTests {

	@Autowired
	MockMvc mvc;

	private final String baseUrl = "/supermarket/";

	@Test
	public void testProductWithoutAuth_Forbidden() throws Exception {
		mvc.perform(get(baseUrl + "produto/all")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testProductWithAuth() throws Exception {
		mvc.perform(get(baseUrl + "produto/all")).andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testProductGetWithAuth() throws Exception {
		mvc.perform(get(baseUrl + "produto/1")).andExpect(status().isOk()).andExpect(
				content().string(
						"{\"id\":1,\"name\":\"Smartphone\",\"brand\":\"Samsung\",\"price\":3000.00,\"stock\":20,\"description\":\"O melhor da Samsung\",\"available\":true,\"categories\":[{\"id\":2,\"name\":\"Smartphones\"},{\"id\":4,\"name\":\"Eletrônicos\"}],\"images\":[],\"available\":true,\"id\":1}"));
	}

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testSaveProductWithAuth() throws Exception {
		String productJson = """
				    {
				        "name": "Smartphone",
				        "brand": "LG",
				        "price": 3000,
				        "stock": 20,
				        "description": "O melhor da Samsung",
				        "categories": ["Smartphones", "Eletrônicos"]
				    }
				""";

		mvc.perform(post("/supermarket/produto/save")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productJson)
				.with(csrf()))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("Smartphone"))
				.andExpect(jsonPath("$.brand").value("LG"));
	}

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testUpdateProductWithAuth() throws Exception {
		String productJson = """
				    {
				        "name": "Smartphone",
				        "brand": "LG",
				        "price": 3000,
				        "stock": 30,
				        "description": "O melhor da Samsung"
				    }
				""";

		mvc.perform(put("/supermarket/produto/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productJson)
				.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("Smartphone"))
				.andExpect(jsonPath("$.stock").value("30"));
	}

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testDeleteProductWithAuth() throws Exception {
		mvc.perform(delete("/supermarket/produto/4")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testCreateCategory() throws Exception {
		String jsonSend = """
				{
                    "name": "Ferramentas"
                }
				""";
		String jsonExpect = """
				{
				    "id": 5,
                    "name": "Ferramentas"
                }
				""";
		mvc.perform(post("/supermarket/categoria/save")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonSend)
				.with(csrf()))
				.andExpect(status().isCreated())
				.andExpect(content().json(jsonExpect));
	}

	@Test
	@WithMockUser(username = "joao@gmail.com", roles = "CLIENTE")
	public void testAddCartItem() throws Exception {
		mvc.perform(post(baseUrl + "/carrinho/addItem/1")
				.queryParam("quantity", "2")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "joao@gmail.com", roles = "CLIENTE")
	public void testCreateOrder() throws Exception {
		mvc.perform(post(baseUrl + "/pedido/criar")
				.with(csrf()))
				.andExpect(status().isCreated());
	}

}
