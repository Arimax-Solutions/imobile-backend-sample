package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.CustomerDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.IMEIDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ReturnOrderDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.*;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.*;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ReturnOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class ReturnOrderServiceIMPL implements ReturnOrderService {
    @Autowired
    private final ModelMapper mapper;
    @Autowired
    private final ReturnOrderRepository returnOrderRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ReturnPhonesRepository returnPhonesRepository;

    @Override
    public void save(ReturnOrderDTO dto) {
        Optional<Shop> shopOptional = shopRepository.findById(dto.getShop().getShop_id());
        Shop shop = shopOptional.orElseThrow(() -> new IllegalArgumentException("Shop not found"));

        String shopName = shop.getShop_name();
        String shopContactNumber = shop.getContact_number();

        List<String> imeiNumbers = dto.getImeis().stream()
                .map(IMEIDTO::getImei)
                .collect(Collectors.toList());

        List<ReturnPhones> existingReturnPhones = returnPhonesRepository.findByImeiIn(imeiNumbers);
        if (existingReturnPhones == null || existingReturnPhones.isEmpty()) {
            throw new IllegalArgumentException("No IMEIs found in the returnPhonesRepository with the given numbers");
        }

        for (ReturnPhones returnPhones : existingReturnPhones) {
            returnPhones.setStatus("sold");
            returnPhonesRepository.save(returnPhones);
        }

        ReturnOrder returnOrder = new ReturnOrder();
        returnOrder.setReturn_order_id(dto.getReturn_order_id());
        returnOrder.setDiscount(dto.getDiscount());
        returnOrder.setActual_price(dto.getActual_price());
        returnOrder.setTotal_amount(dto.getTotal_amount());
        returnOrder.setDate(dto.getDate());
        returnOrder.setShop(shop);
        returnOrder.setReturnPhones(existingReturnPhones);

        returnOrderRepository.save(returnOrder);
    }


    @Override
    public void update(ReturnOrderDTO data) {
        try {
            Optional<ReturnOrder> returnOrderOptional = returnOrderRepository.findById(data.getReturn_order_id());
            if (returnOrderOptional.isPresent()) {
                ReturnOrder returnOrder = returnOrderOptional.get();

                returnOrder.setDiscount(data.getDiscount());
                returnOrder.setActual_price(data.getActual_price());
                returnOrder.setTotal_amount(data.getTotal_amount());
                returnOrder.setDate(data.getDate());
                returnOrder.set_deleted(data.is_deleted());

                returnOrderRepository.save(returnOrder);
            } else {
                throw new EntityNotFoundException("Return order not found for ID: " + data.getReturn_order_id());
            }
        } catch (Exception e) {
            log.error("Error updating return order: ", e);
            throw e;
        }
    }


    @Override
    public void delete(String id) {
        try {
            Optional<ReturnOrder> optional = returnOrderRepository.findById(id);
            if (!optional.isPresent()) {
                throw new RuntimeException("Order not found");
            }
            ReturnOrder order = optional.get();
            order.set_deleted(true);
            returnOrderRepository.save(order);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public ReturnOrderDTO findById(String id) {
        try {
            Optional<ReturnOrder> returnOrderOptional = returnOrderRepository.findById(id);
            if (returnOrderOptional.isPresent()) {
                ReturnOrder returnOrder = returnOrderOptional.get();
                ReturnOrderDTO dto = mapper.map(returnOrder, ReturnOrderDTO.class);
                List<IMEIDTO> imeiDTOs = returnOrder.getReturnPhones().stream()
                        .map(returnPhone -> mapper.map(returnPhone, IMEIDTO.class))
                        .collect(Collectors.toList());
                dto.setImeis(imeiDTOs);
                return dto;
            } else {
                throw new EntityNotFoundException("Return order not found for ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error fetching return order by ID: ", e);
            throw e;
        }
    }


    @Override
    public List<ReturnOrderDTO> findAll() {

        try {
            List<ReturnOrder> allReturnOrders = returnOrderRepository.findAll();

            // Get the current date and calculate the last two months
            LocalDate now = LocalDate.now();
            LocalDate twoMonthsAgo = now.minusMonths(2);

            List<ReturnOrderDTO> activeReturnOrderDTOs = allReturnOrders.stream()
                    .filter(returnOrder -> !returnOrder.is_deleted()) // Filter out deleted orders
                    .filter(returnOrder -> {
                        // Get the order date and convert it to LocalDate
                        Date orderDate = returnOrder.getDate(); // Assuming getDate() returns the date of the order
                        LocalDate localOrderDate = orderDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        // Check if the order date is within the last two months
                        return !localOrderDate.isBefore(twoMonthsAgo) && !localOrderDate.isAfter(now);
                    })
                    .map(returnOrder -> {
                        ReturnOrderDTO dto = mapper.map(returnOrder, ReturnOrderDTO.class);
                        List<IMEIDTO> imeiDTOs = returnOrder.getReturnPhones().stream()
                                .map(returnPhone -> mapper.map(returnPhone, IMEIDTO.class))
                                .collect(Collectors.toList());
                        dto.setImeis(imeiDTOs);
                        return dto;
                    })
                    .collect(Collectors.toList());

            return activeReturnOrderDTOs;
        } catch (Exception e) {
            log.error("Error fetching return orders: ", e);
            throw e;
        }
    }


    public List<ReturnOrderDTO> findAllOrdersInLastTwoMonths() {
       try {
            List<ReturnOrder> allReturnOrders = returnOrderRepository.findAll();
            List<ReturnOrderDTO> activeReturnOrderDTOs = allReturnOrders.stream()
                    .filter(returnOrder -> !returnOrder.is_deleted())
                    .map(returnOrder -> {
                        ReturnOrderDTO dto = mapper.map(returnOrder, ReturnOrderDTO.class);
                        List<IMEIDTO> imeiDTOs = returnOrder.getReturnPhones().stream()
                                .map(returnPhone -> mapper.map(returnPhone, IMEIDTO.class))
                                .collect(Collectors.toList());
                        dto.setImeis(imeiDTOs);
                        return dto;
                    })
                    .collect(Collectors.toList());
            return activeReturnOrderDTOs;
        } catch (Exception e) {
            log.error("Error fetching orders: ", e);
            throw e;
        }
    }


}
