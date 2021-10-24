package com.systems.integrated.wineshopbackend.service.intef;

import com.systems.integrated.wineshopbackend.models.products.Attribute;
import com.systems.integrated.wineshopbackend.models.products.DTO.AttributeDTO;

import java.util.List;

public interface AttributeService {
    Attribute findById(Long id);
    List<Attribute> findAll();
    Attribute create(AttributeDTO attributeDTO);
    Attribute update(Long id, AttributeDTO attributeDTO);
    void delete(Long id);
}
