package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ModelDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ShopDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.StockDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ShopService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.StockService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl.StockServiceIMPL;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@RestController
@RequestMapping("api/stock")
@RequiredArgsConstructor
@CrossOrigin
public class StockController {
    private final StockService stockService;
    private final StockServiceIMPL stockServiceIMPL;

    @PostMapping()
    public ResponseEntity<StandardResponse> saveStock(@RequestBody @Valid StockDTO dto) {
        stockService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponse(200, "Stock Saved", null));
    }

    @PutMapping("/{stockId}")
    public ResponseEntity<StandardResponse> updateStock(@PathVariable Long stockId, @RequestBody StockDTO updatedStockDTO) {
        stockServiceIMPL.updateStock(stockId, updatedStockDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new StandardResponse(200, "Stock updated successfully", null));
    }

    @GetMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> getAllStocks() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Stocked Fetched", stockService.findAll()), HttpStatus.OK);
    }

    @GetMapping("models/{stockId}")
    public ResponseEntity<List<ModelDTO>> getModelsByStockId(@PathVariable("stockId") Long stockId) {
        try {
            List<ModelDTO> modelsByStockId = stockServiceIMPL.findModelsByStockId(stockId);
            return new ResponseEntity<>(modelsByStockId, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error fetching models for stock ID: ", e);
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("imei/delete/{stockId}/{imeiId}")
    public ResponseEntity<String> deleteImei(@PathVariable Long stockId, @PathVariable Long imeiId) {
        try {
            stockServiceIMPL.deleteImeiByStockIdAndImeiId(stockId, imeiId);
            return ResponseEntity.ok("IMEI deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting IMEI");
        }
    }

}
