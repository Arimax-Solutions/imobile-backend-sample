package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.impl;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.CustomerDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Customer;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.ReturnPhones;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.CustomerRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.ReturnPhonesRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.CustomerService;
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
public class CustomerServiceIMPL implements CustomerService {
    private final ModelMapper mapper;
    private final CustomerRepository customerRepository;
    private final ReturnPhonesRepository returnPhonesRepository;

    @Override
    public void save(CustomerDTO data) {
        try {
            customerRepository.save(mapper.map(data, Customer.class));
        } catch (Exception e) {
            log.error("Error: ", e);
            throw e;
        }
    }

    @Override
    public void update(CustomerDTO data) {
        try {
            Optional<Customer> optional = customerRepository.findById(data.getCustomer_id());

            if (optional.isPresent() && !optional.get().is_deleted()) {
                Customer existingCustomer = optional.get();

                existingCustomer.setName(data.getName());
                existingCustomer.setEmail(data.getEmail());
                existingCustomer.setContact_phone(data.getContact_phone());
                existingCustomer.setNic(data.getNic());

                customerRepository.save(mapper.map(data, Customer.class));
            }
        } catch (Exception e) {
            log.error("Error updating customer: ", e);
            throw e;
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Optional<Customer> optional = customerRepository.findById(id);
            if (!optional.isPresent()) {
                throw new RuntimeException("Customer not found");
            }
            Customer customer = optional.get();
            customer.set_deleted(true);
            customerRepository.save(customer);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public CustomerDTO findById(Long id) {
        try {
            Optional<Customer> optional = customerRepository.findById(id);
            if (optional.isPresent() && !optional.get().is_deleted()) {
                return mapper.map(optional.get(), CustomerDTO.class);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error finding customer by ID: ", e);
            throw e;
        }
    }

    @Override
    public List<CustomerDTO> findAll() {
        try {
            List<Customer> allCustomers = customerRepository.findAll();
            List<CustomerDTO> activeCustomerDTOs = allCustomers.stream()
                    .filter(customer -> !customer.is_deleted())
                    .map(customer -> mapper.map(customer, CustomerDTO.class))
                    .collect(Collectors.toList());
            return activeCustomerDTOs;
        } catch (Exception e) {
            log.error("Error fetching customers: ", e);
            throw e;
        }
    }

    public List<CustomerDTO> findByContact_phone(String contact_number) {
        try {
            List<Customer> allCustomers = customerRepository.findAll();
            List<CustomerDTO> activeCustomerDTOs = allCustomers.stream()
                    .filter(customer -> customer.getContact_phone() != null &&
                            customer.getContact_phone().contains(contact_number) && !customer.is_deleted())
                    .map(customer -> {
                        // Create DTO
                        CustomerDTO dto = mapper.map(customer, CustomerDTO.class);

                        // Calculate the outstanding amount for this customer
                        Double outstandingAmount = returnPhonesRepository.findAll().stream()
                                .filter(returnPhone -> returnPhone.getCustomer() != null &&
                                        returnPhone.getCustomer().getCustomer_id().equals(customer.getCustomer_id()))
                                .mapToDouble(ReturnPhones::getOutStanding)
                                .sum();

                        dto.setOutstandingAmount(outstandingAmount);
                        return dto;
                    })
                    .collect(Collectors.toList());
            return activeCustomerDTOs;
        } catch (Exception e) {
            log.error("Error fetching customers: ", e);
            throw e;
        }
    }


}
