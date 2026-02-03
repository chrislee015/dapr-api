package com.example.api.application.pipeline;

import com.example.common.cqrs.Request;
import com.example.common.cqrs.pipeline.PipelineBehavior;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.function.Supplier;

public class ValidationBehavior implements PipelineBehavior<Request> {

    private final Validator validator;

    public ValidationBehavior(Validator validator) {
        this.validator = validator;
    }

    @Override
    @SuppressWarnings({"rawtypes","unchecked"})
    public Object apply(Request message, Supplier<Object> next) {
        Set<ConstraintViolation<Object>> violations = (Set) validator.validate(message);
        if (!violations.isEmpty()) throw new ConstraintViolationException((Set) violations);
        return next.get();
    }
}
