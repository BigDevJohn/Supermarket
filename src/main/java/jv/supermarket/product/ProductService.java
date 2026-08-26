package jv.supermarket.product;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jv.supermarket.category.Category;
import jv.supermarket.category.CategoryRepository;
import jv.supermarket.image.ImageDTO;
import jv.supermarket.shared.customexception.AlreadyExistException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;
import jv.supermarket.stock.Stock;
import jv.supermarket.stock.StockService;
import jv.supermarket.user.User;
import jv.supermarket.user.UserService;

@Service
public class ProductService {

    final ProductRepository productRepository;

    final CategoryRepository categoryRepository;

    final UserService userService;

    final StockService stockService;

    ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
            UserService userService, StockService stockService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userService = userService;
        this.stockService = stockService;
    }

    private boolean productExists(String name, String brand) {
        return productRepository.existsByNameAndBrand(name, brand);
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products;
        if (isClient()) {
            products = productRepository.findAllByAvailable(true);
        } else {
            products = productRepository.findAll();
        }
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ProductDTO saveProduct(ProductRequestDTO dto) {
        if (productExists(dto.getName(), dto.getBrand())) {
            throw new AlreadyExistException("A product with this name and brand already exists.");
        }
        Product product = new Product();
        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());

        Stock stock = stockService.buildStock(dto.getStock(), product);

        product.setStock(stock);

        addCategoriesToProduct(product, dto.getCategories());

        return convertToDTO(productRepository.save(product));
    }

    public void addCategoriesToProduct(Product product, List<String> categories) {
        for (String categoryName : categories) {
            if (categoryRepository.existsByName(categoryName)) {
                Category category = categoryRepository.findByName(categoryName);
                product.getCategories().add(category);
            } else {
                throw new ResourceNotFoundException("Category with name: " + categoryName + " not found");
            }
        }
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id: " + id + " not found"));
    }

    public ProductDTO getProductDTOById(Long id) {
        return convertToDTO(getProductById(id));
    }

    public ProductDTO updateProduct(Product product, Long id) {
        if (productRepository.existsById(id)) {
            Product savedProduct = getProductById(id);

            savedProduct.setName(product.getName());
            savedProduct.setBrand(product.getBrand());
            savedProduct.setPrice(product.getPrice());
            savedProduct.setDescription(product.getDescription());

            return convertToDTO(productRepository.save(savedProduct));
        }
        throw new ResourceNotFoundException("Product with id: " + id + " not found");
    }

    public void deleteProductById(Long id) {
        if (productRepository.existsById(id)) {
            try {
                productRepository.deleteById(id);
            } catch (DataIntegrityViolationException e) {
                makeUnavailable(id);
            }
        } else {
            throw new ResourceNotFoundException("Product with id: " + id + " not found");
        }
    }

    public void makeUnavailable(Long id) {
        if (productRepository.existsById(id)) {
            Product product = productRepository.findById(id).get();
            if (!product.isAvailable()) {
                throw new IllegalStateException("The product was already unavailable.");
            }
            product.setAvailable(false);
            productRepository.save(product);
        } else {
            throw new ResourceNotFoundException("Product with id: " + id + " not found");
        }
    }

    public void makeAvailable(Long id) {
        if (productRepository.existsById(id)) {
            Product product = productRepository.findById(id).get();
            if (product.isAvailable()) {
                throw new IllegalStateException("The product was already available.");
            }
            product.setAvailable(true);
            productRepository.save(product);
        } else {
            throw new ResourceNotFoundException("Product with id: " + id + " not found");
        }
    }

    public List<ProductDTO> getProductsByName(String name) {
        List<Product> products;
        if (isClient()) {
            products = productRepository.findByNameContainingIgnoreCaseAndAvailable(name, true);
        } else {
            products = productRepository.findByNameContainingIgnoreCase(name);
        }
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByBrand(String brand) {
        List<Product> products;
        if (isClient()) {
            products = productRepository.findByBrandContainingIgnoreCaseAndAvailable(brand, true);
        } else {
            products = productRepository.findByBrandContainingIgnoreCase(brand);
        }
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProductDTO getProductByBrandAndName(String brand, String name) {
        if (productExists(name, brand)) {
            Product product;
            if (isClient()) {
                product = productRepository.findByBrandAndNameAndAvailable(brand, name, true);
            } else {
                product = productRepository.findByBrandAndName(brand, name);
            }
            return convertToDTO(product);
        }
        throw new ResourceNotFoundException(
                "No product found with brand: " + brand + " and name: " + name);
    }

    public List<ProductDTO> getProductsByCategoryName(String name) {
        List<Product> products;
        if (isClient()) {
            products = productRepository.findByCategoryNameContainingAndAvailable(name);
        } else {
            products = productRepository.findByCategoryNameContaining(name);
        }

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found in category with name: " + name);
        }
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setPrice(product.getPrice());
        dto.setDescription(product.getDescription());
        dto.setAvailable(product.isAvailable());

        // Convert Set<Category> → Set<String>
        Set<String> categoryNames = product.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toSet());
        dto.setCategories(categoryNames);

        // Convert List<Image> → List<ImageDTO>
        if (product.getImages() != null) {
            List<ImageDTO> imageDTOs = product.getImages().stream()
                    .map(img -> new ImageDTO(img.getFileName(), img.getFileType(), img.getDownloadUrl()))
                    .collect(Collectors.toList());
            dto.setImages(imageDTOs);
        }

        return dto;
    }

    public boolean existById(Long productId) {
        return productRepository.existsById(productId);
    }

    private boolean isClient() {
        User user = userService.getLoggedUser();
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_CLIENTE"));
    }

}
