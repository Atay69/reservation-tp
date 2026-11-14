package com.tp.reservation.service;

import com.tp.reservation.exception.ReservationException;
import com.tp.reservation.model.Commande;
import com.tp.reservation.model.Etudiant;
import com.tp.reservation.model.Produit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ServiceReservationTest {

    private ServiceReservation service;
    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        service = new ServiceReservation();
        service.ajouterEtudiant(new Etudiant("E001", "Dupont", "Marie"));
        service.ajouterProduit(new Produit("P001", "Ordinateur", new BigDecimal("10.00"), new BigDecimal("100.00")));
    }

    // --- Cycle 4: Validation (RG01, RG02, RG03) ---
    @Test
    void should_reject_empty_student_id() {
        assertThrows(ReservationException.class, () -> 
            service.reserver("", "P001", today, today.plusDays(2)));
        assertThrows(ReservationException.class, () -> 
            service.reserver(null, "P001", today, today.plusDays(2)));
    }

    @Test
    void should_reject_empty_product_id() {
        assertThrows(ReservationException.class, () -> 
            service.reserver("E001", "", today, today.plusDays(2)));
        assertThrows(ReservationException.class, () -> 
            service.reserver("E001", null, today, today.plusDays(2)));
    }

    @Test
    void should_reject_null_dates() {
        assertThrows(ReservationException.class, () -> 
            service.reserver("E001", "P001", null, today.plusDays(2)));
        assertThrows(ReservationException.class, () -> 
            service.reserver("E001", "P001", today, null));
    }

    @Test
    void should_reject_past_start_date() {
        LocalDate yesterday = today.minusDays(1);
        assertThrows(ReservationException.class, () -> 
            service.reserver("E001", "P001", yesterday, today.plusDays(2)));
    }

    @Test
    void should_reject_end_date_before_start_date() {
        assertThrows(ReservationException.class, () -> 
            service.reserver("E001", "P001", today.plusDays(2), today.plusDays(1)));
    }

    // --- Cycle 5: Existence (RG05) ---
    @Test
    void should_reject_unknown_student() {
        assertThrows(ReservationException.class, () -> 
            service.reserver("E999", "P001", today, today.plusDays(2)));
    }

    @Test
    void should_reject_unknown_product() {
        assertThrows(ReservationException.class, () -> 
            service.reserver("E001", "P999", today, today.plusDays(2)));
    }

    // --- Cycle 6: Calculation (RG04, RG10, RG11, RG12) ---
    @Test
    void should_calculate_correct_amounts_for_5_days() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = start.plusDays(4);
        Commande c = service.reserver("E001", "P001", start, end);
        
        assertNotNull(c);
        assertEquals(5, c.getDuree());
        assertEquals(0, c.getMontantLocation().compareTo(new BigDecimal("50.00")));
        assertEquals(0, c.getCaution().compareTo(new BigDecimal("100.00")));
        assertEquals(0, c.getMontantTotal().compareTo(new BigDecimal("150.00")));
    }

    @Test
    void should_calculate_1_day_when_start_equals_end() {
        LocalDate start = LocalDate.now().plusDays(10);
        Commande c = service.reserver("E001", "P001", start, start);
        
        assertEquals(1, c.getDuree());
        assertEquals(0, c.getMontantLocation().compareTo(new BigDecimal("10.00")));
        assertEquals(0, c.getMontantTotal().compareTo(new BigDecimal("110.00")));
    }

    // --- Cycle 7: Max Duration (RG06) ---
    @Test
    void should_accept_exactly_7_days() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = start.plusDays(6);
        Commande c = service.reserver("E001", "P001", start, end);
        assertEquals(7, c.getDuree());
    }

    @Test
    void should_reject_more_than_7_days() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = start.plusDays(7);
        assertThrows(ReservationException.class, () -> 
            service.reserver("E001", "P001", start, end));
    }

    // --- Cycle 8: Student Quota (RG07) ---
    @Test
    void should_reject_third_active_order_for_same_student() {
        LocalDate d1 = LocalDate.now().plusDays(10);
        LocalDate d2 = LocalDate.now().plusDays(20);
        LocalDate d3 = LocalDate.now().plusDays(30);

        service.reserver("E001", "P001", d1, d1.plusDays(1));
        service.reserver("E001", "P001", d2, d2.plusDays(1));

        assertThrows(ReservationException.class, () -> 
            service.reserver("E001", "P001", d3, d3.plusDays(1)));
    }

    // --- Cycle 9: Availability (RG08) ---
    @Test
    void should_reject_overlapping_dates_for_same_product() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = start.plusDays(4);
        
        service.reserver("E001", "P001", start, end);
        
        assertThrows(ReservationException.class, () -> 
            service.reserver("E002", "P001", start.plusDays(2), end.plusDays(2)));
    }

    @Test
    void should_reject_overlap_on_boundary_dates() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = start.plusDays(4);
        
        service.reserver("E001", "P001", start, end);
        
        assertThrows(ReservationException.class, () -> 
            service.reserver("E002", "P001", end, end.plusDays(2)));
    }
}