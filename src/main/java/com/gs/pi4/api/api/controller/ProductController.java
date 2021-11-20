package com.gs.pi4.api.api.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import com.amazonaws.services.kms.model.NotFoundException;
import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.UnauthorizedActionException;
import com.gs.pi4.api.app.service.CompanyService;
import com.gs.pi4.api.app.service.ProductService;
import com.gs.pi4.api.app.vo.product.ProductCreateVO;
import com.gs.pi4.api.app.vo.product.ProductVO;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "ProductEndpoint")
@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductService service;

    @Autowired
    CompanyService companyService;

    @Autowired
    private PagedResourcesAssembler<ProductVO> assembler;

    @Transactional
    @ApiOperation(value = "Returns a list of my products")
    @GetMapping(value = "my/company/{companyId}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public ResponseEntity<PagedModel<EntityModel<ProductVO>>> getMy(Authentication authentication, @PathVariable("companyId") Long companyId,
            @RequestParam(value = "page", defaultValue = "0") int pageRequest,
            @RequestParam(value = "limit", defaultValue = "10") int size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction) {
        User user = (User) authentication.getPrincipal();

        if (Boolean.FALSE.equals(companyService.userHasCompany(user, companyId))) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(pageRequest, size, Sort.by(sortDirection, "name"));

        Page<ProductVO> page = service.findAllByCompany(pageable, companyId);
        page.stream().forEach(el -> el
                .add(linkTo(methodOn(ProductController.class).findById(authentication, el.getKey())).withSelfRel()));

        PagedModel<EntityModel<ProductVO>> resources = assembler.toModel(page);

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @Transactional
    @ApiOperation(value = "Returns a product")
    @GetMapping(value = "my/{id}", produces = { "application/json", "application/xml", "application/x-yaml" })
    public ProductVO findById(Authentication authentication, @PathVariable("id") Long id) {

        User user = (User) authentication.getPrincipal();

        if (Boolean.FALSE.equals(companyService.userHasCompany(user, service.findCompanyIdByProductId(id)))) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findById(id);
    }

    @Transactional
    @ApiOperation(value = "Returns a resource")
    @GetMapping(value = "/image/{id}/{key}", produces = { MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE })
    public ResponseEntity<ByteArrayResource> getImage(Authentication authentication, @PathVariable("id") Long id,
            @PathVariable("key") String key) {
        byte[] data = service.findImage(id, key);

        if (data == null) {
            throw new NotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString());
        }

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok().contentLength(data.length)
                .header("Content-disposition", "inline; filename=\"" + id + "/" + key + "\"").body(resource);
    }

    @Transactional
    @ApiOperation(value = "Create a product")
    @PostMapping(value = "my", produces = { "application/json", "application/xml", "application/x-yaml" })
    public ProductVO create(Authentication authentication, @ModelAttribute ProductCreateVO vo) {
        User user = (User) authentication.getPrincipal();
        if (Boolean.FALSE.equals(companyService.userHasCompany(user, vo.getCompanyId()))) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.create(user, vo);
    }

    @ApiOperation(value = "Get a product list")
    @GetMapping(value = "/company/{id}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public List<ProductVO> getAllInPartners(Authentication authentication, @PathVariable("id") Long id) {
        User user = (User) authentication.getPrincipal();
        if (Boolean.FALSE.equals(companyService.userHasCompany(user, id))) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findAllByCompanyInPartner(id);
    }

    @ApiOperation(value = "Get a product")
    @GetMapping(value = "/{id2}/company/{id}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public ProductVO getProduct(Authentication authentication, @PathVariable("id") Long id, @PathVariable("id2") Long id2) {
        User user = (User) authentication.getPrincipal();
        if (Boolean.FALSE.equals(companyService.userHasCompany(user, id))) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findByIdInCompanyPartner(id, id2);
    }

    
    @ApiOperation(value = "Get a product list")
    @GetMapping(value = "/company/{id}/of_company/{id2}", produces = { "application/json", "application/xml",
            "application/x-yaml" })
    public List<ProductVO> getAllInCompany(Authentication authentication, @PathVariable("id") Long id, @PathVariable("id2") Long id2) {
        User user = (User) authentication.getPrincipal();
        if (Boolean.FALSE.equals(companyService.userHasCompany(user, id))) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.findAllByCompanyInPartner(id, id2);
    }

    @Transactional
    @ApiOperation(value = "Update a product")
    @PutMapping(value = "my/{id}", produces = { "application/json", "application/xml", "application/x-yaml" })
    public ProductVO update(Authentication authentication, @ModelAttribute ProductCreateVO vo,
            @PathVariable("id") Long id) {
        User user = (User) authentication.getPrincipal();

        if (Boolean.FALSE.equals(companyService.userHasCompany(user, service.findCompanyIdByProductId(id)))) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        if (Boolean.FALSE.equals(companyService.userHasCompany(user, vo.getCompanyId()))) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        return service.update(user, id, vo);
    }

    @Transactional
    @ApiOperation(value = "Delete a product")
    @DeleteMapping(value = "my/{id}", produces = { "application/json", "application/xml", "application/x-yaml" })
    public void delete(Authentication authentication, @PathVariable("id") Long id) {
        User user = (User) authentication.getPrincipal();

        if (Boolean.FALSE.equals(companyService.userHasCompany(user, service.findCompanyIdByProductId(id)))) {
            throw new UnauthorizedActionException(CodeExceptionEnum.UNAUTHORIZED_RESOURCE_ACCESS.toString());
        }

        service.delete(user, id);
    }

}
