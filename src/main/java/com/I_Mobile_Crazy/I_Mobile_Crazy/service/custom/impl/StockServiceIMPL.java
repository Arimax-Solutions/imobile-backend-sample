package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.IMEIDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ModelDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ShopDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.StockDTO;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.IMEI;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Models;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Shop;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Stock;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.IMEIRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ModelRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.StockRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.StockService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceIMPL implements StockService {
    private final ModelMapper mapper;
    private final StockRepository stockRepository;
    private final ModelRepository modelRepository;
    private final IMEIRepository imeiRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void save(StockDTO stockDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(formatter);
        // Save the stock first
        Stock stock = new Stock();
        stock.setName(stockDTO.getName());
        stock.setDescription(stockDTO.getDescription());
        stock.setQty(stockDTO.getQty());
        stock = stockRepository.save(stock);

        // Iterate through each model
        for (ModelDTO modelDTO : stockDTO.getModels()) {
            // Save or update the model
            Models model = new Models();
            model.setName(modelDTO.getName());
            model.setStock(stock);
            model.setStockAddedDate(formattedDate);

            // Before saving the model, check if any IMEI already exists in the modelDTO
            for (IMEIDTO imeiDTO : modelDTO.getImeiNumbers()) {
                IMEI existingImei = imeiRepository.findByImei(imeiDTO.getImei());
                if (existingImei != null) {
                    // If IMEI exists, throw an exception and stop processing
                    throw new RuntimeException("IMEI already exists: " + imeiDTO.getImei());
                }
            }

            // If no existing IMEI is found, save the model
            modelRepository.save(model);

            // Now save the IMEIs
            for (IMEIDTO imeiDTO : modelDTO.getImeiNumbers()) {
                IMEI imei = new IMEI();
                imei.setImei(imeiDTO.getImei());
                imei.setStorage(imeiDTO.getStorage());
                imei.setColour(imeiDTO.getColour());
                imei.setModel(model);
                imei.setBatteryHealth(imeiDTO.getBatteryHealth());
                imei.setIOSVersion(imeiDTO.getIOSVersion());

                // Save the IMEI if it doesn't exist
                imeiRepository.save(imei);
            }
        }
    }

    @Override
    public void update(StockDTO data) {

    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public StockDTO findById(Long stockId) {
        return null;
    }

    @Override
    public List<StockDTO> findAll() {
        try {
            List<Stock> allStocks = stockRepository.findAll();

            // Define the desired date-time format without seconds
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            List<StockDTO> stockDTOs = allStocks.stream()
                    .map(stock -> {
                        StockDTO stockDTO = mapper.map(stock, StockDTO.class);

                        // Check if the stock has models
                        if (stock.getModels() != null) {
                            stock.getModels().forEach(model -> {
                                if (model.getStockAddedDate() != null) {
                                    // Format the LocalDateTime to the desired string format
                                    String formattedDate = model.getStockAddedDate().format(String.valueOf(formatter));

                                    // Set the formatted date to a suitable field in your model or DTO
                                    model.setStockAddedDate(formattedDate); // Ensure this field exists in your DTO
                                }
                            });
                        }

                        stockDTO.setModels(null); // Setting models to null as per your code
                        return stockDTO;
                    })
                    .collect(Collectors.toList());
            return stockDTOs;
        } catch (Exception e) {
            log.error("Error fetching stocks: ", e);
            throw e;
        }
    }

    /*@Transactional
    public void updateStock(Long stockId, StockDTO updatedStockDTO) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        stock.setName(updatedStockDTO.getName());
        stock.setDescription(updatedStockDTO.getDescription());
        stock.setQty(updatedStockDTO.getQty());

        List<Models> existingModels = modelRepository.findByStockId(stockId);

        Set<Long> existingModelIds = existingModels.stream()
                .map(Models::getId)
                .collect(Collectors.toSet());

        Set<Long> incomingModelIds = updatedStockDTO.getModels().stream()
                .map(ModelDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (Models existingModel : existingModels) {
            if (!incomingModelIds.contains(existingModel.getId())) {
                modelRepository.delete(existingModel);
            }
        }

        for (ModelDTO modelDTO : updatedStockDTO.getModels()) {
            Models model;
            if (modelDTO.getId() != null) {
                // Find the existing model if it exists
                model = modelRepository.findById(modelDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Model not found"));
            } else {
                model = new Models();
                model.setStock(stock);
            }
            model.setName(modelDTO.getName());
            model.setStockAddedDate(String.valueOf(LocalDateTime.now()));

            for (IMEIDTO imeiDTO : modelDTO.getImeiNumbers()) {
                IMEI existingImei = imeiRepository.findByImei(imeiDTO.getImei());
                if (existingImei != null) {
                    // If IMEI exists, throw an exception and stop execution
                    throw new RuntimeException("IMEI already exists: " + imeiDTO.getImei());
                }
            }

            modelRepository.save(model);

            for (IMEIDTO imeiDTO : modelDTO.getImeiNumbers()) {
                IMEI imei = new IMEI();
                imei.setModel(model);
                imei.setImei(imeiDTO.getImei());
                imei.setStorage(imeiDTO.getStorage());
                imei.setColour(imeiDTO.getColour());
                imei.setIOSVersion(imeiDTO.getIOSVersion());
                imei.setBatteryHealth(imeiDTO.getBatteryHealth());

                imeiRepository.save(imei);
            }
        }
    }*/
    @Transactional
    public void updateStock(Long stockId, StockDTO updatedStockDTO) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        String formattedDate = today.format(formatter);

        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        stock.setName(updatedStockDTO.getName());
        stock.setDescription(updatedStockDTO.getDescription());
        stock.setQty(updatedStockDTO.getQty());

        List<Models> existingModels = modelRepository.findByStockId(stockId);

        Set<Long> existingModelIds = existingModels.stream()
                .map(Models::getId)
                .collect(Collectors.toSet());

        Set<Long> incomingModelIds = updatedStockDTO.getModels().stream()
                .map(ModelDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (Models existingModel : existingModels) {
            if (!incomingModelIds.contains(existingModel.getId())) {
                modelRepository.delete(existingModel);
            }
        }

        for (ModelDTO modelDTO : updatedStockDTO.getModels()) {
            Models model;
            if (modelDTO.getId() != null) {
                // Find the existing model if it exists
                model = modelRepository.findById(modelDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Model not found"));
            } else {
                model = new Models();
                model.setStock(stock);
            }
            model.setName(modelDTO.getName());
            model.setStockAddedDate(formattedDate);

            for (IMEIDTO imeiDTO : modelDTO.getImeiNumbers()) {
                IMEI existingImei = imeiRepository.findByImei(imeiDTO.getImei());
                if (existingImei != null) {
                    // If IMEI exists, throw an exception and stop execution
                    throw new RuntimeException("IMEI already exists: " + imeiDTO.getImei());
                }
            }

            modelRepository.save(model);

            for (IMEIDTO imeiDTO : modelDTO.getImeiNumbers()) {
                IMEI imei = new IMEI();
                imei.setModel(model);
                imei.setImei(imeiDTO.getImei());
                imei.setStorage(imeiDTO.getStorage());
                imei.setColour(imeiDTO.getColour());
                imei.setIOSVersion(imeiDTO.getIOSVersion());
                imei.setBatteryHealth(imeiDTO.getBatteryHealth());

                imeiRepository.save(imei);
            }
        }
    }

    public List<ModelDTO> findModelsByStockId(Long stockId) {
        try {
            // Fetch the stock by ID
            Stock stock = stockRepository.findById(stockId)
                    .orElseThrow(() -> new RuntimeException("Stock not found"));

            // Map the models to ModelDTO and filter only IMEIs where isDeleted is false
            return stock.getModels().stream()
                    .map(model -> {
                        // Filter IMEIs where isDeleted is false
                        List<IMEI> nonDeletedImeiNumbers = model.getImeiNumbers().stream()
                                .filter(imei -> !imei.isDeleted())
                                .collect(Collectors.toList());

                        // Set the filtered IMEIs back into the model
                        model.setImeiNumbers(nonDeletedImeiNumbers);

                        // Map the model to ModelDTO
                        return mapper.map(model, ModelDTO.class);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching models for stock ID: ", e);
            throw new RuntimeException("Error fetching models for stock ID", e);
        }
    }

    public void deleteImeiByStockIdAndImeiId(Long stockId, Long imeiId) {
        try {
            // Step 1: Retrieve the stock by ID
            Stock stock = stockRepository.findById(stockId)
                    .orElseThrow(() -> new RuntimeException("Stock not found with ID: " + stockId));

            // Step 2: Find the model containing the IMEI
            Optional<Models> modelOpt = stock.getModels().stream()
                    .filter(model -> model.getImeiNumbers().stream()
                            .anyMatch(imei -> imei.getId().equals(imeiId)))
                    .findFirst();

            if (!modelOpt.isPresent()) {
                throw new RuntimeException("Model not found for IMEI ID: " + imeiId);
            }

            Models model = modelOpt.get();

            // Step 3: Find and delete the IMEI from the model
            IMEI imeiToDelete = model.getImeiNumbers().stream()
                    .filter(imei -> imei.getId().equals(imeiId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("IMEI not found with ID: " + imeiId));

            // Mark the IMEI as deleted (or remove it if you prefer)
            imeiToDelete.setDeleted(true);

            // Step 4: Remove the IMEI from the model's list of IMEIs
            model.getImeiNumbers().remove(imeiToDelete);

            // If the model has no more IMEIs, delete the model as well
            if (model.getImeiNumbers().isEmpty()) {
                // Optionally, you can delete the model from the stock repository
                stock.getModels().remove(model);
                modelRepository.delete(model);  // Deleting the model
            } else {
                // If the model still has IMEIs, just save the model
                modelRepository.save(model);
            }

            // Step 5: Save the updated stock if necessary
            stockRepository.save(stock);

            log.info("IMEI with ID: {} successfully deleted from stock with ID: {}", imeiId, stockId);

        } catch (Exception e) {
            log.error("Error deleting IMEI by stockId and imeiId: ", e);
            throw new RuntimeException("Error deleting IMEI by stockId and imeiId", e);
        }
    }


}
