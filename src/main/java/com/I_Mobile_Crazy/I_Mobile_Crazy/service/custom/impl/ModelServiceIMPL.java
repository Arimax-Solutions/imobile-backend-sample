package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.ModelDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.IMEI;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Models;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ModelRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.ModelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class ModelServiceIMPL implements ModelService {
    private final ModelRepository modelRepository;
    @Override
    @Transactional
    public void save(ModelDTO modelDTO) {
        // Create Model entity from ModelDTO
        Models model = new Models();
        model.setName(modelDTO.getName());
        // Set other properties as needed

        // Save Model entity to get generated ID
        modelRepository.save(model);

        // No need to save IMEI numbers here, assuming IMEI is handled separately
    }

    @Override
    public void update(ModelDTO data) {

    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public ModelDTO findById(Long aLong) {
        return null;
    }

    @Override
    public List<ModelDTO> findAll() {
        return null;
    }

}
