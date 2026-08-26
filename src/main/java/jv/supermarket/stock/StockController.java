package jv.supermarket.stock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/supermarket/stock")
public class StockController {

    private final StockService stockService;

    StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/product/{productId}")
    public StockDTO getProductStock(@PathVariable Long productId) {
        return stockService.getStockById(productId);
    }

    @PutMapping("/product/{productId}/entries")
    public StockDTO addProductEntries(@RequestBody StockDTO stock, @PathVariable Long productId) {
        return stockService.stockEntry(productId, stock.quantity());
    }

    @PutMapping("/product/{productId}/exits")
    public StockDTO subtractProductStock(@RequestBody StockDTO stock, @PathVariable Long productId) {
        return stockService.stockExit(productId, stock.quantity());
    }

}