package com.example.spendwise.data.entity

import com.example.spendwise.viewmodel.Transaction

fun TransactionEntity.toTransaction(): Transaction = Transaction(
    id = id,
    title = title,
    amount = amount,
    type = type,
    date = date,
    notes = notes
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    title = title,
    amount = amount,
    type = type,
    date = date,
    notes = notes
)