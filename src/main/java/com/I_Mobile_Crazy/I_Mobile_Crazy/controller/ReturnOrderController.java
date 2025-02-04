package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.CustomerDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ReturnOrderDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ReturnOrderService;
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
@RequestMapping("api/returnOrder")
@RequiredArgsConstructor
@CrossOrigin
public class ReturnOrderController {
    private final ReturnOrderService returnOrderService;

    @PostMapping()
    public ResponseEntity<StandardResponse> saveOrder(@Validated @RequestBody ReturnOrderDTO dto) {
        returnOrderService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponse(200, "Order Saved", null));
    }

    @GetMapping()
    public ResponseEntity<StandardResponse> getAllReturnOrders() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Fetched", returnOrderService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getReturnOrderById(@PathVariable String id) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Fetched", returnOrderService.findById(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> updateReturnOrder(@PathVariable("id") String id, @RequestBody ReturnOrderDTO dto) {
        try {
            dto.setReturn_order_id(id);
            returnOrderService.update(dto);

            return ResponseEntity.ok(new StandardResponse(200, "Return order updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Failed to update Return order", null));
        }
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> deleteOrder(@PathVariable String id) {
        returnOrderService.delete(id);
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Deleted", null), HttpStatus.OK);
    }
}
