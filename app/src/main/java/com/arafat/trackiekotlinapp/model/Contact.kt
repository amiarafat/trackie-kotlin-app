package com.arafat.trackiekotlinapp.model

class Contact{

    private lateinit var name : String
    private lateinit var number : String

    private var status: Int = 0

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getNumber(): String {
        return number
    }

    fun setNumber(number: String) {
        this.number = number
    }

    fun isStatus(): Int {
        return status
    }

    fun setStatus(status: Int) {
        this.status = status
    }
}