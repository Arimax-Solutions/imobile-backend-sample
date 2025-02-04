package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.IMEIDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ModelDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.WholesaleOrderDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.*;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.*;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.WholesaleOrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
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
public class WholesaleOrderServiceIMPL implements WholesaleOrderService {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private final ModelMapper mapper;
    @Autowired
    private final WholesaleOrderRepository wholesaleOrderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private IMEIRepository imeiRepository;
    @Autowired
    private ReturnPhonesRepository returnPhonesRepository;


    public List<ReturnPhones> findByShopId(Long shopId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReturnPhones> criteriaQuery = criteriaBuilder.createQuery(ReturnPhones.class);
        Root<ReturnPhones> returnPhonesRoot = criteriaQuery.from(ReturnPhones.class);

        // Access the customer field and its id
        Join<ReturnPhones, Shop> customerJoin = returnPhonesRoot.join("shop");
        Predicate customerPredicate = criteriaBuilder.equal(customerJoin.get("id"), shopId);
        criteriaQuery.where(customerPredicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }


    @Override
    @Transactional
    public void save(WholesaleOrderDTO dto) {
        System.out.println(dto.getImeis());
        Optional<Shop> shopOptional = shopRepository.findById(dto.getShop().getShop_id());
        Shop shop = shopOptional.orElseThrow(() -> new IllegalArgumentException("shop not found"));


        List<ReturnPhones> returnPhonesList = this.findByShopId(shop.getShop_id());
        if (returnPhonesList != null && !returnPhonesList.isEmpty()) {
            returnPhonesList.forEach(returnPhone -> {
                returnPhone.setOutStanding(0.0);
                returnPhonesRepository.save(returnPhone);
            });
        }

        List<Item> items = new ArrayList<>();
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            items = dto.getItems().stream()
                    .map(itemDTO -> {
                        Optional<Item> itemOptional = itemRepository.findById(itemDTO.getItem_id());
                        Item item = itemOptional.orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemDTO.getItem_id()));
                        if (item.getQty() < itemDTO.getQty()) {
                            throw new IllegalArgumentException("Insufficient stock for item: " + item.getName());
                        }
                        item.setQty(item.getQty() - itemDTO.getQty());
                        itemRepository.save(item);
                        return item;
                    })
                    .collect(Collectors.toList());
        }

        List<IMEI> imeisToUpdate = new ArrayList<>();
        if (dto.getImeis() != null && !dto.getImeis().isEmpty()) {
            List<String> imeiNumbers = dto.getImeis().stream()
                    .map(IMEIDTO::getImei)
                    .collect(Collectors.toList());

            imeisToUpdate = imeiRepository.findByImeiIn(imeiNumbers);
            if (imeiNumbers.size() != imeisToUpdate.size()) {
                throw new IllegalArgumentException("One or more IMEIs not found with the given numbers");
            }

            imeisToUpdate.forEach(imei -> imei.setStatus("sold"));
            imeisToUpdate.forEach(imei -> imei.setWarranty(dto.getImeis().get(0).getWarranty()));
            imeisToUpdate.forEach(imei -> imei.setPrice(dto.getTotal_amount()));
            imeiRepository.saveAll(imeisToUpdate);
        }

        WholeSaleOrder wholeSaleOrder = new WholeSaleOrder();
        wholeSaleOrder.setWholesale_order_id(dto.getWholesale_order_id());
        wholeSaleOrder.setShop(shop);
        wholeSaleOrder.setItems(items);
        wholeSaleOrder.setImeis(imeisToUpdate);
        wholeSaleOrder.setDiscount(dto.getDiscount());
        wholeSaleOrder.setActual_price(dto.getActual_price());
        wholeSaleOrder.setTotal_amount(dto.getTotal_amount());
        wholeSaleOrder.setDate(new Date());

        wholesaleOrderRepository.save(wholeSaleOrder);
    }


    @Override
    public void update(WholesaleOrderDTO data) {
        try {
            Optional<WholeSaleOrder> orderOptional = wholesaleOrderRepository.findById(data.getWholesale_order_id());
            if (orderOptional.isPresent()) {
                WholeSaleOrder returnOrder = orderOptional.get();

                returnOrder.setDiscount(data.getDiscount());
                returnOrder.setActual_price(data.getActual_price());
                returnOrder.setTotal_amount(data.getTotal_amount());
                returnOrder.setDate(data.getDate());
                returnOrder.set_deleted(data.is_deleted());

                wholesaleOrderRepository.save(returnOrder);
            } else {
                throw new EntityNotFoundException("Wholesale order not found for ID: " + data.getWholesale_order_id());
            }
        } catch (Exception e) {
            log.error("Error updating wholesale order: ", e);
            throw e;
        }
    }

    @Override
    public void delete(String id) {
        try {
            Optional<WholeSaleOrder> optional = wholesaleOrderRepository.findById(id);
            if (!optional.isPresent()) {
                throw new RuntimeException("Order not found");
            }
            WholeSaleOrder order = optional.get();
            order.set_deleted(true);
            wholesaleOrderRepository.save(order);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public WholesaleOrderDTO findById(String id) {
        try {
            Optional<WholeSaleOrder> returnOrderOptional = wholesaleOrderRepository.findById(id);
            if (returnOrderOptional.isPresent()) {
                WholeSaleOrder returnOrder = returnOrderOptional.get();
                WholesaleOrderDTO dto = mapper.map(returnOrder, WholesaleOrderDTO.class);
                List<IMEIDTO> imeiDTOs = returnOrder.getImeis().stream()
                        .map(returnPhone -> mapper.map(returnPhone, IMEIDTO.class))
                        .collect(Collectors.toList());
                dto.setImeis(imeiDTOs);
                return dto;
            } else {
                throw new EntityNotFoundException("Wholesale order not found for ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error fetching Wholesale order by ID: ", e);
            throw e;
        }
    }

    @Override
    public List<WholesaleOrderDTO> findAll() {
        try {
            List<WholeSaleOrder> allReturnOrders = wholesaleOrderRepository.findAll();
            List<WholesaleOrderDTO> activeReturnOrderDTOs = allReturnOrders.stream()
                    .filter(returnOrder -> !returnOrder.is_deleted())
                    .map(returnOrder -> {
                        WholesaleOrderDTO dto = mapper.map(returnOrder, WholesaleOrderDTO.class);
                        List<IMEIDTO> imeiDTOs = returnOrder.getImeis().stream()
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

    public long getWholesaleOrderCountForThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        List<WholeSaleOrder> allWholesaleOrders = wholesaleOrderRepository.findAll();
        return allWholesaleOrders.stream()
                .filter(wholesaleOrder -> !wholesaleOrder.is_deleted())
                .filter(wholesaleOrder -> {
                    LocalDate orderDate = wholesaleOrder.getDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return (orderDate.isEqual(firstDayOfMonth) || orderDate.isAfter(firstDayOfMonth))
                            && (orderDate.isEqual(lastDayOfMonth) || orderDate.isBefore(lastDayOfMonth));
                })
                .count();
    }

    /*public List<WholesaleOrderDTO> findAllOrdersInLastTwoMonths() {
        try {
            List<WholeSaleOrder> allWholesaleOrders = wholesaleOrderRepository.findAll();

            // Get the current date and calculate the last two months
            LocalDate now = LocalDate.now();
            LocalDate twoMonthsAgo = now.minusMonths(2);

            List<WholesaleOrderDTO> activeWholesaleOrderDTOs = allWholesaleOrders.stream()
                    .filter(wholesaleOrder -> !wholesaleOrder.is_deleted()) // Filter out deleted orders
                    .filter(wholesaleOrder -> {
                        // Get the order date and convert it to LocalDate
                        Date orderDate = wholesaleOrder.getDate(); // Assuming getDate() returns the date of the order
                        LocalDate localOrderDate = orderDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        // Check if the order date is within the last two months
                        return !localOrderDate.isBefore(twoMonthsAgo) && !localOrderDate.isAfter(now);
                    })
                    .map(wholesaleOrder -> {
                        WholesaleOrderDTO dto = mapper.map(wholesaleOrder, WholesaleOrderDTO.class);
                        List<IMEIDTO> imeiDTOs = wholesaleOrder.getImeis().stream()
                                .map(returnPhone -> mapper.map(returnPhone, IMEIDTO.class))
                                .collect(Collectors.toList());
                        dto.setImeis(imeiDTOs);
                        return dto;
                    })
                    .collect(Collectors.toList());

            return activeWholesaleOrderDTOs;
        } catch (Exception e) {
            log.error("Error fetching wholesale orders: ", e);
            throw e;
        }
    }*/
    public List<WholesaleOrderDTO> findAllOrdersInLastTwoMonths() {
        try {
            List<WholeSaleOrder> allWholesaleOrders = wholesaleOrderRepository.findAll();

            LocalDate now = LocalDate.now();
            LocalDate twoMonthsAgo = now.minusMonths(2);

            List<WholesaleOrderDTO> activeWholesaleOrderDTOs = allWholesaleOrders.stream()
                    .filter(wholesaleOrder -> !wholesaleOrder.is_deleted())
                    .filter(wholesaleOrder -> {
                        Date orderDate = wholesaleOrder.getDate();
                        LocalDate localOrderDate = orderDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();

                        // Check if the order date is within the last two months
                        return !localOrderDate.isBefore(twoMonthsAgo) && !localOrderDate.isAfter(now);
                    })
                    .map(wholesaleOrder -> {
                        WholesaleOrderDTO dto = mapper.map(wholesaleOrder, WholesaleOrderDTO.class);
                        List<IMEIDTO> imeiDTOs = wholesaleOrder.getImeis().stream()
                                .map(returnPhone -> {
                                    IMEIDTO imeiDTO = mapper.map(returnPhone, IMEIDTO.class);

                                    // Set model details in the IMEIDTO
                                    if (returnPhone.getModel() != null) {
                                        ModelDTO modelDTO = new ModelDTO();
                                        modelDTO.setId(returnPhone.getModel().getId());
                                        modelDTO.setName(returnPhone.getModel().getName()); // Set model name
                                        imeiDTO.setModelId(modelDTO);
                                    }

                                    // Set warranty details in the IMEIDTO
                                    if (returnPhone.getWarranty() != null) {
                                        imeiDTO.setWarranty(returnPhone.getWarranty());
                                    }

                                    return imeiDTO;
                                })
                                .collect(Collectors.toList());
                        dto.setImeis(imeiDTOs);
                        return dto;
                    })
                    .collect(Collectors.toList());

            return activeWholesaleOrderDTOs;
        } catch (Exception e) {
            log.error("Error fetching wholesale orders: ", e);
            throw e;
        }
    }



}
