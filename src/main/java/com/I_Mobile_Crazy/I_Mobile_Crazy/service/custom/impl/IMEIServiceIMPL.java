package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.*;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.*;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.CustomerRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.IMEIRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ModelRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.RetailOrderRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.IMEIService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
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
public class IMEIServiceIMPL implements IMEIService {
    @PersistenceContext
    private EntityManager entityManager;

    private final IMEIRepository imeiRepository;
    private final CustomerRepository customerRepository;
    private final RetailOrderRepository retailOrderRepository;
    private final ModelRepository modelRepository;
    private final ModelMapper mapper;

    @Override
    public void save(IMEIDTO data) {

    }

    @Override
    public void update(IMEIDTO data) {

    }

    @Override
    public void delete(Long imeiId) {
        System.out.println("Hit");
        try {
            // Find the IMEI by its ID
            IMEI imei = imeiRepository.findById(imeiId)
                    .orElseThrow(() -> new RuntimeException("IMEI not found with ID: " + imeiId));

            // Set the isDeleted flag to true
            imei.setDeleted(true);

            // Save the updated IMEI back to the repository
            imeiRepository.save(imei);

        } catch (Exception e) {
            log.error("Error deleting imei: ", e);
            throw e;
        }
    }


    @Override
    public IMEIDTO findById(Long aLong) {
        return null;
    }

    @Override
    public List<IMEIDTO> findAll() {
        try {
            List<IMEI> allImeis = imeiRepository.findAll();
            List<IMEIDTO> activeImeiDTOs = allImeis.stream()
                    .filter(imei -> "sale".equals(imei.getStatus()) && !imei.isDeleted())
                    .map(imei -> mapper.map(imei, IMEIDTO.class))
                    .collect(Collectors.toList());
            return activeImeiDTOs;
        } catch (Exception e) {
            log.error("Error fetching imei: ", e);
            throw e;
        }
    }


    @Override
    public List<IMEIDTO> findAllSold() {
        try {
            List<IMEI> allImeis = imeiRepository.findAll();
            List<IMEIDTO> soldImeiDTOs = allImeis.stream()
                    .filter(imei -> "sold".equals(imei.getStatus()) && !imei.isDeleted())
                    .map(imei -> mapper.map(imei, IMEIDTO.class))
                    .collect(Collectors.toList());
            return soldImeiDTOs;
        } catch (Exception e) {
            log.error("Error fetching sold imei: ", e);
            throw e;
        }
    }


    //for retail and wholesale order find phone by imei number
    public IMEIDTO findImeiByNumber(String imeiNumber) {
        try {
            IMEI imeiEntity = imeiRepository.findByImei(imeiNumber);

            if (imeiEntity != null && "sale".equals(imeiEntity.getStatus()) && !imeiEntity.isDeleted()) {
                // Fetch the model associated with the IMEI
                Models model = imeiEntity.getModel(); // Adjust based on your entity relationship

                // Create and populate ModelDTO
                ModelDTO modelDTO = new ModelDTO();
                if (model != null) {
                    modelDTO.setName(model.getName()); // Populate fields as needed
                } else {
                    modelDTO.setName("Unknown");
                }

                // Map the IMEI entity to DTO
                IMEIDTO imeiDTO = mapper.map(imeiEntity, IMEIDTO.class);
                imeiDTO.setModelId(modelDTO); // Set the ModelDTO in the IMEIDTO

                return imeiDTO;
            } else {
                log.warn("IMEI with number {} is not available for sale or not found", imeiNumber);
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching IMEI: ", e);
            throw e;
        }
    }



    //for return phones when sell in retail and wholesale order
    /*public IMEIDTO findImeiByNumberReturn(String imeiNumber) {
        try {
            IMEI imeiEntity = imeiRepository.findByImei(imeiNumber);

            if (imeiEntity != null && "sold".equals(imeiEntity.getStatus()) && !imeiEntity.isDeleted()) {
                Long modelId = imeiEntity.getModel().getId();

                Models model = modelRepository.findById(modelId).orElse(null);

                if (model != null) {
                    IMEIDTO imeiDTO = mapper.map(imeiEntity, IMEIDTO.class);

                    ModelDTO modelDTO = mapper.map(model, ModelDTO.class);

                    List<IMEIDTO> filteredImeiNumbers = modelDTO.getImeiNumbers()
                            .stream()
                            .filter(imei -> imeiNumber.equals(imei.getImei()))
                            .collect(Collectors.toList());
                    modelDTO.setImeiNumbers(filteredImeiNumbers);

                    imeiDTO.setModelId(modelDTO);

                    // Check for a retail order
                    RetailOrder retailOrder = imeiEntity.getRetailOrders()
                            .stream()
                            .filter(order -> !order.is_deleted())
                            .findFirst()
                            .orElse(null);

                    if (retailOrder != null) {
                        Customer customer = retailOrder.getCustomer();
                        if (customer != null) {
                            CustomerDTO customerDTO = mapper.map(customer, CustomerDTO.class);
                            imeiDTO.setCustomer(customerDTO);
                        } else {
                            log.warn("Customer not found for retail order id {}", retailOrder.getRetail_order_id());
                        }
                    } else {
                        // If no retail order, check for a wholesale order
                        WholeSaleOrder wholesaleOrder = imeiEntity.getWholesaleOrders()
                                .stream()
                                .filter(order -> !order.is_deleted())
                                .findFirst()
                                .orElse(null);

                        if (wholesaleOrder != null) {
                            Shop shop = wholesaleOrder.getShop();
                            if (shop != null) {
                                ShopDTO shopDTO = mapper.map(shop, ShopDTO.class);
                                imeiDTO.setShop(shopDTO);
                            } else {
                                log.warn("Shop not found for wholesale order id {}", wholesaleOrder.getWholesale_order_id());
                            }
                        } else {
                            log.warn("Neither retail nor wholesale order found for IMEI number {}", imeiNumber);
                        }
                    }

                    return imeiDTO;
                } else {
                    log.warn("Model not found for model ID {}", modelId);
                }
            } else {
                log.warn("IMEI with number {} is not sold or not found", imeiNumber);
            }

            return null;
        } catch (Exception e) {
            log.error("Error fetching IMEI: ", e);
            throw e;
        }
    }*/

    public IMEIDTO findImeiByNumberReturn(String imeiNumber) {
        try {
            // Find IMEI entity by imeiNumber
            IMEI imeiEntity = imeiRepository.findByImei(imeiNumber);

            if (imeiEntity != null && "sold".equals(imeiEntity.getStatus()) && !imeiEntity.isDeleted()) {
                // Get Model information
                Long modelId = imeiEntity.getModel().getId();
                Models model = modelRepository.findById(modelId).orElse(null);

                if (model != null) {
                    IMEIDTO imeiDTO = mapper.map(imeiEntity, IMEIDTO.class);

                    // Map the model to ModelDTO
                    ModelDTO modelDTO = mapper.map(model, ModelDTO.class);
                    List<IMEIDTO> filteredImeiNumbers = modelDTO.getImeiNumbers()
                            .stream()
                            .filter(imei -> imeiNumber.equals(imei.getImei()))
                            .collect(Collectors.toList());
                    modelDTO.setImeiNumbers(filteredImeiNumbers);
                    imeiDTO.setModelId(modelDTO);

                    // Check for a retail order and set customer details
                    RetailOrder retailOrder = imeiEntity.getRetailOrders()
                            .stream()
                            .filter(order -> !order.is_deleted())
                            .findFirst()
                            .orElse(null);

                    if (retailOrder != null) {
                        // Set retailOrderDate
                        imeiDTO.setRetailOrderDate(retailOrder.getDate()); // Set retail order date

                        Customer customer = retailOrder.getCustomer();
                        if (customer != null) {
                            CustomerDTO customerDTO = mapper.map(customer, CustomerDTO.class);
                            imeiDTO.setCustomer(customerDTO);  // Set customer details
                        } else {
                            log.warn("Customer not found for retail order id {}", retailOrder.getRetail_order_id());
                        }
                    } else {
                        // If no retail order, check for a wholesale order and set shop details
                        WholeSaleOrder wholesaleOrder = imeiEntity.getWholesaleOrders()
                                .stream()
                                .filter(order -> !order.is_deleted())
                                .findFirst()
                                .orElse(null);

                        if (wholesaleOrder != null) {
                            // Set wholesaleOrderDate
                            imeiDTO.setWholesaleOrderDate(wholesaleOrder.getDate()); // Add this line

                            Shop shop = wholesaleOrder.getShop();
                            if (shop != null) {
                                ShopDTO shopDTO = mapper.map(shop, ShopDTO.class);
                                imeiDTO.setShop(shopDTO);  // Set shop details
                            } else {
                                log.warn("Shop not found for wholesale order id {}", wholesaleOrder.getWholesale_order_id());
                            }
                        } else {
                            log.warn("Neither retail nor wholesale order found for IMEI number {}", imeiNumber);
                        }
                    }

                    return imeiDTO;
                } else {
                    log.warn("Model not found for model ID {}", modelId);
                }
            } else {
                log.warn("IMEI with number {} is not sold or not found", imeiNumber);
            }

            return null;
        } catch (Exception e) {
            log.error("Error fetching IMEI: ", e);
            throw e;
        }
    }




    //create method for get status will be sale and get that imei count
    public Map<String, Long> getCountOfSaleImeisWithModelNameAndStorage() {
        try {
            List<IMEI> allImeis = imeiRepository.findAll();
            List<Models> allModels = modelRepository.findAll();

            // Create a map to map model IDs to their corresponding model names.
            Map<Long, String> modelIdToNameMap = allModels.stream()
                    .collect(Collectors.toMap(Models::getId, Models::getName));

            // Group by model name and storage, and count the occurrences.
            return allImeis.stream()
                    .filter(imei -> "sale".equals(imei.getStatus()) && !imei.isDeleted())
                    .collect(Collectors.groupingBy(
                            imei -> modelIdToNameMap.get(imei.getModel().getId()) + " " + imei.getStorage(),
                            Collectors.counting()
                    ));
        } catch (Exception e) {
            log.error("Error fetching count of imei with status 'sale': ", e);
            throw e;
        }
    }



    public long countSoldImeisThisMonth() {
        try {
            LocalDate now = LocalDate.now();
            LocalDate startOfMonthLocal = now.withDayOfMonth(1);
            LocalDate endOfMonthLocal = now.withDayOfMonth(now.lengthOfMonth());

            Date startOfMonth = Date.from(startOfMonthLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endOfMonth = Date.from(endOfMonthLocal.atStartOfDay(ZoneId.systemDefault()).toInstant());

            System.out.println(startOfMonth);
            System.out.println(endOfMonth);

            List<RetailOrder> ordersThisMonth = retailOrderRepository.findAll().stream()
                    .filter(order -> {
                        Date orderDate = order.getDate();
                        return orderDate != null && !orderDate.before(startOfMonth) && !orderDate.after(endOfMonth);
                    })
                    .collect(Collectors.toList());

            long soldCount = ordersThisMonth.stream()
                    .flatMap(order -> order.getImeis().stream())
                    .filter(imei -> "sold".equals(imei.getStatus()) && !imei.isDeleted())
                    .count();

            return soldCount;
        } catch (Exception e) {
            log.error("Error fetching sold IMEI count for this month: ", e);
            throw e;
        }
    }
}
