package com.turfbooking.service;

import com.turfbooking.model.Tournament;
import com.turfbooking.model.TournamentRegistration;
import com.turfbooking.model.User;
import com.turfbooking.repository.TournamentRepository;
import com.turfbooking.repository.TournamentRegistrationRepository;
import com.turfbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Tournament entity operations
 * Handles tournament management, registrations, and event coordination
 */
@Service
@Transactional
public class TournamentService {
    
    @Autowired
    private TournamentRepository tournamentRepository;
    
    @Autowired
    private TournamentRegistrationRepository registrationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get all tournaments
     */
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }
    
    /**
     * Get tournament by ID
     */
    public Optional<Tournament> getTournamentById(Long id) {
        return tournamentRepository.findById(id);
    }
    
    /**
     * Get tournaments by status
     */
    public List<Tournament> getTournamentsByStatus(Tournament.TournamentStatus status) {
        return tournamentRepository.findByStatusOrderByTournamentDateAsc(status);
    }
    
    /**
     * Get upcoming tournaments
     */
    public List<Tournament> getUpcomingTournaments() {
        return tournamentRepository.findUpcomingTournaments(LocalDate.now());
    }
    
    /**
     * Get tournaments by turf type
     */
    public List<Tournament> getTournamentsByTurfType(com.turfbooking.model.Turf.TurfType turfType) {
        return tournamentRepository.findByTurfTypeAndStatusOrderByTournamentDateAsc(
            turfType, Tournament.TournamentStatus.UPCOMING);
    }
    
    /**
     * Search tournaments by location
     */
    public List<Tournament> searchTournamentsByLocation(String location) {
        return tournamentRepository.findByLocationContainingIgnoreCase(location);
    }
    
    /**
     * Get tournaments with open registration
     */
    public List<Tournament> getTournamentsWithOpenRegistration() {
        return tournamentRepository.findTournamentsWithOpenRegistration(LocalDate.now());
    }
    
    /**
     * Get tournaments by date range
     */
    public List<Tournament> getTournamentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return tournamentRepository.findTournamentsByDateRange(startDate, endDate);
    }
    
    /**
     * Get tournaments with pagination
     */
    public Page<Tournament> getTournamentsWithPagination(Tournament.TournamentStatus status, 
                                                        int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return tournamentRepository.findByStatusOrderByTournamentDateAsc(status, pageable);
    }
    
    /**
     * Get popular tournaments
     */
    public List<Tournament> getPopularTournaments(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return tournamentRepository.findPopularTournaments(pageable);
    }
    
    /**
     * Create new tournament
     */
    public Tournament createTournament(Tournament tournament) {
        // Validate tournament data
        if (tournament.getTournamentDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Tournament date cannot be in the past");
        }
        
        if (tournament.getRegistrationDeadline() != null && 
            tournament.getRegistrationDeadline().isAfter(tournament.getTournamentDate())) {
            throw new RuntimeException("Registration deadline cannot be after tournament date");
        }
        
        if (tournament.getMaxTeams() <= 0) {
            throw new RuntimeException("Maximum teams must be greater than 0");
        }
        
        tournament.setStatus(Tournament.TournamentStatus.UPCOMING);
        tournament.setRegisteredTeams(0);
        
        return tournamentRepository.save(tournament);
    }
    
    /**
     * Update tournament
     */
    public Tournament updateTournament(Long id, Tournament tournamentDetails) {
        Tournament tournament = tournamentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tournament not found with id: " + id));
        
        // Update fields
        tournament.setName(tournamentDetails.getName());
        tournament.setDescription(tournamentDetails.getDescription());
        tournament.setLocation(tournamentDetails.getLocation());
        tournament.setTurfType(tournamentDetails.getTurfType());
        tournament.setTournamentDate(tournamentDetails.getTournamentDate());
        tournament.setStartTime(tournamentDetails.getStartTime());
        tournament.setEndTime(tournamentDetails.getEndTime());
        tournament.setEntryFee(tournamentDetails.getEntryFee());
        tournament.setPrizeMoney(tournamentDetails.getPrizeMoney());
        tournament.setMaxTeams(tournamentDetails.getMaxTeams());
        tournament.setRegistrationDeadline(tournamentDetails.getRegistrationDeadline());
        tournament.setRules(tournamentDetails.getRules());
        tournament.setContactInfo(tournamentDetails.getContactInfo());
        tournament.setImageUrl(tournamentDetails.getImageUrl());
        
        return tournamentRepository.save(tournament);
    }
    
    /**
     * Cancel tournament
     */
    public Tournament cancelTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tournament not found with id: " + id));
        
        if (tournament.getStatus() == Tournament.TournamentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed tournament");
        }
        
        tournament.setStatus(Tournament.TournamentStatus.CANCELLED);
        return tournamentRepository.save(tournament);
    }
    
    /**
     * Register team for tournament
     */
    @Transactional
    public TournamentRegistration registerTeam(Long tournamentId, Long userId, String teamName, 
                                             String teamMembers, String contactPhone) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
            .orElseThrow(() -> new RuntimeException("Tournament not found with id: " + tournamentId));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Check if registration is open
        if (!tournament.isRegistrationOpen()) {
            throw new RuntimeException("Registration is closed for this tournament");
        }
        
        // Check if tournament is full
        if (tournament.isFull()) {
            throw new RuntimeException("Tournament is full");
        }
        
        // Check if user is already registered
        if (registrationRepository.isUserRegisteredForTournament(userId, tournamentId)) {
            throw new RuntimeException("You are already registered for this tournament");
        }
        
        // Create registration
        TournamentRegistration registration = new TournamentRegistration(
            tournament, user, teamName, teamMembers, contactPhone);
        registration = registrationRepository.save(registration);
        
        // Update tournament registered teams count
        tournament.incrementRegisteredTeams();
        tournamentRepository.save(tournament);
        
        return registration;
    }
    
    /**
     * Cancel team registration
     */
    @Transactional
    public void cancelRegistration(Long registrationId, Long userId) {
        TournamentRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found with id: " + registrationId));
        
        // Check if user owns this registration
        if (!registration.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own registrations");
        }
        
        Tournament tournament = registration.getTournament();
        
        // Check if tournament has started
        if (tournament.getTournamentDate().isBefore(LocalDate.now()) || 
            tournament.getStatus() != Tournament.TournamentStatus.UPCOMING) {
            throw new RuntimeException("Cannot cancel registration for started or completed tournaments");
        }
        
        registrationRepository.delete(registration);
        
        // Update tournament registered teams count
        tournament.decrementRegisteredTeams();
        tournamentRepository.save(tournament);
    }
    
    /**
     * Get tournament registrations
     */
    public List<TournamentRegistration> getTournamentRegistrations(Long tournamentId) {
        return registrationRepository.findByTournamentIdOrderByRegistrationDateAsc(tournamentId);
    }
    
    /**
     * Get user's tournament registrations
     */
    public List<TournamentRegistration> getUserTournamentRegistrations(Long userId) {
        return registrationRepository.findByUserIdOrderByRegistrationDateDesc(userId);
    }
    
    /**
     * Get user's upcoming tournaments
     */
    public List<TournamentRegistration> getUserUpcomingTournaments(Long userId) {
        return registrationRepository.findUserUpcomingTournaments(userId, LocalDate.now());
    }
    
    /**
     * Update registration payment status
     */
    public TournamentRegistration updateRegistrationPaymentStatus(Long registrationId, 
                                                                TournamentRegistration.PaymentStatus status) {
        TournamentRegistration registration = registrationRepository.findById(registrationId)
            .orElseThrow(() -> new RuntimeException("Registration not found with id: " + registrationId));
        
        registration.setPaymentStatus(status);
        return registrationRepository.save(registration);
    }
    
    /**
     * Get tournament statistics
     */
    public List<Object[]> getTournamentStatsByStatus() {
        return tournamentRepository.countTournamentsByStatus();
    }
    
    /**
     * Get upcoming tournaments by type
     */
    public List<Object[]> getUpcomingTournamentsByType() {
        return tournamentRepository.countUpcomingTournamentsByType();
    }
    
    /**
     * Get tournaments ending registration soon
     */
    public List<Tournament> getTournamentsWithRegistrationEndingSoon(int days) {
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = currentDate.plusDays(days);
        return tournamentRepository.findTournamentsWithRegistrationEndingSoon(currentDate, endDate);
    }
    
    /**
     * Get tournaments by prize range
     */
    public List<Tournament> getTournamentsByPrizeRange(BigDecimal minPrize, BigDecimal maxPrize) {
        return tournamentRepository.findTournamentsByPrizeRange(minPrize, maxPrize);
    }
    
    /**
     * Get tournaments by entry fee range
     */
    public List<Tournament> getTournamentsByEntryFeeRange(BigDecimal minFee, BigDecimal maxFee) {
        return tournamentRepository.findTournamentsByEntryFeeRange(minFee, maxFee);
    }
}
