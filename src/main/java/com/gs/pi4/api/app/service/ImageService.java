package com.gs.pi4.api.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.app.vo.ImageVO;
import com.gs.pi4.api.core.image.Image;
import com.gs.pi4.api.core.image.ImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class ImageService {

    @Autowired
    ImageRepository repository;


    public Image reserveId() {
        Image entity = Image.builder().id(0L).externalId("reserving").build();
        return repository.save(entity);
    }

    public Image save(@NonNull Image entity) {
        return repository.save(entity);
    }

    protected ImageVO parse2ImageVO(@NonNull Image entity) {
        return ImageVO.builder()
            .externalId(entity.getId() + "/" + entity.getExternalId())
            .build();
    }

    protected List<ImageVO> parse2ImageVO(List<Image> entity) {
        if (entity == null) {
            return new ArrayList<>();
        }

        return entity.stream().map(this::parse2ImageVO).collect(Collectors.toList());
    }

    protected Image findImageByIdAndKey(@NonNull Long id, @NonNull String key) {
        return repository.findByIdAndKey(id, key);
    }
}

