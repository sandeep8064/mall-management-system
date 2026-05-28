package com.mall.productcatalog.changelog;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;

@ChangeUnit(id = "init", order = "001", author = "system")
public class DatabaseChangeLog {

    @Execution
    public void execution() {
        // Initialization logic
    }

    @RollbackExecution
    public void rollbackExecution() {
        // Rollback logic
    }
}
