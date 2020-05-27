package com.ditucci.matteo.infrastructure.controller;

import application.DeleteTransactions;
import com.ditucci.matteo.infrastructure.controllers.DeleteTransactionController;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DeleteTransactionControllerTest {
    private DeleteTransactionController controller;
    private DeleteTransactions deleteTransactions;

    @BeforeEach
    void setUp() {
        deleteTransactions = mock(DeleteTransactions.class);
        controller = new DeleteTransactionController(deleteTransactions);
    }

    @Test
    void returnNoContent() {
        HttpResponse<Void> response = controller.delete();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
    }

    @Test
    void callDeleteTransactionUseCase() {
        controller.delete();

        verify(deleteTransactions).delete();
    }
}