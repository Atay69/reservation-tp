package com.tp.reservation.service;
import org.mockito.MockedStatic;
import com.tp.reservation.exception.ReservationException;
import com.tp.reservation.model.Commande;
import com.tp.reservation.model.Etudiant;
import com.tp.reservation.model.Produit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceReservationTest {

    @InjectMocks
    private ServiceReservation service;

    @Mock
    private Etudiant mockEtudiant;

    @Mock
    private Produit mockProduit;

    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        // 1. Configure the Student mock
        lenient().when(mockEtudiant.getId()).thenReturn("E001");

        // 2. Configure the Product mock
        lenient().when(mockProduit.getId()).thenReturn("P001");
        lenient().when(mockProduit.getTarifJournalier()).thenReturn(new BigDecimal("10.00"));
        lenient().when(mockProduit.getCaution()).thenReturn(new BigDecimal("100.00"));

        // 3. Add the mocks to the service
        service.ajouterEtudiant(mockEtudiant);
        service.ajouterProduit(mockProduit);
        
        // 4. Add a second real student for overlap tests (RG08)
        service.ajouterEtudiant(new Etudiant("E002", "Martin", "Paul"));
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

    // --- Cycle 11: Mockito Verification ---
    @Test
    void should_use_both_mocks_for_reservation() {
        LocalDate start = LocalDate.now().plusDays(10);
        Commande c = service.reserver("E001", "P001", start, start.plusDays(4));

        assertNotNull(c);
        assertEquals(5, c.getDuree());
        assertEquals(0, c.getMontantTotal().compareTo(new BigDecimal("150.00")));

        // Verify that the service interacted with the Mocks!
        verify(mockEtudiant, times(1)).getId();
        verify(mockProduit, times(2)).getId();
        verify(mockProduit, times(1)).getTarifJournalier();
        verify(mockProduit, times(1)).getCaution();
    }
    @Test
    void should_allow_reservation_before_later_non_overlapping_reservation() {
    LocalDate laterStart = today.plusDays(20);
    LocalDate laterEnd = laterStart.plusDays(2);
    service.reserver("E001", "P001", laterStart, laterEnd);

    LocalDate earlierStart = today.plusDays(10);
    Commande earlier = service.reserver(
            "E002", "P001", earlierStart, earlierStart.plusDays(2));

    assertNotNull(earlier);
    assertEquals(earlierStart, earlier.getDateDebut());
    }
    @Test
    void should_assign_unique_references_to_successive_orders() {
    LocalDate firstDate = today.plusDays(10);
    LocalDate secondDate = today.plusDays(20);

    Commande first = service.reserver("E001", "P001", firstDate, firstDate);
    Commande second = service.reserver("E001", "P001", secondDate, secondDate);

    assertEquals("CMD-001", first.getReference());
    assertEquals("CMD-002", second.getReference());
    }
    @Test
    void should_not_register_rejected_third_order() {
    LocalDate firstDate = today.plusDays(10);
    LocalDate secondDate = today.plusDays(20);
    LocalDate rejectedDate = today.plusDays(30);

    service.reserver("E001", "P001", firstDate, firstDate);
    service.reserver("E001", "P001", secondDate, secondDate);

    assertThrows(ReservationException.class,
            () -> service.reserver("E001", "P001", rejectedDate, rejectedDate));

    Commande otherStudentOrder =
            service.reserver("E002", "P001", rejectedDate, rejectedDate);

    assertEquals("CMD-003", otherStudentOrder.getReference());
    }
    @Test
    void should_allow_reservation_starting_the_day_after_another_ends() {
    LocalDate firstStart = today.plusDays(10);
    LocalDate firstEnd = firstStart.plusDays(2);

    service.reserver("E001", "P001", firstStart, firstEnd);

    LocalDate nextStart = firstEnd.plusDays(1);
    Commande next = service.reserver("E002", "P001", nextStart, nextStart.plusDays(1));

    assertEquals(nextStart, next.getDateDebut());
    }
    @Test
    void should_allow_same_dates_for_different_products() {
    service.ajouterProduit(
            new Produit("P002", "Écran", new BigDecimal("5.00"), BigDecimal.ZERO));

    LocalDate start = today.plusDays(10);
    LocalDate end = start.plusDays(2);

    service.reserver("E001", "P001", start, end);
    Commande otherProductOrder = service.reserver("E002", "P002", start, end);

    assertEquals("P002", otherProductOrder.getIdProduit());
    }
    @Test
    void should_ignore_expired_orders_when_counting_quota() {
    LocalDate firstDate = today.plusDays(10);

    service.reserver("E001", "P001", firstDate, firstDate);
    service.reserver("E001", "P001", firstDate.plusDays(1), firstDate.plusDays(1));

    LocalDate simulatedToday = firstDate.plusDays(2);

    try (MockedStatic<LocalDate> mockedNow =
                 mockStatic(LocalDate.class, CALLS_REAL_METHODS)) {
        mockedNow.when(() -> LocalDate.now()).thenReturn(simulatedToday);

        Commande thirdOrder =
                service.reserver("E001", "P001", simulatedToday, simulatedToday);

        assertNotNull(thirdOrder);
    }
    }
}