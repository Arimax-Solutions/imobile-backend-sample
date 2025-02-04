package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ReturnPhonesDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ShopDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ReturnPhonesService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl.ReturnPhonesServiceIMPL;
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
@RequestMapping("api/return/phone")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReturnPhoneController {
    private final ReturnPhonesService returnPhonesService;
    private final ReturnPhonesServiceIMPL returnPhonesServiceIMPL;

    @PostMapping
    public ResponseEntity<StandardResponse> save(@RequestBody @Valid ReturnPhonesDTO returnPhonesDTO) {
        returnPhonesService.save(returnPhonesDTO);
        return new ResponseEntity<>(
                new StandardResponse(201, "Return Phone Saved", null), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAllReturnPhones() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Phones Fetched", returnPhonesServiceIMPL.findAll()), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> updateReturnPhone(@PathVariable("id") Long id, @RequestBody ReturnPhonesDTO updatedShopData) {
        try {
            updatedShopData.setShop_id(id);
            returnPhonesService.update(updatedShopData);

            return ResponseEntity.ok(new StandardResponse(200, "Phone updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Failed to update Phone", null));
        }
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> deleteReturnPhone(@PathVariable Long id) {
        returnPhonesService.delete(id);
        return new ResponseEntity<>(
                new StandardResponse(200, "Phone Deleted", null), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getReturnPhoneById(@PathVariable Long id) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Phone Fetched", returnPhonesService.findById(id)), HttpStatus.OK);
    }

    @GetMapping("/imei/{imei}")
    public ResponseEntity<ReturnPhonesDTO> getPhoneByImei(@PathVariable String imei) {
        System.out.println("HIT");
        ReturnPhonesDTO returnPhonesDTO = returnPhonesServiceIMPL.findByImei(imei);
        if (returnPhonesDTO != null) {
            return ResponseEntity.ok(returnPhonesDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
