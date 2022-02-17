package com.roman.persistance.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "address")
class Address(
    @Id
    @Column
    val address: String,

    @Column
    val privateKey: ByteArray,
)