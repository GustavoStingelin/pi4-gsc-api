package com.gs.pi4.api.app.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.google.common.hash.Hashing;
import com.gs.pi4.api.api.exception.FileStorageException;
import com.gs.pi4.api.core.image.Image;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public abstract class StorageService {

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private ImageService service;    

    private String prefix;

    protected StorageService(String prefix) {
        this.prefix = prefix + "/";
    }

    private String generateKey(Long id, String fileType) {
        return Hashing.sha256().hashString(prefix + id + fileType + new Date() + Math.random(), StandardCharsets.UTF_8).toString() + "." + fileType;
    }

    @Transactional
    public List<Image> save(List<MultipartFile> files) {
        return files.stream().map(this::save).collect(Collectors.toList());
    }

    @Transactional
    public Image save(MultipartFile file) {
        Image entity = service.reserveId();
        String key = save(file, entity.getId());
        entity.setExternalId(key);
        return service.save(entity);
    }

    public String save(MultipartFile file, Long id) {
        try {
            File fileObj = convertMultiPartFileToFile(file);
            String key = generateKey(id, FilenameUtils.getExtension(file.getOriginalFilename()));
            s3Client.putObject(new PutObjectRequest(bucketName, prefix + key, fileObj));
            Files.delete(fileObj.toPath());
            return key;
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + file.getOriginalFilename() + ". Please try again!", e);
        }
    }

    public byte[] find(String key) {
        S3Object s3Object = s3Client.getObject(bucketName, prefix + key);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public void delete(String key) {
        try {
            s3Client.deleteObject(bucketName, prefix + key);
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + bucketName + "/" + prefix + key + ". Please try again!", e);
        }
    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new FileStorageException("Error converting multipartFile to file. Please try again!", e);
        }
        return convertedFile;
    }

    public void copy(String sourceKey, String destinationKey) {
        s3Client.copyObject(bucketName, sourceKey, bucketName, destinationKey);
    }

}
