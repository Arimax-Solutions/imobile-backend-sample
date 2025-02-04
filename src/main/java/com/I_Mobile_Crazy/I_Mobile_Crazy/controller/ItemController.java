package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.CustomerDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ItemDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.CustomerService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ItemService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl.ItemServiceIMPL;
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
@RequestMapping("api/items")
@RequiredArgsConstructor
@CrossOrigin
public class ItemController {
    private final ItemService itemService;
    private final ItemServiceIMPL itemServiceIMPL;

    @PostMapping
    public ResponseEntity<StandardResponse> saveItem(@RequestBody @Valid ItemDTO dto) {
        itemService.save(dto);
        return new ResponseEntity<>(
                new StandardResponse(200, "Item Saved", null), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> updateItem(@PathVariable("id") Long id, @RequestBody ItemDTO dto) {
        try {
            dto.setItem_id(id);
            itemService.update(dto);

            return ResponseEntity.ok(new StandardResponse(200, "Item updated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new StandardResponse(500, "Failed to update Item", null));
        }
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> deleteItem(@PathVariable Long id) {
        itemService.delete(id);
        return new ResponseEntity<>(
                new StandardResponse(200, "Item Deleted", null), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAllItems() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Item Fetched", itemService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse> getItemById(@PathVariable Long id) {
        return new ResponseEntity<>(
                new StandardResponse(200, "Item Fetched", itemService.findById(id)), HttpStatus.OK);
    }
    @GetMapping("/search/{name}")
    public ResponseEntity<StandardResponse> findItemByName(@PathVariable(name = "name") String name) {
        try {
            List<ItemDTO> itemDTOs = itemServiceIMPL.findByNameContaining(name);
            if (itemDTOs != null && !itemDTOs.isEmpty()) {
                return new ResponseEntity<>(
                        new StandardResponse(200, "Items Found", itemDTOs), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(
                        new StandardResponse(404, "No Items Found", null), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error finding items by name: ", e);
            return new ResponseEntity<>(
                    new StandardResponse(500, "Internal Server Error", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
