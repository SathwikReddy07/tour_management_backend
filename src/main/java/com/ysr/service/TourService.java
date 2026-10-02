package com.ysr.service;

import com.ysr.exception.LocationNotFoundException;
import com.ysr.exception.LodgingNotFoundException;
import com.ysr.exception.TourNotFoundException;
import com.ysr.exception.TransportNotFoundException;
import com.ysr.model.Location;
import com.ysr.model.Lodging;
import com.ysr.model.Tours;
import com.ysr.model.Transport;
import com.ysr.repository.LocationRepo;
import com.ysr.repository.LodgingRepo;
import com.ysr.repository.TourRepo;
import com.ysr.repository.TransportRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TourService {

    private TourRepo tourRepo;
    @Autowired
    public void setTourRepo(TourRepo tourRepo) {
        this.tourRepo = tourRepo;
    }

    private LocationRepo locationRepo;
    @Autowired
    public void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    private LodgingRepo lodgingRepo;
    @Autowired
    public void setLodgingRepo(LodgingRepo lodgingRepo) {
        this.lodgingRepo = lodgingRepo;
    }

    private TransportRepo transportRepo;
    @Autowired
    public void setTransportRepo(TransportRepo transportRepo) {
        this.transportRepo = transportRepo;
    }

    private CloudinaryService cloudinaryService;
    @Autowired
    public void setCloudinaryService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    public Tours addTour(Tours tour, List<MultipartFile> images,
                         Integer locationId, Integer transportId, Integer lodgingId) throws IOException {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        Lodging lodging = lodgingRepo.findById(lodgingId)
                .orElseThrow(() -> new LodgingNotFoundException("Lodging not found with id: " + lodgingId));
        Transport transport = transportRepo.findById(transportId)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found with id: " + transportId));
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                throw new IOException("Image is empty");
            }
            String imageUrl = cloudinaryService.uploadImage(image);
            imageUrls.add(imageUrl);
        }
        tour.setImages(imageUrls);
        tour.setLocation(location);
        tour.setLodging(lodging);
        tour.setTransport(transport);
        return tourRepo.save(tour);
    }

    public List<Tours> getAllTours() {
        return tourRepo.getAllToursWithDetails();
    }

    public Tours getTourById(Integer tourId) {
        Tours tour = tourRepo.getTourByIdWithDetails(tourId);
        if (tour == null)
            throw new TourNotFoundException("Tour not found with id: " + tourId);
        return tour;
    }

    public void deleteTourById(Integer tourId) {
        Tours tour = tourRepo.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        tourRepo.delete(tour);
    }

    public Tours updateTourById(Integer tourId, Tours tour, List<MultipartFile> images,
                                Integer locationId, Integer transportId, Integer lodgingId) throws IOException {
        Tours existingTour = tourRepo.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + locationId));
        Lodging lodging = lodgingRepo.findById(lodgingId)
                .orElseThrow(() -> new LodgingNotFoundException("Lodging not found with id: " + lodgingId));
        Transport transport = transportRepo.findById(transportId)
                .orElseThrow(() -> new TransportNotFoundException("Transport not found with id: " + transportId));
        List<String> imageUrls = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            imageUrls = existingTour.getImages();
        } else {
            for (MultipartFile image : images) {
                if (image.isEmpty()) {
                    throw new IOException("Image is empty");
                }
                String imageUrl = cloudinaryService.uploadImage(image);
                imageUrls.add(imageUrl);
            }
        }
        existingTour.setName(tour.getName());
        existingTour.setDescription(tour.getDescription());
        existingTour.setGuide(tour.getGuide());
        existingTour.setStartDate(tour.getStartDate());
        existingTour.setEndDate(tour.getEndDate());
        existingTour.setPrice(tour.getPrice());
        existingTour.setTicketsAvailable(tour.getTicketsAvailable());
        existingTour.setMeals(tour.getMeals());
        existingTour.setActivities(tour.getActivities());
        existingTour.setImages(imageUrls);
        existingTour.setLocation(location);
        existingTour.setLodging(lodging);
        existingTour.setTransport(transport);
        return tourRepo.save(existingTour);
    }

}
