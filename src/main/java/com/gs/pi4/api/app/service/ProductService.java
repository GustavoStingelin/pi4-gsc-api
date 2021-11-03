package com.gs.pi4.api.app.service;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.ResourceNotFoundException;
import com.gs.pi4.api.app.vo.product.ProductVO;
import com.gs.pi4.api.core.product.Product;
import com.gs.pi4.api.core.product.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    @Autowired
    ProductUnitMeasureService unitMeasureService;

    @Autowired
    ImageService imageService;

    @Autowired
    ProductImageStorageService imageStorageService;
/*
    @Transactional
    //--  --public void save(ProductVO vo, User user) {
        // Product entity = parseVOToBeerOrder(vo, user);
        // entity = repository.save(entity);

        // MultipartFile file = vo.getPhoto();
        // storage.uploadFile(file, storage.generateKey(entity.getId()));
    }*/

    public Page<ProductVO> findAllByCompany(Pageable pageable, Long companyId) {
        return repository.findAllByCompany(pageable, companyId).map(this::parse2ProductVO);
    }

    public ProductVO parse2ProductVO(Product entity) {
        return ProductVO.builder()
            .key(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .price(entity.getPrice())
            .unitMeasure(unitMeasureService.parse2UnitMeasureVO(entity.getUnitMeasure()))
            .images(imageService.parse2ImageVO(entity.getImages()))
            .build();
    }

    public byte[] findImage(Long id, String key) {
        if(imageService.findImageByIdAndKey(id, key) == null) {
            throw new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString());
        }

        return imageStorageService.find(key);   
    }

}
