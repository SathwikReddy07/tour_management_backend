package com.ysr.service;

import com.ysr.model.Location;
import com.ysr.repository.LocationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private LocationRepo locationRepo;
    @Autowired
    public void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    public Location addLocation(Location location) {
        return locationRepo.save(location);
    }

    public Location getLocationById(Integer id) {
        return locationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
    }

    public List<Location> getAllLocations() {
        return locationRepo.findAll();
    }

    public Location updateLocation(Integer id, Location location) {
        Location existingLocation = locationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        existingLocation.setFromLocation(location.getFromLocation());
        existingLocation.setToLocation(location.getToLocation());
        existingLocation.setCountry(location.getCountry());
        existingLocation.setDistance(location.getDistance());
        existingLocation.setDescription(location.getDescription());
        existingLocation.setEstimatedTime(location.getEstimatedTime());
        return locationRepo.save(existingLocation);
    }

    public void deleteLocation(Integer id) {
        if (!locationRepo.existsById(id)) {
            throw new RuntimeException("Location not found with id: " + id);
        }
        locationRepo.deleteById(id);
    }

}
