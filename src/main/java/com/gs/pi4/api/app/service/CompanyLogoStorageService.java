package com.gs.pi4.api.app.service;

import org.springframework.stereotype.Service;

@Service
public class CompanyLogoStorageService  extends StorageService {
    
    private static final String PREFIX = "images/company";

    public CompanyLogoStorageService() {
        super(PREFIX);
    }
}
