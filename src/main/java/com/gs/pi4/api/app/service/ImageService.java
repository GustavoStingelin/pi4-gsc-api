package com.gs.pi4.api.app.service;

import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.app.vo.ImageVO;
import com.gs.pi4.api.core.image.Image;
import com.gs.pi4.api.core.image.ImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    @Autowired
    ImageRepository repository;

    public ImageVO parse2ImageVO(Image entity) {
        return ImageVO.builder()
            .externalId(entity.getId() + "/" + entity.getExternalId())
            .build();
    }

    public List<ImageVO> parse2ImageVO(List<Image> entity) {
        return entity.stream().map(this::parse2ImageVO).collect(Collectors.toList());
    }

    public Image findImageByIdAndKey(Long id, String key) {
        return repository.findByIdAndKey(id, key);
    }
}

