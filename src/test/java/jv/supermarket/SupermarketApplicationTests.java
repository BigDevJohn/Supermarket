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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = SupermarketApplication.class)
@AutoConfigureMockMvc
public class SupermarketApplicationTests {

	@Autowired
	MockMvc mvc;

	private final String url_padrao = "/supermarket/";

	@Test
	public void testProdutoWithoutAuth_Forbidden() throws Exception {
		mvc.perform(get(url_padrao + "produto/all")).andExpect(status().isForbidden());
	}

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testProdutoWithAuth() throws Exception {
		mvc.perform(get(url_padrao + "produto/all")).andExpect(status().isOk());
	}

	@Test
	// @WithMockUser(roles = { "USER" })
	@WithUserDetails("admin@gmail.com")
	public void testProdutoGetWithAuth() throws Exception {
		mvc.perform(get(url_padrao + "produto/1")).andExpect(status().isOk()).andExpect(
				content().string(
						"{\"id\":1,\"nome\":\"Smartphone\",\"marca\":\"Samsung\",\"preco\":3000.00,\"estoque\":20,\"descricao\":\"O melhor da Samsung\",\"disponivel\":true,\"categorias\":[{\"id\":2,\"nome\":\"Smartphones\"},{\"id\":4,\"nome\":\"EletrÃ´nicos\"}],\"imagens\":[]}"));
	}

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testSaveProdutoWithAuth() throws Exception {
		// Corpo da requisição em JSON
		String produtoJson = """
				    {
				        "nome": "Smartphone",
				        "marca": "LG",
				        "preco": 3000,
				        "estoque": 20,
				        "descricao": "O melhor da Samsung",
				        "categorias": ["Smartphones", "Eletrônicos"]
				    }
				""";

		// Executando a requisição POST
		mvc.perform(post("/supermarket/produto/save")
				.contentType(MediaType.APPLICATION_JSON)
				.content(produtoJson)
				.with(csrf())) // Inclui CSRF para endpoints protegidos
				.andExpect(status().isCreated()) // Verifica se o status é 201 Created
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Verifica se a resposta é JSON
				.andExpect(jsonPath("$.nome").value("Smartphone")) // Verifica o nome no JSON de resposta
				.andExpect(jsonPath("$.marca").value("LG")); // Verifica a marca no JSON de resposta
	}
	@Test
	@WithUserDetails("admin@gmail.com")
	public void testUpdateProdutoWithAuth() throws Exception {
		String produtoJson = """
				    {
				        "nome": "Smartphone",
				        "marca": "LG",
				        "preco": 3000,
				        "estoque": 30,
				        "descricao": "O melhor da Samsung"
				    }
				""";

		mvc.perform(put("/supermarket/produto/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(produtoJson)
				.with(csrf())) // Inclui CSRF para endpoints protegidos
				.andExpect(status().isOk()) // Verifica se o status é 201 Created
				.andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Verifica se a resposta é JSON
				.andExpect(jsonPath("$.nome").value("Smartphone")) // Verifica o nome no JSON de resposta
				.andExpect(jsonPath("$.estoque").value("30")); // Verifica a marca no JSON de resposta
	}

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testDeleteProdutoWithAuth() throws Exception {
        mvc.perform(delete("/supermarket/produto/4")
                .with(csrf())) // 
                .andExpect(status().isOk()); 
    }

	@Test
	@WithUserDetails("admin@gmail.com")
	public void testCreateCategoria() throws Exception {
		String jsonEnviar ="""
				{
                    "nome": "Ferramentas"
                }
				""";
				String jsonReceber ="""
				{
				    "id": 5,
                    "nome": "Ferramentas"
                }
				""";
			mvc.perform(post("/supermarket/categoria/save")
				.contentType(MediaType.APPLICATION_JSON) 
				.content(jsonEnviar) 
				.with(csrf())) 
				.andExpect(status().isCreated()) 
				.andExpect(content().json(jsonReceber));
	}

	@Test
	@WithMockUser(username = "joao@gmail.com", roles = "CLIENTE")
	public void testAddItemCarrinho() throws Exception {
		mvc.perform(post(url_padrao+"/carrinho/addItem/1")
				.queryParam("quantidade","2")
				.with(csrf()))
				.andExpect(status().isOk());
	}
	@Test
	@WithMockUser(username = "joao@gmail.com", roles = "CLIENTE")
	public void testCriarPedido() throws Exception {
		mvc.perform(post(url_padrao+"/pedido/criar")
				.with(csrf()))
				.andExpect(status().isCreated());
	}

}
