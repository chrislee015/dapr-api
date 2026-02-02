package com.example.common.cqrs.bus;

import com.example.common.cqrs.CqrsMessage;

public interface CqrsBus {
    <R> R dispatch(CqrsMessage message);
}
