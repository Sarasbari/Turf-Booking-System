package com.turfbooking.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Tournament entity representing tournaments organized at various turfs
 * Contains tournament details, registration information, and status
 */
@Entity
@Table(name = "tournaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tournament {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 150)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, length = 200)
    private String location;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "turf_type", nullable = false)
    private Turf.TurfType turfType;
    
    @Column(name = "tournament_date", nullable = false)
    private LocalDate tournamentDate;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "entry_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal entryFee;
    
    @Column(name = "prize_money", precision = 10, scale = 2)
    private BigDecimal prizeMoney;
    
    @Column(name = "max_teams", nullable = false)
    private Integer maxTeams;
    
    @Column(name = "registered_teams")
    private Integer registeredTeams = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status = TournamentStatus.UPCOMING;
    
    @Column(name = "registration_deadline")
    private LocalDate registrationDeadline;
    
    @Column(columnDefinition = "TEXT")
    private String rules;
    
    @Column(name = "contact_info", length = 200)
    private String contactInfo;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TournamentRegistration> registrations;
    
    public enum TournamentStatus {
        UPCOMING, ONGOING, COMPLETED, CANCELLED
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Checks if registration is still open
     */
    public boolean isRegistrationOpen() {
        if (registrationDeadline == null) {
            return status == TournamentStatus.UPCOMING;
        }
        return status == TournamentStatus.UPCOMING && 
               LocalDate.now().isBefore(registrationDeadline) &&
               registeredTeams < maxTeams;
    }
    
    /**
     * Checks if tournament is full
     */
    public boolean isFull() {
        return registeredTeams >= maxTeams;
    }
    
    /**
     * Increments registered teams count
     */
    public void incrementRegisteredTeams() {
        if (registeredTeams < maxTeams) {
            registeredTeams++;
        }
    }
    
    /**
     * Decrements registered teams count
     */
    public void decrementRegisteredTeams() {
        if (registeredTeams > 0) {
            registeredTeams--;
        }
    }
}
