package com.machimpyo.dot.data.model

import java.time.LocalDate
import java.time.ZoneId

sealed class ExitState(val value: Int) {
    object IsNotAssigned: ExitState(0)
    object BeforeExit: ExitState(1)
    object ExitDay: ExitState(2)
    object AfterExit: ExitState(3)

    companion object {
        fun fromValue(value: Int): ExitState {
            return when(value) {
                1 -> BeforeExit
                2 -> ExitDay
                3 -> AfterExit
                else -> IsNotAssigned
            }
        }

        fun fromExitDate(exitDate: LocalDate?): ExitState {
            exitDate ?: return IsNotAssigned
            val today = LocalDate.now(ZoneId.systemDefault())
            return if(today == exitDate) ExitDay
            else if (today < exitDate) BeforeExit
            else AfterExit
        }

    }
}
