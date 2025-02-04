package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.RetailOrderDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.WholesaleOrderDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.RetailOrderService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.WholesaleOrderService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl.WholesaleOrderServiceIMPL;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.StandardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@RestController
@RequestMapping("api/wholesaleOrder")
@RequiredArgsConstructor
@CrossOrigin
public class WholesaleOrderController {
    private final WholesaleOrderService wholesaleOrderService;
    private final WholesaleOrderServiceIMPL wholesaleOrderServiceIMPL;

    @PostMapping()
    public ResponseEntity<StandardResponse> saveOrder(@Validated @RequestBody WholesaleOrderDTO dto) {
        wholesaleOrderService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponse(200, "Order Saved", null));
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> updateWholeSaleOrder(@PathVariable("id") String id, @RequestBody WholesaleOrderDTO dto) {
        try {
            dto.setWholesale_order_id(id);
            wholesaleOrderService.update(dto);

            return ResponseEntity.ok(new StandardResponse(200, "Wholesale order updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Failed to update Wholesale order", null));

        }
    }

    @GetMapping()
    public ResponseEntity<StandardResponse> getAllWholesaleOrders() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Fetched", wholesaleOrderServiceIMPL.findAllOrdersInLastTwoMonths()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getWholesaleOrderById(@PathVariable String id) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Fetched", wholesaleOrderService.findById(id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> deleteOrder(@PathVariable String id) {
        wholesaleOrderService.delete(id);
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Deleted", null), HttpStatus.OK);
    }

    @GetMapping("/count-this-month")
    public ResponseEntity<Long> getWholesaleOrderCountForThisMonth() {
        long count = wholesaleOrderServiceIMPL.getWholesaleOrderCountForThisMonth();
        return ResponseEntity.ok(count);
    }
}
