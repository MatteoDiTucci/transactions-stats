package com.ditucci.matteo.infrastructure;

import application.DeleteTransactions;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;

@Controller
public class DeleteTransactionController {
    private final DeleteTransactions deleteTransactions;

    public DeleteTransactionController(DeleteTransactions deleteTransactions) {
        this.deleteTransactions = deleteTransactions;
    }

    @Delete(value = "/transactions")
    public HttpResponse<Void> delete() {
        deleteTransactions.delete();
        return HttpResponse.noContent();
    }
}
