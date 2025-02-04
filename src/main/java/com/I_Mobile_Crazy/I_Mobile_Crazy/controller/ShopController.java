package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ShopDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Shop;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.User;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ShopService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl.ShopServiceIMPL;
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
@RequestMapping("api/shop")
@RequiredArgsConstructor
@CrossOrigin
public class ShopController {
    private final ShopService shopService;
    private final ShopServiceIMPL shopServiceIMPL;

    @PostMapping
    public ResponseEntity<StandardResponse> saveShop(@RequestBody @Valid ShopDTO shopDTO) {
        shopService.save(shopDTO);
        return new ResponseEntity<>(
                new StandardResponse(200, "Shop Saved", null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> updateShop(@PathVariable("id") Long id, @RequestBody ShopDTO updatedShopData) {
        try {
            updatedShopData.setShop_id(id);
            shopService.update(updatedShopData);

            return ResponseEntity.ok(new StandardResponse(200, "Shop updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Failed to update Shop", null));
        }
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> deleteShop(@PathVariable Long id) {
        shopService.delete(id);
        return new ResponseEntity<>(
                new StandardResponse(200, "Shop Deleted", null), HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<StandardResponse> getAllShops() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Shops Fetched", shopService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getShopById(@PathVariable Long id) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Shop Fetched", shopService.findById(id)), HttpStatus.OK);
    }

    @GetMapping("/contact/{contact_number}")
    public ResponseEntity<StandardResponse> getShopByContactNumber(@PathVariable String contact_number) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Customer Fetched", shopServiceIMPL.findByContactPhone(contact_number)), HttpStatus.OK);
    }

}
