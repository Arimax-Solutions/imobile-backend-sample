package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.IMEIDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.IMEI;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.IMEIService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl.IMEIServiceIMPL;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.StandardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@RestController
@RequestMapping("api/imei")
@RequiredArgsConstructor
@CrossOrigin
public class IMEIController {
    private final IMEIService imeiService;
    private final IMEIServiceIMPL imeiServiceIMPL;

    @GetMapping("/sale")
    public ResponseEntity<StandardResponse> getAllImeisSale() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Imei Fetched", imeiService.findAll()), HttpStatus.OK);
    }
    @GetMapping("/sold")
    public ResponseEntity<StandardResponse> getAllImeisSold() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Imei Fetched", imeiService.findAllSold()), HttpStatus.OK);
    }

    @GetMapping("/check-sale/{imeiNumber}")
    public ResponseEntity<IMEIDTO> checkImeiStatus(@PathVariable String imeiNumber) {
        System.out.println(imeiNumber);
        try {
            IMEIDTO imeiDTO = imeiServiceIMPL.findImeiByNumber(imeiNumber);
            if (imeiDTO != null) {
                return ResponseEntity.ok(imeiDTO);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/return/{imeiNumber}")
    public IMEIDTO getImeiByNumber(@PathVariable String imeiNumber) {
        return imeiServiceIMPL.findImeiByNumberReturn(imeiNumber);
    }

    @GetMapping("/count/sale")
    public ResponseEntity<Map<String, Long>> getCountOfSaleImeisWithModelName() {
        try {
            Map<String, Long> countByModel = imeiServiceIMPL.getCountOfSaleImeisWithModelNameAndStorage();
            return ResponseEntity.ok(countByModel);
        } catch (Exception e) {
            log.error("Error fetching count of imei with status 'sale': ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/sold-count-this-month")
    public long getSoldImeisCountThisMonth() {
        return imeiServiceIMPL.countSoldImeisThisMonth();
    }

    @DeleteMapping("/{imeiId}")
    public ResponseEntity<String> deleteIMEI(@PathVariable Long imeiId) {
        try {
            imeiService.delete(imeiId); // Call the delete method from the service layer
            return ResponseEntity.ok("IMEI deleted successfully.");
        } catch (Exception e) {
            log.error("Error deleting IMEI with ID: " + imeiId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting IMEI: " + e.getMessage());
        }
    }
}
