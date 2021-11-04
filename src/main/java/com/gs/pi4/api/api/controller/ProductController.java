package com.gs.pi4.api.api.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.UnauthorizedActionException;
import com.gs.pi4.api.app.service.CompanyService;
import com.gs.pi4.api.app.service.ProductService;
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
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<?> getMy(Authentication authentication, @PathVariable("companyId") Long companyId,
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
        /*
         * page .stream() .forEach(el -> el.add( linkTo(
         * methodOn(ProductController.class).findById(authentication, el.getKey())
         * ).withSelfRel() ));
         */
        PagedModel<?> resources = assembler.toModel(page);

        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    /*
     * private ResponseEntity<?> findById(Authentication authentication, Long key) {
     * return new ; }
     */

    @ApiOperation(value = "Returns a resource")
    @GetMapping(value = "/image/{id}/{key}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<ByteArrayResource> getImage(Authentication authentication, @PathVariable("id") Long id,
            @PathVariable("key") String key) {
        byte[] data = service.findImage(id, key);

        if (data == null) {
            throw new NotFoundException(CodeExceptionEnum.RESOURCE_NOT_FOUND.toString());
        }

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok().contentLength(data.length)
            .header("Content-disposition", "inline; filename=\"" + id + "/" + key + "\"")
            .body(resource);
    }

}