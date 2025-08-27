package com.turfbooking.service;

import com.turfbooking.model.Transaction;
import com.turfbooking.model.Booking;
import com.turfbooking.repository.TransactionRepository;
import com.turfbooking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for Transaction entity operations
 * Handles payment processing, transaction management, and financial operations
 */
@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    /**
     * Create new transaction for booking
     */
    public Transaction createTransaction(Long bookingId, Transaction.PaymentMethod paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        
        Transaction transaction = new Transaction(booking, booking.getTotalAmount(), paymentMethod);
        return transactionRepository.save(transaction);
    }
    
    /**
     * Process payment (simulation)
     */
    @Transactional
    public Transaction processPayment(Long bookingId, Transaction.PaymentMethod paymentMethod, 
                                    String paymentDetails) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
        
        // Create transaction
        Transaction transaction = new Transaction(booking, booking.getTotalAmount(), paymentMethod);
        transaction.setPaymentStatus(Transaction.PaymentStatus.PENDING);
        transaction = transactionRepository.save(transaction);
        
        try {
            // Simulate payment processing
            boolean paymentSuccess = simulatePaymentGateway(transaction.getAmount(), paymentMethod, paymentDetails);
            
            if (paymentSuccess) {
                transaction.setPaymentStatus(Transaction.PaymentStatus.SUCCESS);
                transaction.setGatewayResponse("{\"status\":\"success\",\"message\":\"Payment processed successfully\"}");
                
                // Update booking status
                booking.setStatus(Booking.BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
            } else {
                transaction.setPaymentStatus(Transaction.PaymentStatus.FAILED);
                transaction.setGatewayResponse("{\"status\":\"failed\",\"message\":\"Payment processing failed\"}");
            }
            
        } catch (Exception e) {
            transaction.setPaymentStatus(Transaction.PaymentStatus.FAILED);
            transaction.setGatewayResponse("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Simulate payment gateway processing
     */
    private boolean simulatePaymentGateway(BigDecimal amount, Transaction.PaymentMethod method, String details) {
        // Simulate random success/failure for demo purposes
        // In real implementation, this would integrate with actual payment gateways
        return Math.random() > 0.1; // 90% success rate
    }
    
    /**
     * Process refund for cancelled booking
     */
    @Transactional
    public void processRefund(Long bookingId) {
        List<Transaction> transactions = transactionRepository.findByBookingId(bookingId);
        
        for (Transaction transaction : transactions) {
            if (transaction.getPaymentStatus() == Transaction.PaymentStatus.SUCCESS) {
                // Create refund transaction
                Transaction refundTransaction = new Transaction();
                refundTransaction.setBooking(transaction.getBooking());
                refundTransaction.setAmount(transaction.getAmount().negate()); // Negative amount for refund
                refundTransaction.setPaymentMethod(transaction.getPaymentMethod());
                refundTransaction.setPaymentStatus(Transaction.PaymentStatus.SUCCESS);
                refundTransaction.setGatewayResponse("{\"status\":\"refunded\",\"original_transaction\":\"" + 
                                                   transaction.getTransactionId() + "\"}");
                
                transactionRepository.save(refundTransaction);
                
                // Update original transaction status
                transaction.setPaymentStatus(Transaction.PaymentStatus.REFUNDED);
                transactionRepository.save(transaction);
            }
        }
    }
    
    /**
     * Get transaction by ID
     */
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    /**
     * Get transaction by transaction ID
     */
    public Optional<Transaction> getTransactionByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }
    
    /**
     * Get transactions for a booking
     */
    public List<Transaction> getTransactionsByBooking(Long bookingId) {
        return transactionRepository.findByBookingId(bookingId);
    }
    
    /**
     * Get user's transactions
     */
    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    /**
     * Get transactions by status
     */
    public List<Transaction> getTransactionsByStatus(Transaction.PaymentStatus status) {
        return transactionRepository.findByPaymentStatus(status);
    }
    
    /**
     * Get transactions by payment method
     */
    public List<Transaction> getTransactionsByPaymentMethod(Transaction.PaymentMethod method) {
        return transactionRepository.findByPaymentMethod(method);
    }
    
    /**
     * Get transactions in date range
     */
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Get successful transactions
     */
    public List<Transaction> getSuccessfulTransactions() {
        return transactionRepository.findSuccessfulTransactions();
    }
    
    /**
     * Get recent transactions
     */
    public List<Transaction> getRecentTransactions(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return transactionRepository.findRecentTransactions(pageable);
    }
    
    /**
     * Calculate total revenue
     */
    public Double getTotalRevenue() {
        return transactionRepository.calculateTotalRevenue();
    }
    
    /**
     * Calculate revenue for period
     */
    public Double getRevenueForPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.calculateRevenueForPeriod(startDate, endDate);
    }
    
    /**
     * Get transaction statistics by status
     */
    public List<Object[]> getTransactionStatsByStatus() {
        return transactionRepository.countTransactionsByStatus();
    }
    
    /**
     * Get transaction statistics by payment method
     */
    public List<Object[]> getTransactionStatsByPaymentMethod() {
        return transactionRepository.countTransactionsByPaymentMethod();
    }
    
    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
