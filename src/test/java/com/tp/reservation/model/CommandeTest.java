package com.tp.reservation.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class CommandeTest {

    @Test
    void should_create_order_with_all_details() {
        LocalDate debut = LocalDate.of(2024, 12, 10);
        LocalDate fin = LocalDate.of(2024, 12, 14);
        
        Commande c = new Commande(
                "CMD-001", "E001", "P001", 
                debut, fin, 5, 
                new BigDecimal("50.00"), 
                new BigDecimal("100.00"), 
                new BigDecimal("150.00")
        );

        assertEquals("CMD-001", c.getReference());
        assertEquals("E001", c.getIdEtudiant());
        assertEquals("P001", c.getIdProduit());
        assertEquals(debut, c.getDateDebut());
        assertEquals(fin, c.getDateFin());
        assertEquals(5, c.getDuree());
        assertEquals(0, c.getMontantLocation().compareTo(new BigDecimal("50.00")));
        assertEquals(0, c.getCaution().compareTo(new BigDecimal("100.00")));
        assertEquals(0, c.getMontantTotal().compareTo(new BigDecimal("150.00")));
    }
}