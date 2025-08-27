package com.turfbooking.repository;

import com.turfbooking.model.Tournament;
import com.turfbooking.model.Turf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Tournament entity operations
 * Handles tournament queries, registrations, and event management
 */
@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    
    // Find tournaments by status
    List<Tournament> findByStatusOrderByTournamentDateAsc(Tournament.TournamentStatus status);
    
    // Find upcoming tournaments
    @Query("SELECT t FROM Tournament t WHERE t.status = 'UPCOMING' AND t.tournamentDate >= :currentDate " +
           "ORDER BY t.tournamentDate ASC")
    List<Tournament> findUpcomingTournaments(@Param("currentDate") LocalDate currentDate);
    
    // Find tournaments by turf type
    List<Tournament> findByTurfTypeAndStatusOrderByTournamentDateAsc(Turf.TurfType turfType, 
                                                                    Tournament.TournamentStatus status);
    
    // Find tournaments by location
    @Query("SELECT t FROM Tournament t WHERE LOWER(t.location) LIKE LOWER(CONCAT('%', :location, '%')) " +
           "ORDER BY t.tournamentDate ASC")
    List<Tournament> findByLocationContainingIgnoreCase(@Param("location") String location);
    
    // Find tournaments with open registration
    @Query("SELECT t FROM Tournament t WHERE t.status = 'UPCOMING' AND " +
           "(:currentDate IS NULL OR t.registrationDeadline IS NULL OR t.registrationDeadline >= :currentDate) AND " +
           "t.registeredTeams < t.maxTeams " +
           "ORDER BY t.tournamentDate ASC")
    List<Tournament> findTournamentsWithOpenRegistration(@Param("currentDate") LocalDate currentDate);
    
    // Find tournaments by date range
    @Query("SELECT t FROM Tournament t WHERE t.tournamentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.tournamentDate ASC")
    List<Tournament> findTournamentsByDateRange(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);
    
    // Find tournaments with pagination
    Page<Tournament> findByStatusOrderByTournamentDateAsc(Tournament.TournamentStatus status, 
                                                         Pageable pageable);
    
    // Find popular tournaments (by registration count)
    @Query("SELECT t FROM Tournament t WHERE t.registeredTeams > 0 " +
           "ORDER BY t.registeredTeams DESC, t.tournamentDate ASC")
    List<Tournament> findPopularTournaments(Pageable pageable);
    
    // Find tournaments by prize money range
    @Query("SELECT t FROM Tournament t WHERE t.prizeMoney BETWEEN :minPrize AND :maxPrize " +
           "AND t.status = 'UPCOMING' ORDER BY t.prizeMoney DESC")
    List<Tournament> findTournamentsByPrizeRange(@Param("minPrize") java.math.BigDecimal minPrize,
                                                @Param("maxPrize") java.math.BigDecimal maxPrize);
    
    // Find tournaments by entry fee range
    @Query("SELECT t FROM Tournament t WHERE t.entryFee BETWEEN :minFee AND :maxFee " +
           "AND t.status = 'UPCOMING' ORDER BY t.entryFee ASC")
    List<Tournament> findTournamentsByEntryFeeRange(@Param("minFee") java.math.BigDecimal minFee,
                                                   @Param("maxFee") java.math.BigDecimal maxFee);
    
    // Count tournaments by status
    @Query("SELECT t.status, COUNT(t) FROM Tournament t GROUP BY t.status")
    List<Object[]> countTournamentsByStatus();
    
    // Count tournaments by turf type
    @Query("SELECT t.turfType, COUNT(t) FROM Tournament t WHERE t.status = 'UPCOMING' " +
           "GROUP BY t.turfType")
    List<Object[]> countUpcomingTournamentsByType();
    
    // Find tournaments ending registration soon
    @Query("SELECT t FROM Tournament t WHERE t.status = 'UPCOMING' AND " +
           "t.registrationDeadline IS NOT NULL AND " +
           "t.registrationDeadline BETWEEN :currentDate AND :endDate " +
           "ORDER BY t.registrationDeadline ASC")
    List<Tournament> findTournamentsWithRegistrationEndingSoon(@Param("currentDate") LocalDate currentDate,
                                                              @Param("endDate") LocalDate endDate);
}
