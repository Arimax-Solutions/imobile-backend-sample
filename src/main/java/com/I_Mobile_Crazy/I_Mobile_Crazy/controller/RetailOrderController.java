package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.RetailOrderDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ReturnOrderDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.WholesaleOrderDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.RetailOrderService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl.RetailOrderServiceIMPL;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@RestController
@RequestMapping("api/retailOrder")
@RequiredArgsConstructor
@CrossOrigin
public class RetailOrderController {
    private final RetailOrderService retailOrderService;
    private final RetailOrderServiceIMPL retailOrderServiceIMPL;

    @PostMapping()
    public ResponseEntity<StandardResponse> saveOrder(@Validated @RequestBody RetailOrderDTO dto) {
        System.out.println(dto);
        retailOrderService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new StandardResponse(200, "Order Saved", null));

    }

    /*@GetMapping()
    public ResponseEntity<StandardResponse> getAllRetailOrders() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Fetched", retailOrderService.findAll()), HttpStatus.OK);
    }*/

    @GetMapping()
    public ResponseEntity<StandardResponse> getAllRetailOrdersInTwoMonth() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Fetched", retailOrderServiceIMPL.findAllInTwoMonths()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getRetailOrderById(@PathVariable String id) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Fetched", retailOrderService.findById(id)), HttpStatus.OK);
    }

    /*@PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> updateRetailOrder(@PathVariable("id") String id, @RequestBody RetailOrderDTO dto) {
        try {
            dto.setRetail_order_id(id);
            retailOrderService.update(dto);

            return ResponseEntity.ok(new StandardResponse(200, "Retail order updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Failed to update Retail order", null));

        }
    }*/

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> deleteOrder(@PathVariable String id) {
        retailOrderService.delete(id);
        return new ResponseEntity<>(
                new StandardResponse(200, "Order Deleted", null), HttpStatus.OK);
    }

    @GetMapping("/today")
    public List<RetailOrderDTO> getTodayRetailOrders() {
        return retailOrderServiceIMPL.findAllTodayRetailOrders();
    }
    @GetMapping("/wholesale/today")
    public List<WholesaleOrderDTO> getTodayWholesaleOrders() {
        return retailOrderServiceIMPL.findAllTodayWholesaleOrders();
    }

    @GetMapping("/return/today")
    public List<ReturnOrderDTO> getTodayReturnOrderOrders() {
        return retailOrderServiceIMPL.findAllTodayReturnOrders();
    }

    @GetMapping("/monthly")
    public double getMonthlyIncome() {
        return retailOrderServiceIMPL.calculateMonthlyAllIncome();
    }

    @GetMapping("/order/count")
    public double getOrderCount() {
        return retailOrderServiceIMPL.getRetailOrderCountForThisMonth();
    }

}
