package com.turfbooking.repository;

import com.turfbooking.model.TournamentRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TournamentRegistration entity operations
 * Handles tournament registration queries and team management
 */
@Repository
public interface TournamentRegistrationRepository extends JpaRepository<TournamentRegistration, Long> {
    
    // Find registrations by tournament
    List<TournamentRegistration> findByTournamentIdOrderByRegistrationDateAsc(Long tournamentId);
    
    // Find registrations by user
    List<TournamentRegistration> findByUserIdOrderByRegistrationDateDesc(Long userId);
    
    // Find registration by user and tournament
    Optional<TournamentRegistration> findByUserIdAndTournamentId(Long userId, Long tournamentId);
    
    // Find registrations by payment status
    List<TournamentRegistration> findByPaymentStatus(TournamentRegistration.PaymentStatus paymentStatus);
    
    // Find paid registrations for a tournament
    @Query("SELECT tr FROM TournamentRegistration tr WHERE tr.tournament.id = :tournamentId " +
           "AND tr.paymentStatus = 'PAID' ORDER BY tr.registrationDate ASC")
    List<TournamentRegistration> findPaidRegistrationsByTournament(@Param("tournamentId") Long tournamentId);
    
    // Count registrations by tournament
    @Query("SELECT COUNT(tr) FROM TournamentRegistration tr WHERE tr.tournament.id = :tournamentId")
    Long countRegistrationsByTournament(@Param("tournamentId") Long tournamentId);
    
    // Count paid registrations by tournament
    @Query("SELECT COUNT(tr) FROM TournamentRegistration tr WHERE tr.tournament.id = :tournamentId " +
           "AND tr.paymentStatus = 'PAID'")
    Long countPaidRegistrationsByTournament(@Param("tournamentId") Long tournamentId);
    
    // Check if user is registered for tournament
    @Query("SELECT COUNT(tr) > 0 FROM TournamentRegistration tr WHERE tr.user.id = :userId " +
           "AND tr.tournament.id = :tournamentId")
    boolean isUserRegisteredForTournament(@Param("userId") Long userId, @Param("tournamentId") Long tournamentId);
    
    // Find registrations by team name (case-insensitive)
    @Query("SELECT tr FROM TournamentRegistration tr WHERE LOWER(tr.teamName) LIKE LOWER(CONCAT('%', :teamName, '%'))")
    List<TournamentRegistration> findByTeamNameContainingIgnoreCase(@Param("teamName") String teamName);
    
    // Count registrations by payment status
    @Query("SELECT tr.paymentStatus, COUNT(tr) FROM TournamentRegistration tr GROUP BY tr.paymentStatus")
    List<Object[]> countRegistrationsByPaymentStatus();
    
    // Find recent registrations
    @Query("SELECT tr FROM TournamentRegistration tr ORDER BY tr.registrationDate DESC")
    List<TournamentRegistration> findRecentRegistrations(org.springframework.data.domain.Pageable pageable);
    
    // Find user's upcoming tournament registrations
    @Query("SELECT tr FROM TournamentRegistration tr WHERE tr.user.id = :userId " +
           "AND tr.tournament.tournamentDate >= :currentDate " +
           "AND tr.tournament.status IN ('UPCOMING', 'ONGOING') " +
           "ORDER BY tr.tournament.tournamentDate ASC")
    List<TournamentRegistration> findUserUpcomingTournaments(@Param("userId") Long userId,
                                                            @Param("currentDate") java.time.LocalDate currentDate);
}
