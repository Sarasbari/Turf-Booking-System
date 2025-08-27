package com.turfbooking.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * TournamentRegistration entity representing team registrations for tournaments
 * Links users to tournaments with team details and payment status
 */
@Entity
@Table(name = "tournament_registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "tournament_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TournamentRegistration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "team_name", nullable = false, length = 100)
    private String teamName;
    
    @Column(name = "team_members", nullable = false, columnDefinition = "JSON")
    private String teamMembers;
    
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    public enum PaymentStatus {
        PENDING, PAID, FAILED
    }
    
    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
    }
    
    /**
     * Constructor for creating a new tournament registration
     */
    public TournamentRegistration(Tournament tournament, User user, String teamName, 
                                String teamMembers, String contactPhone) {
        this.tournament = tournament;
        this.user = user;
        this.teamName = teamName;
        this.teamMembers = teamMembers;
        this.contactPhone = contactPhone;
        this.paymentStatus = PaymentStatus.PENDING;
    }
}
