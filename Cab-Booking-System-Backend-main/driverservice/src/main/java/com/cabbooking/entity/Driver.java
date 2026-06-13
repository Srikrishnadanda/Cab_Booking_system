package com.cabbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "driver")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Driver {

    @Id
    @Column(name = "driver_id", columnDefinition = "CHAR(36)")
    private String driverId = UUID.randomUUID().toString();

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String vehicleNumber;

    @Column(nullable = false)
    private String vehicleName;

    @Column(nullable = false)
    private String licenceNumber;

    @Column(nullable = false)
    private String carSeater;

    private String password;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private double rating = 0.0;

    @Column(nullable = false)
    private boolean isAvailable = false;
    

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private Timestamp deletedAt;

}
