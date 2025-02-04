package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.CustomerDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ReturnItemDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Customer;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.ReturnItem;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.CustomerRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ReturnItemRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ReturnItemService;
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
public class ReturnItemServiceIMPL implements ReturnItemService {
    private final ModelMapper mapper;
    private final ReturnItemRepository returnItemRepository;
    @Override
    public void save(ReturnItemDTO data) {
        try {
            returnItemRepository.save(mapper.map(data, ReturnItem.class));
        } catch (Exception e) {
            log.error("Error: ", e);
            throw e;
        }
    }

    @Override
    public void update(ReturnItemDTO data) {
        try {
            Optional<ReturnItem> optional = returnItemRepository.findById(data.getReturn_phone_id());

            if (optional.isPresent() && !optional.get().is_deleted()) {
                ReturnItem existingReturnItem = optional.get();

                existingReturnItem.setName(data.getName());
                existingReturnItem.setCategory(data.getCategory());
                existingReturnItem.setBrand(data.getBrand());
                existingReturnItem.setContact_number(data.getContact_number());

                returnItemRepository.save(mapper.map(data, ReturnItem.class));
            }
        } catch (Exception e) {
            log.error("Error updating item: ", e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Optional<ReturnItem> optional = returnItemRepository.findById(id);
            if (!optional.isPresent()) {
                throw new RuntimeException("Item not found");
            }
            ReturnItem item = optional.get();
            item.set_deleted(true);
            returnItemRepository.save(item);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public ReturnItemDTO findById(Long id) {
        try {
            Optional<ReturnItem> optional = returnItemRepository.findById(id);
            if (optional.isPresent() && !optional.get().is_deleted()) {
                return mapper.map(optional.get(), ReturnItemDTO.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error finding item by ID: ", e);
            throw e;
        }
    }

    @Override
    public List<ReturnItemDTO> findAll() {
        try {
            List<ReturnItem> allItems = returnItemRepository.findAll();
            List<ReturnItemDTO> activeItemDTOs = allItems.stream()
                    .filter(item -> !item.is_deleted())
                    .map(item -> mapper.map(item, ReturnItemDTO.class))
                    .collect(Collectors.toList());
            return activeItemDTOs;
        } catch (Exception e) {
            log.error("Error fetching items: ", e);
            throw e;
        }
    }
}
