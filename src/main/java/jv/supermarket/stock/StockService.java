package jv.supermarket.stock;

import org.springframework.stereotype.Service;

import jv.supermarket.product.Product;
import jv.supermarket.shared.customexception.OutOfStockException;
import jv.supermarket.shared.customexception.ResourceNotFoundException;

@Service
public class StockService {

    private final StockRepository stockRepository;

    StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public StockDTO getStockById(Long id) {
        final Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with id: " + id + " not found"));
        return new StockDTO(stock.getQuantity());
    }

    public Stock buildStock(Integer quantity, Product product) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Initial stock quantity cannot be negative");
        }
        return new Stock(quantity, product);
    }

    public StockDTO stockEntry(Long productId, Integer quantity) {
        Stock stock = stockRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with product id: " + productId + " not found"));

        stock.setQuantity(stock.getQuantity() + quantity);
        stockRepository.save(stock);

        return new StockDTO(stock.getQuantity());
    }

    public StockDTO stockExit(Long productId, Integer quantity) {
        Stock stock = stockRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with product id: " + productId + " not found"));
        if (stock.getQuantity() < quantity) {
            throw new OutOfStockException("Insufficient stock quantity");
        }
        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);
        return new StockDTO(stock.getQuantity());
    }

    public boolean isStockAvailable(Integer quantity, Long productId) {
        Stock stock = stockRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with product id: " + productId + " not found"));
        return quantity <= stock.getQuantity();
    }
}
