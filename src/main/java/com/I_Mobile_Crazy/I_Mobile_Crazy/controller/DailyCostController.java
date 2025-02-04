package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.DailyCostDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ItemDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.DailyCostService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ItemService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl.DailyCostServiceImpl;
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
@RequestMapping("api/dailyCost")
@RequiredArgsConstructor
@CrossOrigin
public class DailyCostController {
    private final DailyCostService dailyCostService;
    private final DailyCostServiceImpl dailyCostServiceImpl;

    @PostMapping
    public ResponseEntity<StandardResponse> saveDailyCost(@RequestBody @Valid DailyCostDTO dto) {
        dailyCostService.save(dto);
        return new ResponseEntity<>(
                new StandardResponse(200, "Daily cost Saved", null), HttpStatus.CREATED);
    }

    @GetMapping("/monthly/cost")
    public ResponseEntity<StandardResponse> getAllDCostAmountInMonth() {
        double amount = dailyCostServiceImpl.getSumOfAllAmountsForToday();
        log.info("Fetched amount: {}", amount);
        return new ResponseEntity<>(
                new StandardResponse(200, "Daily cost Fetched", amount), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<StandardResponse> getAllDailyCosts() {
        return new ResponseEntity<>(
                new StandardResponse(200, "Daily cost Fetched", dailyCostService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/today")
    public ResponseEntity<StandardResponse> getTodaysDailyCosts() {
        try {
            List<DailyCostDTO> todaysCosts = dailyCostServiceImpl.findTodaysCosts();
            StandardResponse response = new StandardResponse(200, "Today's daily costs fetched", todaysCosts);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching today's daily costs: ", e);
            StandardResponse errorResponse = new StandardResponse(500, "Error fetching today's daily costs", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
