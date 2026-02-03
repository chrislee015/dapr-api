package com.example.api.application.pipeline;

import com.example.common.cqrs.Request;
import com.example.common.cqrs.Query;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

public class TransactionBehavior implements PipelineBehavior<Request> {

    private final PlatformTransactionManager txManager;

    public TransactionBehavior(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    @Override
    public Object apply(Request message, Supplier<Object> next) {
        if (message instanceof Query<?>) return next.get();
        return new TransactionTemplate(txManager).execute(status -> next.get());
    }
}
