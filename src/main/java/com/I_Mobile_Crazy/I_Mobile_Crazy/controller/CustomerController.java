package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.CustomerDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.CustomerService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl.CustomerServiceIMPL;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.StandardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@RestController
@RequestMapping("api/customer")
@RequiredArgsConstructor
@CrossOrigin
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerServiceIMPL customerServiceIMPL;

    @PostMapping
    public ResponseEntity<StandardResponse> saveCustomer(@RequestBody @Valid CustomerDTO dto) {
        customerService.save(dto);
        return new ResponseEntity<>(
                new StandardResponse(200, "Customer Saved", null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> updateCustomer(@PathVariable("id") Long id, @RequestBody CustomerDTO dto) {
        try {
            dto.setCustomer_id(id);
            customerService.update(dto);

            return ResponseEntity.ok(new StandardResponse(200, "Customer updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Failed to update Customer", null));
        }
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> deleteCustomer(@PathVariable Long id) {
        customerService.delete(id);
        return new ResponseEntity<>(
                new StandardResponse(200, "Customer Deleted", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAllCustomers() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Customer Fetched", customerService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getCustomerById(@PathVariable Long id) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Customer Fetched", customerService.findById(id)), HttpStatus.OK);
    }

    @GetMapping("/contact/{contact_number}")
    public ResponseEntity<StandardResponse> getCustomerByContactNumber(@PathVariable String contact_number) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Customer Fetched", customerServiceIMPL.findByContact_phone(contact_number)), HttpStatus.OK);
    }
}
