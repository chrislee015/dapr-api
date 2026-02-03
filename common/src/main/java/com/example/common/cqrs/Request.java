package com.example.common.cqrs;

public sealed interface Request extends IRequest permits Command, Query {
}
