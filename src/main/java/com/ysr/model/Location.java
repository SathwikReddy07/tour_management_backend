package com.ysr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fromLocation;
    private String toLocation;
    private String country;
    private Double distance;
    private String description;
    private String estimatedTime;

    public Location() {}

    public Location(String fromLocation, String toLocation, String country, Double distance, String description, String estimatedTime) {
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.country = country;
        this.distance = distance;
        this.description = description;
        this.estimatedTime = estimatedTime;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getFromLocation() {
        return fromLocation;
    }
    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }
    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }

    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }
    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", fromLocation='" + fromLocation + '\'' +
                ", toLocation='" + toLocation + '\'' +
                ", country='" + country + '\'' +
                ", distance=" + distance +
                ", description='" + description + '\'' +
                ", estimatedTime='" + estimatedTime + '\'' +
                '}';
    }

}
