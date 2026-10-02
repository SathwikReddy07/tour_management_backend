package com.ysr.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty())
            throw  new IllegalArgumentException("File is empty");
        Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) uploadResult.get("secure_url");
        return imageUrl;
    }

    public String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty())
            throw new IllegalArgumentException("Image URL is empty");
        String[] urlArray = imageUrl.split("/");
        for (int i = 0; i < urlArray.length - 1; i++) {
            if (urlArray[i].equals("upload") && i+2 < urlArray.length) {
                String publicId = urlArray[i + 2];
                Integer dotIndex = publicId.lastIndexOf('.');
                publicId = dotIndex > 0 ? publicId.substring(0, dotIndex) : publicId;
                return publicId;
            }
        }
        return null;
    }

    public String updateImage(String oldUrl, MultipartFile newFile) throws IOException {
        if (newFile.isEmpty())
            throw new IllegalArgumentException("New file is empty");
        if (oldUrl != null && !oldUrl.trim().isEmpty()) {
            String publicId = extractPublicId(oldUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        }
        return uploadImage(newFile);
    }

    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.trim().isEmpty())
            return;
        String publicId = extractPublicId(imageUrl);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

}
