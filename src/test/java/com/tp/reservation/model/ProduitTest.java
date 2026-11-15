package com.tp.reservation.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProduitTest {

    @Test
    void should_create_product_with_valid_values() {
        Produit p = new Produit("P001", "Ordinateur", new BigDecimal("10.00"), new BigDecimal("100.00"));
        assertEquals("P001", p.getId());
        assertEquals("Ordinateur", p.getLibelle());
        assertEquals(new BigDecimal("10.00"), p.getTarifJournalier());
        assertEquals(new BigDecimal("100.00"), p.getCaution());
    }

    @Test
    void should_reject_zero_rate() {
        assertThrows(IllegalArgumentException.class,
                () -> new Produit("P001", "Ordinateur", BigDecimal.ZERO, new BigDecimal("100.00")));
    }

    @Test
    void should_reject_negative_rate() {
        assertThrows(IllegalArgumentException.class,
                () -> new Produit("P001", "Ordinateur", new BigDecimal("-5.00"), new BigDecimal("100.00")));
    }

    @Test
    void should_reject_negative_deposit() {
        assertThrows(IllegalArgumentException.class,
                () -> new Produit("P001", "Ordinateur", new BigDecimal("10.00"), new BigDecimal("-1.00")));
    }

    @Test
    void should_accept_zero_deposit() {
        Produit p = new Produit("P002", "Kit", new BigDecimal("5.00"), BigDecimal.ZERO);
        assertEquals(0, p.getCaution().compareTo(BigDecimal.ZERO));
    }
    @Test
    void should_reject_null_rate() {
    assertThrows(IllegalArgumentException.class,
            () -> new Produit("P001", "Ordinateur", null, new BigDecimal("100.00")));
    }

    @Test
    void should_reject_null_deposit() {
    assertThrows(IllegalArgumentException.class,
            () -> new Produit("P001", "Ordinateur", new BigDecimal("10.00"), null));
    }
}