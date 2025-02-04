package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.CustomerDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ReturnItemDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.CustomerService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ReturnItemService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@RestController
@RequestMapping("api/returnItem")
@RequiredArgsConstructor
@CrossOrigin
public class ReturnItemController {
    private final ReturnItemService returnItemService;

    @PostMapping
    public ResponseEntity<StandardResponse> saveCustomer(@RequestBody @Valid ReturnItemDTO dto) {
        returnItemService.save(dto);
        return new ResponseEntity<>(
                new StandardResponse(200, "Item Saved", null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> updateCustomer(@PathVariable("id") Long id, @RequestBody ReturnItemDTO dto) {
        try {
            dto.setReturn_phone_id(id);
            returnItemService.update(dto);

            return ResponseEntity.ok(new StandardResponse(200, "Item updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Failed to update Item", null));
        }
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> deleteCustomer(@PathVariable Long id) {
        returnItemService.delete(id);
        return new ResponseEntity<>(
                new StandardResponse(200, "Item Deleted", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAllCustomers() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Item Fetched", returnItemService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getCustomerById(@PathVariable Long id) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Item Fetched", returnItemService.findById(id)), HttpStatus.OK);
    }
}
