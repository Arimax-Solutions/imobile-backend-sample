package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ReturnPhonesDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.IMEI;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.ReturnPhones;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.CustomerRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.IMEIRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ReturnPhonesRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ShopRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ReturnPhonesService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

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
public class ReturnPhonesServiceIMPL implements ReturnPhonesService {
    private final ModelMapper mapper;
    private final ReturnPhonesRepository returnPhonesRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final IMEIRepository imeiRepository;

    @Override
    @Transactional
    public void save(ReturnPhonesDTO data) {
        // Log the input DTO for debugging
        System.out.println("Received ReturnPhonesDTO: " + data);

        ReturnPhones returnPhones = mapper.map(data, ReturnPhones.class);

        if (data.getCustomer_id() != null) {
            returnPhones.setCustomer(customerRepository.findById(data.getCustomer_id())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found")));
        } else {
            returnPhones.setCustomer(null);
        }

        if (data.getShop_id() != null) {
            returnPhones.setShop(shopRepository.findById(data.getShop_id())
                    .orElseThrow(() -> new IllegalArgumentException("Shop not found")));
        } else {
            returnPhones.setShop(null);
        }

        ReturnPhones savedReturnPhones = returnPhonesRepository.save(returnPhones);

        // Log the IMEI being searched for
        String imeiValue = data.getImei();
        System.out.println("Searching for IMEI: " + imeiValue);

        if (imeiValue == null || imeiValue.isEmpty()) {
            throw new IllegalArgumentException("IMEI value is null or empty");
        }

        IMEI imei = imeiRepository.findByImei(imeiValue);
        if (imei != null) {
            imei.setDeleted(true);
            imeiRepository.save(imei);
            System.out.println("IMEI found and marked as deleted: " + imeiValue);
        } else {
            throw new IllegalArgumentException("IMEI not found: " + imeiValue);
        }

        ReturnPhonesDTO returnPhonesDTO = mapper.map(savedReturnPhones, ReturnPhonesDTO.class);
        // Log the saved return phones DTO for verification
        System.out.println("Saved ReturnPhonesDTO: " + returnPhonesDTO);
    }

//    @Override
//    public void update(ReturnPhonesDTO data) {
//        try {
//            Optional<ReturnPhones> optional = returnPhonesRepository.findById(data.getShop_id());
//
//            if (optional.isPresent() && !optional.get().is_deleted()) {
//                ReturnPhones existingPhone = optional.get();
//
//                existingPhone.setImei(data.getImei());
//                existingPhone.setModel(data.getModel());
//                existingPhone.setStorage(data.getStorage());
//                existingPhone.setContact_number(data.getContact_number());
//                existingPhone.setOutStanding(data.getOutStanding());
//                existingPhone.setColour(data.getColour());
//                existingPhone.setReason(data.getReason());
//                existingPhone.setName(data.getName());
//
//                returnPhonesRepository.save(mapper.map(data, ReturnPhones.class));
//            }
//        } catch (Exception e) {
//            log.error("Error updating shop: ", e);
//            throw e;
//        }
//    }

    @Override
    public void update(ReturnPhonesDTO data) {
        try {
            Optional<ReturnPhones> optional = returnPhonesRepository.findById(data.getShop_id());

            if (optional.isPresent() && !optional.get().is_deleted()) {
                ReturnPhones existingPhone = optional.get();

                existingPhone.setImei(data.getImei());
                existingPhone.setModel(data.getModel());
                existingPhone.setStorage(data.getStorage());
                existingPhone.setContact_number(data.getContact_number());
                existingPhone.setOutStanding(data.getOutStanding());
                existingPhone.setColour(data.getColour());
                existingPhone.setReason(data.getReason());
                existingPhone.setName(data.getName());

                returnPhonesRepository.save(existingPhone);
            }
        } catch (Exception e) {
            log.error("Error updating shop: ", e);
            throw e;
        }
    }


    @Override
    public void delete(Long id) {
        try {
            Optional<ReturnPhones> shopOptional = returnPhonesRepository.findById(id);
            if (!shopOptional.isPresent()) {
                throw new RuntimeException("Phone not found");
            }
            ReturnPhones phones = shopOptional.get();
            phones.set_deleted(true);
            returnPhonesRepository.save(phones);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public ReturnPhonesDTO findById(Long id) {
        try {
            Optional<ReturnPhones> optional = returnPhonesRepository.findById(id);
            if (optional.isPresent() && !optional.get().is_deleted()) {
                return mapper.map(optional.get(), ReturnPhonesDTO.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error finding phone by ID: ", e);
            throw e;
        }
    }

    @Override
    public List<ReturnPhonesDTO> findAll() {
        try {
            List<ReturnPhones> allShops = returnPhonesRepository.findAll();

            List<ReturnPhonesDTO> activeShopDTOs = allShops.stream()
                    .filter(shop -> !shop.is_deleted())
                    .map(shop -> mapper.map(shop, ReturnPhonesDTO.class))
                    .collect(Collectors.toList());

            return activeShopDTOs;
        } catch (Exception e) {
            log.error("Error fetching shops: ", e);
            throw e;
        }
    }

    public ReturnPhonesDTO findByImei(String imei) {
        try {
            Optional<ReturnPhones> optional = returnPhonesRepository.findByImei(imei);
            if (optional.isPresent() && !optional.get().is_deleted()) {
                return mapper.map(optional.get(), ReturnPhonesDTO.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error finding phone by IMEI: ", e);
            throw e;
        }
    }


}
