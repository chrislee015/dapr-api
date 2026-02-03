package com.example.common.cqrs.bus;

import com.example.common.cqrs.Request;

public interface CqrsBus {
    <R> R dispatch(Request message);
}
