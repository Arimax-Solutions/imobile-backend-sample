package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ShopDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.ReturnPhones;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Shop;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.User;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ReturnPhonesRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ShopRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.UserRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ShopServiceIMPL implements ShopService {
    @Autowired
    private final ModelMapper mapper;
    @Autowired
    private final ShopRepository shopRepository;
    @Autowired
    private final ReturnPhonesRepository returnPhonesRepository;

    @Override
    public void save(ShopDTO data) {
        try {
            shopRepository.save(mapper.map(data, Shop.class));
        } catch (Exception e) {
            log.error("Error: ", e);
            throw e;
        }
    }

    @Override
    public void update(ShopDTO data) {
        try {
            Optional<Shop> shopOptional = shopRepository.findById(data.getShop_id());

            if (shopOptional.isPresent() && !shopOptional.get().is_deleted()) {
                Shop existingShop = shopOptional.get();

                existingShop.setShop_name(data.getShop_name());
                existingShop.setAddress(data.getAddress());
                existingShop.setEmail(data.getEmail());
                existingShop.setContact_number(data.getContact_number());
                existingShop.setOwner_nic(data.getOwner_nic());
                existingShop.setCredit_limit(data.getCredit_limit());

                shopRepository.save(mapper.map(data, Shop.class));
            }
        } catch (Exception e) {
            log.error("Error updating shop: ", e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Optional<Shop> shopOptional = shopRepository.findById(id);
            if (!shopOptional.isPresent()) {
                throw new RuntimeException("Shop not found");
            }
            Shop shop = shopOptional.get();
            shop.set_deleted(true);
            shopRepository.save(shop);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public ShopDTO findById(Long id) {
        try {
            Optional<Shop> shopOptional = shopRepository.findById(id);
            if (shopOptional.isPresent() && !shopOptional.get().is_deleted()) {
                return mapper.map(shopOptional.get(), ShopDTO.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error finding shop by ID: ", e);
            throw e;
        }
    }

    public List<ShopDTO> findAll() {
        try {
            List<Shop> allShops = shopRepository.findAll();
            List<ShopDTO> activeShopDTOs = allShops.stream()
                    .filter(shop -> !shop.is_deleted())
                    .map(shop -> mapper.map(shop, ShopDTO.class))
                    .collect(Collectors.toList());
            return activeShopDTOs;
        } catch (Exception e) {
            log.error("Error fetching shops: ", e);
            throw e;
        }
    }

    public List<ShopDTO> findByContactPhone(String contactNumber) {
        System.out.println(contactNumber);
        try {
            List<Shop> allShops = shopRepository.findAll();
            List<ShopDTO> activeShopDTOs = allShops.stream()
                    .filter(shop -> shop.getContact_number() != null &&
                            shop.getContact_number().contains(contactNumber) && !shop.is_deleted())
                    .map(shop -> {
                        // Create DTO
                        ShopDTO dto = mapper.map(shop, ShopDTO.class);

                        // Calculate the outstanding amount for this shop
                        Double outstanding = returnPhonesRepository.findAll().stream()
                                .filter(returnPhone -> returnPhone.getShop() != null &&
                                        returnPhone.getShop().getShop_id().equals(shop.getShop_id()))
                                .mapToDouble(ReturnPhones::getOutStanding)
                                .sum();

                        dto.setOutstanding(outstanding);
                        System.out.println(dto.getOutstanding());
                        return dto;
                    })
                    .collect(Collectors.toList());
            return activeShopDTOs;
        } catch (Exception e) {
            log.error("Error fetching shops: ", e);
            throw e;
        }
    }

}
