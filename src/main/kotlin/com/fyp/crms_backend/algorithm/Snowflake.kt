package com.fyp.crms_backend.algorithm

class Snowflake(private val datacenterId: Long, private val machineId: Long) {
    private val epoch = 1735689600000L // 2025-01-01 00:00:00 UTC

    private val sequenceBits = 12L
    private val machineIdBits = 5L
    private val datacenterIdBits = 5L
    private val maxMachineId = (-1L).shl(machineIdBits.toInt()).inv()
    private val maxDatacenterId = (-1L).shl(datacenterIdBits.toInt()).inv()

    private val machineIdShift = sequenceBits
    private val datacenterIdShift = sequenceBits + machineIdBits
    private val timestampShift = sequenceBits + machineIdBits + datacenterIdBits

    private val sequenceMask = (-1L).shl(sequenceBits.toInt()).inv()

    private var lastTimestamp = -1L
    private var sequence = 0L

    init {
        require(!(machineId > maxMachineId || machineId < 0)) { "Machine ID invalid" }
        require(!(datacenterId > maxDatacenterId || datacenterId < 0)) { "Datacenter ID invalid" }
    }

    @Synchronized
    fun nextId(): Long {
        var timestamp = System.currentTimeMillis() - epoch
        if (timestamp < 0) {
            throw RuntimeException("Invalid system time before epoch")
        }
        if (timestamp > (1L shl 41) - 1) {
            throw RuntimeException("Timestamp overflow (69 years expired)")
        }

        if (timestamp < lastTimestamp) {
            throw RuntimeException("Clock moved backwards")
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) and sequenceMask
            if (sequence == 0L) {
                timestamp = tilNextMillis(lastTimestamp)
            }
        } else {
            sequence = 0L
        }
        lastTimestamp = timestamp
        return (timestamp shl timestampShift.toInt()) or
                (datacenterId shl datacenterIdShift.toInt()) or
                (machineId shl machineIdShift.toInt()) or
                sequence
    }

    private fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = System.currentTimeMillis()
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }
}