package com.gs.pi4.api.app.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.ResourceNotFoundException;
import com.gs.pi4.api.app.vo.company.CompanyPartnerVO;
import com.gs.pi4.api.app.vo.product.ProductCreateVO;
import com.gs.pi4.api.app.vo.product.ProductVO;
import com.gs.pi4.api.core.company.Company;
import com.gs.pi4.api.core.product.Product;
import com.gs.pi4.api.core.product.ProductImage;
import com.gs.pi4.api.core.product.ProductImageRepository;
import com.gs.pi4.api.core.product.ProductRepository;
import com.gs.pi4.api.core.product.ProductUnitMeasure;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    ProductRepository repository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    ProductUnitMeasureService unitMeasureService;

    @Autowired
    ImageService imageService;

    @Autowired
    ProductImageStorageService imageStorageService;

    @Autowired
    CompanyPartnerService companyPartnerService;

    @Autowired
    CompanyService companyService;

    public Page<ProductVO> findAllByCompany(Pageable pageable, Long companyId) {
        return repository.findAllByCompany(pageable, companyId).map(this::parse2ProductVO);
    }

    public List<ProductVO> findAllByCompanyInPartner(Long companyId) {
        List<Long> companyPartnersId = companyPartnerService.getPartners(companyId).stream().map(CompanyPartnerVO::getToCompanyId).collect(Collectors.toList());
        return parse2ProductVO(repository.findAllByCompanyInPartner(companyPartnersId));
    }

    public List<ProductVO> findAllByCompanyInPartner(Long companyId, Long partnerId) {
        List<Long> companyPartnersId = new ArrayList<>();
        if(Boolean.TRUE.equals(companyPartnerService.isPartner(companyId, partnerId))) {
            companyPartnersId.add(partnerId);
        }
        return parse2ProductVO(repository.findAllByCompanyInPartner(companyPartnersId));
    }

    public ProductVO parse2ProductVO(Product entity) {
        return ProductVO.builder()
            .key(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .price(entity.getPrice())
            .unitMeasure(unitMeasureService.parse2UnitMeasureVO(entity.getUnitMeasure()))
            .images(imageService.parse2ImageVO(entity.getImages()))
            .company(companyPartnerService.parse2CompanyPartnerVO(entity.getCompany()))
            .build();
    }

    public List<ProductVO> parse2ProductVO(List<Product> entities) {
        return entities.stream().map(this::parse2ProductVO).collect(Collectors.toList());
    }


    public ProductVO create(User user, ProductCreateVO vo) {
        Product entity = parse(vo);
        entity.setId(0L);
        entity.setCreatedAt(new Date());
        entity.setCreatedBy(user);
        entity = repository.save(entity);

        List<ProductImage> productImages = imageStorageService.parseImage(imageStorageService.save(vo.getImages()), entity);
        entity.setProductImages(productImageRepository.saveAll(productImages));

        return parse2ProductVO(entity);
    }

    public ProductVO update(User user, Long id, ProductCreateVO vo) {
        Product entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString()));
        entity.setId(id);
        entity.setName(vo.getName());
        entity.setDescription(vo.getDescription());
        entity.setPrice(vo.getPrice());
        entity.setUnitMeasure(unitMeasureService.findById(vo.getUnitMeasureId()));
        entity.setChangedAt(new Date());
        entity.setChangedBy(user);
        entity = repository.save(entity);

        return parse2ProductVO(entity);
    }

    public void delete(User user, Long id) {
        Product entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString()));
        entity.setId(id);
        entity.setDeletedAt(new Date());
        entity.setDeletedBy(user);
        repository.save(entity);
    }

    public Long findCompanyIdByProductId(Long id) {
        Product entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString()));
        return entity.getCompany().getId();
    }

    public ProductVO findById(Long id) {
        Product entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString()));
        return parse2ProductVO(entity);
    }


    public Product parse(ProductCreateVO vo) {
        return Product.builder()
            .id(vo.getKey())
            .name(vo.getName())
            .description(vo.getDescription())
            .price(vo.getPrice())
            .unitMeasure(ProductUnitMeasure.builder().id(vo.getUnitMeasureId()).build())
            .company(Company.builder().id(vo.getCompanyId()).build())
            .build();
    }

    public byte[] findImage(Long id, String key) {
        if(imageService.findImageByIdAndKey(id, key) == null) {
            throw new ResourceNotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString());
        }

        return imageStorageService.find(key);   
    }

}
