package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.CustomerDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ItemDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Customer;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Item;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.CustomerRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ItemRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ItemService;
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
public class ItemServiceIMPL implements ItemService {
    private final ModelMapper mapper;
    private final ItemRepository itemRepository;
    @Override
    public void save(ItemDTO data) {
        try {
            itemRepository.save(mapper.map(data, Item.class));
        } catch (Exception e) {
            log.error("Error: ", e);
            throw e;
        }
    }

    @Override
    public void update(ItemDTO data) {
        try {
            Optional<Item> optional = itemRepository.findById(data.getItem_id());

            if (optional.isPresent() && !optional.get().is_deleted()) {
                Item existingItem = optional.get();

                existingItem.setCategory(data.getName());
                existingItem.setBrand(data.getBrand());
                existingItem.setName(data.getName());
                existingItem.setColour(data.getColour());
                existingItem.setWarranty_period(data.getWarranty_period());
                existingItem.setPrice(data.getPrice());

                itemRepository.save(mapper.map(data, Item.class));
            }
        } catch (Exception e) {
            log.error("Error updating item: ", e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Optional<Item> optional = itemRepository.findById(id);
            if (!optional.isPresent()) {
                throw new RuntimeException("Item not found");
            }
            Item item = optional.get();
            item.set_deleted(true);
            itemRepository.save(item);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public ItemDTO findById(Long id) {
        try {
            Optional<Item> optional = itemRepository.findById(id);
            if (optional.isPresent() && !optional.get().is_deleted()) {
                return mapper.map(optional.get(), ItemDTO.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error finding item by ID: ", e);
            throw e;
        }
    }

    @Override
    public List<ItemDTO> findAll() {
        try {
            List<Item> allItems = itemRepository.findAll();
            List<ItemDTO> activeItemDTOs = allItems.stream()
                    .filter(item -> !item.is_deleted())
                    .map(item -> mapper.map(item, ItemDTO.class))
                    .collect(Collectors.toList());
            return activeItemDTOs;
        } catch (Exception e) {
            log.error("Error fetching items: ", e);
            throw e;
        }
    }

    //create me a method for search item by name
    public List<ItemDTO> findByNameContaining(String namePart) {
        try {
            List<Item> allItems = itemRepository.findAll();
            List<ItemDTO> filteredItemDTOs = allItems.stream()
                    .filter(item -> item.getName() != null &&
                            item.getName().toLowerCase().contains(namePart.toLowerCase()) &&
                            !item.is_deleted())
                    .map(item -> mapper.map(item, ItemDTO.class))
                    .collect(Collectors.toList());
            return filteredItemDTOs;
        } catch (Exception e) {
            log.error("Error fetching items: ", e);
            throw e;
        }
    }


}
