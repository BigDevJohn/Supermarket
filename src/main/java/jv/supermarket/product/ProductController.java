package jv.supermarket.product;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jv.supermarket.shared.ApiError;
import jv.supermarket.shared.Response;
import jv.supermarket.shared.customexception.ResourceNotFoundException;

@RestController
@RequestMapping("/supermarket/product")
public class ProductController {

    private final ProductService productService;

    ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Saves a new product", description = "Receives a product DTO with its category name(s) (the category must already exist). A product with the same name and brand cannot exist twice.")
    @ApiResponses({
        @ApiResponse(responseCode = "409", description = "Product with same name and brand already exists",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "404", description = "No category found with the given names",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "201", description = "Product created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class)))
    })
    @PostMapping("/save")
    public ResponseEntity<ProductDTO> saveProduct(@RequestBody @Valid ProductRequestDTO product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(product));
    }

    @Operation(summary = "Finds a product by id")
    @ApiResponses({
        @ApiResponse(responseCode = "404", description = "No product found with the given id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "200", description = "Product found successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "Product id. Example: 1") @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductDTOById(id));
    }

    @Operation(summary = "Returns all products")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Products found successfully",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))))
    })
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
    }

    @Operation(summary = "Finds products by name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Products found successfully",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)))),
        @ApiResponse(responseCode = "404", description = "No products found with the given name",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/by-name")
    public ResponseEntity<List<ProductDTO>> getProductsByName(
            @Parameter(description = "Product name to search for") @RequestParam String name) {
        List<ProductDTO> products = productService.getProductsByName(name);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found with name: " + name);
        }
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @Operation(summary = "Finds products by brand")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Products found successfully",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)))),
        @ApiResponse(responseCode = "404", description = "No products found with the given brand",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/by-brand")
    public ResponseEntity<List<ProductDTO>> getProductsByBrand(
            @Parameter(description = "Brand to search for") @RequestParam String brand) {
        List<ProductDTO> products = productService.getProductsByBrand(brand);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found with brand: " + brand);
        }
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @Operation(summary = "Finds a product by brand and name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product found successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "No product found with the given brand and name",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/by-brand-and-name")
    public ResponseEntity<ProductDTO> getProductByBrandAndName(
            @Parameter(description = "Brand name") @RequestParam String brand,
            @Parameter(description = "Product name") @RequestParam String name) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductByBrandAndName(brand, name));
    }

    @Operation(summary = "Finds products by category name")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Products found successfully",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class)))),
        @ApiResponse(responseCode = "404", description = "No products found in the given category",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/by-category-name")
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryName(
            @Parameter(description = "Category name") @RequestParam String name) {
        List<ProductDTO> products = productService.getProductsByCategoryName(name);
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found in category: " + name);
        }
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @Operation(summary = "Updates a product", description = "Updates a product by id with new information from the request body")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "No product found with the given id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
            @RequestBody @Valid Product product) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(product, id));
    }

    @Operation(summary = "Deletes a product by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product deleted successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "404", description = "No product found with the given id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new Response(Instant.now(), "Product deleted successfully"));
    }

    @Operation(summary = "Makes a product available")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product made available successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "404", description = "No product found with the given id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "409", description = "Product was already available",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/disponibilizar")
    public ResponseEntity<Response> makeProductAvailable(@PathVariable Long id) {
        productService.makeAvailable(id);
        return ResponseEntity.ok(new Response(Instant.now(), "Product made available successfully."));
    }

    @Operation(summary = "Makes a product unavailable")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product made unavailable successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "404", description = "No product found with the given id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "409", description = "Product was already unavailable",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/indisponibilizar")
    public ResponseEntity<Response> makeProductUnavailable(@PathVariable Long id) {
        productService.makeUnavailable(id);
        return ResponseEntity.ok(new Response(Instant.now(), "Product made unavailable successfully."));
    }

    @Operation(summary = "Increases product stock", description = "Increases the stock of a product by a given quantity")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Product stock updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDTO.class))),
        @ApiResponse(responseCode = "404", description = "No product found with the given id",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}/addEstoque")
    public ResponseEntity<ProductDTO> increaseStock(@PathVariable Long id,
            @Parameter(description = "Quantity to add to the product stock") @RequestParam int quantity) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.addProductStock(quantity, id));
    }

}
