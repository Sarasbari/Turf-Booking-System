package com.turfbooking.repository;

import com.turfbooking.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Transaction entity operations
 * Handles payment transaction queries and financial reporting
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Find transaction by transaction ID
    Optional<Transaction> findByTransactionId(String transactionId);
    
    // Find transactions by booking
    List<Transaction> findByBookingId(Long bookingId);
    
    // Find transactions by payment status
    List<Transaction> findByPaymentStatus(Transaction.PaymentStatus paymentStatus);
    
    // Find transactions by payment method
    List<Transaction> findByPaymentMethod(Transaction.PaymentMethod paymentMethod);
    
    // Find transactions by user (through booking)
    @Query("SELECT t FROM Transaction t WHERE t.booking.user.id = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserId(@Param("userId") Long userId);
    
    // Find transactions in date range
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    // Find successful transactions
    @Query("SELECT t FROM Transaction t WHERE t.paymentStatus = 'SUCCESS' " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findSuccessfulTransactions();
    
    // Calculate total revenue from successful transactions
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.paymentStatus = 'SUCCESS'")
    Double calculateTotalRevenue();
    
    // Calculate revenue for date range
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.paymentStatus = 'SUCCESS' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueForPeriod(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    // Count transactions by status
    @Query("SELECT t.paymentStatus, COUNT(t) FROM Transaction t GROUP BY t.paymentStatus")
    List<Object[]> countTransactionsByStatus();
    
    // Count transactions by payment method
    @Query("SELECT t.paymentMethod, COUNT(t) FROM Transaction t WHERE t.paymentStatus = 'SUCCESS' " +
           "GROUP BY t.paymentMethod")
    List<Object[]> countTransactionsByPaymentMethod();
    
    // Find recent transactions
    @Query("SELECT t FROM Transaction t ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactions(org.springframework.data.domain.Pageable pageable);
}
