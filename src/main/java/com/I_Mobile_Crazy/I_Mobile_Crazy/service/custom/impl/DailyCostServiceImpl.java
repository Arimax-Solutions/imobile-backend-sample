package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.DailyCostDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ItemDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.DailyCost;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Item;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.DailyCostRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ItemRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.DailyCostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyCostServiceImpl implements DailyCostService {
    private final ModelMapper mapper;
    private final DailyCostRepository dailyCostRepository;
    @Override
    public void save(DailyCostDTO data) {
        try {
            dailyCostRepository.save(mapper.map(data, DailyCost.class));
        } catch (Exception e) {
            log.error("Error: ", e);
            throw e;
        }
    }

    @Override
    public void update(DailyCostDTO data) {

    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public DailyCostDTO findById(Long aLong) {
        return null;
    }

    @Override
    public List<DailyCostDTO> findAll() {
        try {
            List<DailyCost> allItems = dailyCostRepository.findAll();
            List<DailyCostDTO> activeItemDTOs = allItems.stream()
                    .map(item -> mapper.map(item, DailyCostDTO.class))
                    .collect(Collectors.toList());
            return activeItemDTOs;
        } catch (Exception e) {
            log.error("Error fetching costs: ", e);
            throw e;
        }
    }

    public List<DailyCostDTO> findTodaysCosts() {
        try {
            LocalDate today = LocalDate.now();
            List<DailyCost> todaysCosts = dailyCostRepository.findAll().stream()
                    .filter(item -> {
                        LocalDate itemDate = item.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        return itemDate.isEqual(today);
                    })
                    .collect(Collectors.toList());

            // Map to DTOs
            return todaysCosts.stream()
                    .map(item -> mapper.map(item, DailyCostDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching today's daily costs: ", e);
            throw e;
        }
    }

    public double getSumOfAllAmountsForToday() {
        try {
            LocalDate today = LocalDate.now();

            double totalAmount = dailyCostRepository.findAll().stream()
                    .map(cost -> {
                        LocalDate costDate = cost.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        return new AbstractMap.SimpleEntry<>(costDate, cost.getAmount());
                    })
                    .filter(entry -> entry.getKey().isEqual(today))
                    .mapToDouble(Map.Entry::getValue)
                    .sum();

            log.info("Total amount for today: {}", totalAmount);

            return totalAmount;
        } catch (Exception e) {
            log.error("Error calculating total cost for today", e);
            throw new RuntimeException("Error calculating total cost for today", e);
        }
    }




}
