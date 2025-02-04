package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.*;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.*;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.*;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.RetailOrderService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class RetailOrderServiceIMPL implements RetailOrderService {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private final ModelMapper mapper;
    private final RetailOrderRepository retailOrderRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ReturnPhonesRepository returnPhonesRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private IMEIRepository imeiRepository;
    @Autowired
    private WholesaleOrderRepository wholesaleOrderRepository;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    public void update(RetailOrderDTO data) {
        try {
            Optional<RetailOrder> retailOrderOptional = retailOrderRepository.findById(data.getRetail_order_id());
            if (retailOrderOptional.isPresent()) {
                RetailOrder retailOrder = retailOrderOptional.get();

                retailOrder.setDiscount(data.getDiscount());
                retailOrder.setActual_price(data.getActual_price());
                retailOrder.setTotal_amount(data.getTotal_amount());
                retailOrder.setDate(data.getDate());
                retailOrder.set_deleted(data.is_deleted());

                retailOrderRepository.save(retailOrder);
            } else {
                throw new EntityNotFoundException("Return order not found for ID: " + data.getRetail_order_id());
            }
        } catch (Exception e) {
            log.error("Error updating return order: ", e);
            throw e;
        }
    }

    @Override
    public void delete(String id) {
        try {
            Optional<RetailOrder> optional = retailOrderRepository.findById(id);
            if (!optional.isPresent()) {
                throw new RuntimeException("Order not found");
            }
            RetailOrder order = optional.get();
            order.set_deleted(true);
            retailOrderRepository.save(order);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public RetailOrderDTO findById(String id) {
        try {
            Optional<RetailOrder> retailOrderOptional = retailOrderRepository.findById(id);
            if (retailOrderOptional.isPresent()) {
                RetailOrder retailOrder = retailOrderOptional.get();
                RetailOrderDTO dto = mapper.map(retailOrder, RetailOrderDTO.class);
                List<IMEIDTO> imeiDTOs = retailOrder.getImeis().stream()
                        .map(retailPhone -> mapper.map(retailPhone, IMEIDTO.class))
                        .collect(Collectors.toList());
                dto.setImeis(imeiDTOs);
                return dto;
            } else {
                throw new EntityNotFoundException("Retail order not found for ID: " + id);
            }
        } catch (Exception e) {
            log.error("Error fetching retail order by ID: ", e);
            throw e;
        }
    }

    @Override
    public List<RetailOrderDTO> findAll() {
        try {
            List<RetailOrder> allRetailOrders = retailOrderRepository.findAll();
            List<RetailOrderDTO> activeRetailOrderDTOs = allRetailOrders.stream()
                    .filter(retailOrder -> !retailOrder.is_deleted())
                    .map(retailOrder -> {
                        RetailOrderDTO dto = mapper.map(retailOrder, RetailOrderDTO.class);
                        List<IMEIDTO> imeiDTOs = retailOrder.getImeis().stream()
                                .map(imei -> {
                                    IMEIDTO imeiDTO = mapper.map(imei, IMEIDTO.class);

                                    // Set model details in the IMEIDTO
                                    if (imei.getModel() != null) {
                                        ModelDTO modelDTO = new ModelDTO();
                                        modelDTO.setId(imei.getModel().getId());
                                        modelDTO.setName(imei.getModel().getName());  // Set model name
                                        imeiDTO.setModelId(modelDTO);
                                    }

                                    return imeiDTO;
                                })
                                .collect(Collectors.toList());
                        dto.setImeis(imeiDTOs);
                        return dto;
                    })
                    .collect(Collectors.toList());
            return activeRetailOrderDTOs;
        } catch (Exception e) {
            log.error("Error fetching orders: ", e);
            throw e;
        }
    }


    public List<ReturnPhones> findByCustomerId(Long customerId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ReturnPhones> criteriaQuery = criteriaBuilder.createQuery(ReturnPhones.class);
        Root<ReturnPhones> returnPhonesRoot = criteriaQuery.from(ReturnPhones.class);

        // Access the customer field and its id
        Join<ReturnPhones, Customer> customerJoin = returnPhonesRoot.join("customer");
        Predicate customerPredicate = criteriaBuilder.equal(customerJoin.get("id"), customerId);
        criteriaQuery.where(customerPredicate);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    @Transactional
    public void save(RetailOrderDTO retailOrderDTO) {
        // Find customer
        Optional<Customer> customerOptional = customerRepository.findById(retailOrderDTO.getCustomer().getCustomer_id());
        Customer customer = customerOptional.orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        // Update outstanding balance in ReturnPhones
        List<ReturnPhones> returnPhonesList = this.findByCustomerId(customer.getCustomer_id());
        if (returnPhonesList != null && !returnPhonesList.isEmpty()) {
            returnPhonesList.forEach(returnPhone -> {
                returnPhone.setOutStanding(0.0);
                returnPhonesRepository.save(returnPhone);
            });
        }

        // Handle items
        List<Item> items = Collections.emptyList();
        if (retailOrderDTO.getItems() != null && !retailOrderDTO.getItems().isEmpty()) {
            items = retailOrderDTO.getItems().stream()
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

        // Handle IMEIs
        List<IMEI> imeisToUpdate = Collections.emptyList();
        if (retailOrderDTO.getImeis() != null && !retailOrderDTO.getImeis().isEmpty()) {
            List<String> imeiNumbers = retailOrderDTO.getImeis().stream()
                    .map(IMEIDTO::getImei)
                    .collect(Collectors.toList());

            imeisToUpdate = imeiRepository.findByImeiIn(imeiNumbers);
            if (imeisToUpdate == null || imeisToUpdate.isEmpty()) {
                throw new IllegalArgumentException("No IMEIs found with the given numbers");
            }

            imeisToUpdate.forEach(imei -> {
                imei.setStatus("sold");
                imei.setWarranty(retailOrderDTO.getImeis().stream()
                        .filter(imeiDTO -> imeiDTO.getImei().equals(imei.getImei()))
                        .map(IMEIDTO::getWarranty)
                        .findFirst()
                        .orElse(imei.getWarranty())); // Default to existing warranty if none is found
                imei.setPrice(retailOrderDTO.getImeis().stream()
                        .filter(imeiDTO -> imeiDTO.getImei().equals(imei.getImei()))
                        .map(IMEIDTO::getPrice)
                        .findFirst()
                        .orElse(imei.getPrice())); // Default to existing price if none is found
            });
            imeiRepository.saveAll(imeisToUpdate);
        }

        // Create and save RetailOrder
        RetailOrder retailOrder = new RetailOrder();
        retailOrder.setRetail_order_id(retailOrderDTO.getRetail_order_id());
        retailOrder.setCustomer(customer);
        retailOrder.setItems(items);
        retailOrder.setImeis(imeisToUpdate);
        retailOrder.setDiscount(retailOrderDTO.getDiscount());
        retailOrder.setActual_price(retailOrderDTO.getActual_price());
        retailOrder.setTotal_amount(retailOrderDTO.getTotal_amount());
        retailOrder.setDate(new Date());

        retailOrderRepository.save(retailOrder);
    }

    public List<RetailOrderDTO> findAllTodayRetailOrders() {
        try {
            LocalDate today = LocalDate.now();
            List<RetailOrder> allRetailOrders = retailOrderRepository.findAll();
            List<RetailOrderDTO> activeRetailOrderDTOs = allRetailOrders.stream()
                    .filter(retailOrder -> !retailOrder.is_deleted())
                    .filter(retailOrder -> {
                        LocalDate orderDate = retailOrder.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        return orderDate.equals(today);
                    })
                    .map(retailOrder -> {
                        RetailOrderDTO dto = mapper.map(retailOrder, RetailOrderDTO.class);

                        List<IMEIDTO> imeiDTOs = retailOrder.getImeis().stream()
                                .map(imei -> {
                                    IMEIDTO imeiDTO = mapper.map(imei, IMEIDTO.class);

                                    if (imei.getModel() != null && imei.getModel().getId() != null) {
                                        Models model = modelRepository.findById(imei.getModel().getId())
                                                .orElse(null);
                                        if (model != null) {
                                            ModelDTO modelDTO = new ModelDTO();
                                            modelDTO.setId(model.getId());
                                            modelDTO.setName(model.getName());
                                            imeiDTO.setModelId(modelDTO);
                                        } else {
                                            imeiDTO.setModelId(new ModelDTO());
                                        }
                                    } else {
                                        imeiDTO.setModelId(new ModelDTO());
                                    }

                                    return imeiDTO;
                                })
                                .collect(Collectors.toList());
                        dto.setImeis(imeiDTOs);
                        return dto;
                    })
                    .collect(Collectors.toList());
            return activeRetailOrderDTOs;
        } catch (Exception e) {
            log.error("Error fetching orders: ", e);
            throw e;
        }
    }

    public List<WholesaleOrderDTO> findAllTodayWholesaleOrders() {
        try {
            LocalDate today = LocalDate.now();
            List<WholeSaleOrder> allWholesaleOrders = wholesaleOrderRepository.findAll();
            List<WholesaleOrderDTO> activeWholesaleOrderDTOs = allWholesaleOrders.stream()
                    .filter(wholesaleOrder -> !wholesaleOrder.is_deleted())
                    .filter(wholesaleOrder -> {
                        LocalDate orderDate = wholesaleOrder.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        return orderDate.equals(today);
                    })
                    .map(wholesaleOrder -> {
                        WholesaleOrderDTO dto = mapper.map(wholesaleOrder, WholesaleOrderDTO.class);

                        List<IMEIDTO> imeiDTOs = wholesaleOrder.getImeis().stream()
                                .map(imei -> {
                                    IMEIDTO imeiDTO = mapper.map(imei, IMEIDTO.class);

                                    if (imei.getModel() != null && imei.getModel().getId() != null) {
                                        Models model = modelRepository.findById(imei.getModel().getId())
                                                .orElse(null);
                                        if (model != null) {
                                            ModelDTO modelDTO = new ModelDTO();
                                            modelDTO.setId(model.getId());
                                            modelDTO.setName(model.getName());
                                            imeiDTO.setModelId(modelDTO);
                                        } else {
                                            imeiDTO.setModelId(new ModelDTO());
                                        }
                                    } else {
                                        imeiDTO.setModelId(new ModelDTO());
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


    public List<ReturnOrderDTO> findAllTodayReturnOrders() {
        try {
            LocalDate today = LocalDate.now();
            List<ReturnOrder> allReturnOrders = returnOrderRepository.findAll();
            List<ReturnOrderDTO> activeReturnOrderDTOs = allReturnOrders.stream()
                    .filter(returnOrder -> !returnOrder.is_deleted())
                    .filter(returnOrder -> {
                        LocalDate orderDate = returnOrder.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        return orderDate.equals(today);
                    })
                    .map(returnOrder -> {
                        ReturnOrderDTO dto = mapper.map(returnOrder, ReturnOrderDTO.class);

                        List<IMEIDTO> imeiDTOs = returnOrder.getReturnPhones().stream()
                                .map(imei -> {
                                    IMEIDTO imeiDTO = mapper.map(imei, IMEIDTO.class);

                                    return imeiDTO;
                                })
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

    public double findMonthlyIncomeRetail() {
        try {
            LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

            List<RetailOrder> allRetailOrders = retailOrderRepository.findAll();
            BigDecimal monthlyIncome = allRetailOrders.stream()
                    .filter(retailOrder -> !retailOrder.is_deleted())
                    .filter(retailOrder -> {
                        LocalDate orderDate = retailOrder.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        return (orderDate.isEqual(firstDayOfMonth) || orderDate.isAfter(firstDayOfMonth))
                                && (orderDate.isEqual(lastDayOfMonth) || orderDate.isBefore(lastDayOfMonth));
                    })
                    .map(retailOrder -> BigDecimal.valueOf(retailOrder.getTotal_amount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return monthlyIncome.doubleValue();
        } catch (Exception e) {
            log.error("Error calculating monthly income: ", e);
            throw new RuntimeException("Error calculating monthly income", e);
        }
    }

    public double calculateMonthlyIncomeWholesale() {
        try {
            LocalDate now = LocalDate.now();
            YearMonth currentMonth = YearMonth.from(now);

            List<WholeSaleOrder> allWholesaleOrders = wholesaleOrderRepository.findAll();

            double totalIncome = allWholesaleOrders.stream()
                    .filter(wholesaleOrder -> !wholesaleOrder.is_deleted())
                    .filter(wholesaleOrder -> {
                        LocalDate orderDate = wholesaleOrder.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        YearMonth orderMonth = YearMonth.from(orderDate);
                        return orderMonth.equals(currentMonth);
                    })
                    .mapToDouble(wholesaleOrder -> wholesaleOrder.getTotal_amount())
                    .sum();

            return totalIncome;
        } catch (Exception e) {
            log.error("Error calculating monthly income: ", e);
            throw e;
        }
    }

    public double calculateMonthlyIncomeForReturnOrders() {
        try {
            LocalDate now = LocalDate.now();
            YearMonth currentMonth = YearMonth.from(now);

            List<ReturnOrder> allReturnOrders = returnOrderRepository.findAll();

            double totalIncome = allReturnOrders.stream()
                    .filter(returnOrder -> !returnOrder.is_deleted())
                    .filter(returnOrder -> {
                        LocalDate orderDate = returnOrder.getDate().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        YearMonth orderMonth = YearMonth.from(orderDate);
                        return orderMonth.equals(currentMonth);
                    })
                    .mapToDouble(returnOrder -> returnOrder.getTotal_amount())
                    .sum();

            return totalIncome;
        } catch (Exception e) {
            log.error("Error calculating monthly income for return orders: ", e);
            throw e;
        }
    }

    public double calculateMonthlyAllIncome() {
        return findMonthlyIncomeRetail() + calculateMonthlyIncomeWholesale() + calculateMonthlyIncomeForReturnOrders();
    }

    public long getRetailOrderCountForThisMonth() {
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        List<RetailOrder> allRetailOrders = retailOrderRepository.findAll();
        return allRetailOrders.stream()
                .filter(retailOrder -> !retailOrder.is_deleted())
                .filter(retailOrder -> {
                    LocalDate orderDate = retailOrder.getDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return (orderDate.isEqual(firstDayOfMonth) || orderDate.isAfter(firstDayOfMonth))
                            && (orderDate.isEqual(lastDayOfMonth) || orderDate.isBefore(lastDayOfMonth));
                })
                .count();
    }

    public List<RetailOrderDTO> findAllInTwoMonths() {
        try {
            List<RetailOrder> allRetailOrders = retailOrderRepository.findAll();

            // Create a list to hold all orders found within the last two months
            List<RetailOrder> ordersWithinTwoMonths = new ArrayList<>();

            // Get the current date and calculate the last two months
            LocalDate now = LocalDate.now();
            LocalDate twoMonthsAgo = now.minusMonths(2);

            for (RetailOrder retailOrder : allRetailOrders) {
                if (!retailOrder.is_deleted()) {
                    // Get the order date
                    Date orderDate = retailOrder.getDate(); // Assuming getDate() returns the date of the order

                    // Convert Date to LocalDate for comparison
                    LocalDate localOrderDate = orderDate.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    // Check if the order date is within the last two months
                    if (!localOrderDate.isBefore(twoMonthsAgo) && !localOrderDate.isAfter(now)) {
                        // Add the retail order to the list
                        ordersWithinTwoMonths.add(retailOrder);
                    }
                }
            }

            // Map the orders to RetailOrderDTOs
            List<RetailOrderDTO> activeRetailOrderDTOs = ordersWithinTwoMonths.stream()
                    .map(retailOrder -> {
                        RetailOrderDTO dto = mapper.map(retailOrder, RetailOrderDTO.class);
                        List<IMEIDTO> imeiDTOs = retailOrder.getImeis().stream()
                                .map(imei -> {
                                    IMEIDTO imeiDTO = mapper.map(imei, IMEIDTO.class);

                                    // Set model details in the IMEIDTO
                                    if (imei.getModel() != null) {
                                        ModelDTO modelDTO = new ModelDTO();
                                        modelDTO.setId(imei.getModel().getId());
                                        modelDTO.setName(imei.getModel().getName());  // Set model name
                                        imeiDTO.setModelId(modelDTO);
                                    }

                                    return imeiDTO;
                                })
                                .collect(Collectors.toList());
                        dto.setImeis(imeiDTOs);
                        return dto;
                    })
                    .collect(Collectors.toList());

            return activeRetailOrderDTOs;
        } catch (Exception e) {
            log.error("Error fetching orders: ", e);
            throw e;
        }
    }


}
