package com.ysr.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tours")
public class Tours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String guide;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double price;
    private Integer ticketsAvailable;

    @ElementCollection
    @CollectionTable(
            name = "tour_meals",
            joinColumns = @JoinColumn(name = "tour_id")
    )
    @Column(name = "meal")
    private List<String> meals = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "tour_activities",
            joinColumns = @JoinColumn(name = "tour_id")
    )
    @Column(name = "activity")
    private List<String> activities = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "tour_images",
            joinColumns = @JoinColumn(name = "tour_id")
    )
    @Column(name = "image")
    private List<String> images = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @OneToOne
    @JoinColumn(name = "lodging_id", referencedColumnName = "id")
    private Lodging lodging;

    @OneToOne
    @JoinColumn(name = "transport_id", referencedColumnName = "id")
    private Transport transport;

    public Tours() {
    }

    public Tours(String name, String description, String guide, LocalDate startDate, LocalDate endDate,
                 Double price, Integer ticketsAvailable, List<String> meals, List<String> activities, List<String> images,
                 Location location, Lodging lodging, Transport transport) {
        this.name = name;
        this.description = description;
        this.guide = guide;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.ticketsAvailable = ticketsAvailable;
        this.meals = meals;
        this.activities = activities;
        this.images = images;
        this.location = location;
        this.lodging = lodging;
        this.transport = transport;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getGuide() {
        return guide;
    }
    public void setGuide(String guide) {
        this.guide = guide;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getTicketsAvailable() {
        return ticketsAvailable;
    }
    public void setTicketsAvailable(Integer ticketsAvailable) {
        this.ticketsAvailable = ticketsAvailable;
    }

    public List<String> getMeals() {
        return meals;
    }
    public void setMeals(List<String> meals) {
        this.meals = meals;
    }

    public List<String> getActivities() {
        return activities;
    }
    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public List<String> getImages() {
        return images;
    }
    public void setImages(List<String> images) {
        this.images = images;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public Lodging getLodging() {
        return lodging;
    }
    public void setLodging(Lodging lodging) {
        this.lodging = lodging;
    }

    public Transport getTransport() {
        return transport;
    }
    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    @Override
    public String toString() {
        return "Tours{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", guide='" + guide + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", price=" + price +
                ", ticketsAvailable=" + ticketsAvailable +
                ", meals=" + meals +
                ", activities=" + activities +
                ", images=" + images +
                ", location=" + location +
                ", lodging=" + lodging +
                ", transport=" + transport +
                '}';
    }

}