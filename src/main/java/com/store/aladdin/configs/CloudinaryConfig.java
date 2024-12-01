package com.store.aladdin.configs;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    
        @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dwfe5swye", // Replace with your Cloudinary cloud name
            "api_key", "319493579264318",       // Replace with your Cloudinary API key
            "api_secret", "AApO_205ctSQ_nz2yADzJPKQVLI"  // Replace with your Cloudinary API secret
        ));
    }

}
