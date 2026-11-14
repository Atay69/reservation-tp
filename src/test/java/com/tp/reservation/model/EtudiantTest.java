package com.tp.reservation.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EtudiantTest {

    @Test
    void should_create_student_with_valid_id() {
        Etudiant e = new Etudiant("E001", "Dupont", "Marie");
        assertEquals("E001", e.getId());
        assertEquals("Dupont", e.getNom());
        assertEquals("Marie", e.getPrenom());
    }

    @Test
    void should_reject_empty_id() {
        assertThrows(IllegalArgumentException.class,
                () -> new Etudiant("", "Dupont", "Marie"));
    }

    @Test
    void should_reject_null_id() {
        assertThrows(IllegalArgumentException.class,
                () -> new Etudiant(null, "Dupont", "Marie"));
    }
}