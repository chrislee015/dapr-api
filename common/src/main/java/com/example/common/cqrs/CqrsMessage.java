package com.example.common.cqrs;

public sealed interface CqrsMessage permits Command, Query {
}
