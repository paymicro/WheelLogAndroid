package com.cooper.wheellog.utils

import com.cooper.wheellog.WheelData
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.round

class GotwayAdapterTest {

    private var adapter: GotwayAdapter = GotwayAdapter()
    private var header = byteArrayOf(0x55, 0xAA.toByte())

    @Before
    @Throws(Exception::class)
    fun setUp() {
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }

    @Test
    fun `decode with corrupted data 1-30 units`() {
        // Arrange.
        var byteArray = byteArrayOf()
        for (i in 0..29) {
            byteArray += i.toByte()

            // Act.
            var result = adapter.decode(byteArray)

            // Assert.
            assertThat(result).isFalse()
        }
    }

    @Test
    fun `decode with normal data`() {
        // Arrange.
        WheelData.initiate()
        var data = WheelData.getInstance()
        var voltage = 6000.toShort()
        var voltageBytes = ByteBuffer.allocate(2).putShort(voltage).array()
        var speed = 111.toShort()
        var speedBytes = ByteBuffer.allocate(2).putShort(speed).array()
        var temperature = 99.toShort()
        var temperatureBytes = ByteBuffer.allocate(2).putShort(temperature).array()
        var distance = 321.toShort()
        var distanceBytes = ByteBuffer.allocate(2).putShort(distance).array()
        var byteArray = header +
                voltageBytes +
                speedBytes +
                byteArrayOf(6, 7) +
                distanceBytes +
                byteArrayOf(10, 11) +
                temperatureBytes +
                byteArrayOf(15, 16, 17, 18, 0, 0);

        // Act.
        var result = adapter.decode(byteArray)

        // Assert.
        assertThat(result).isTrue()
        var speedInKm = round(speed * 3.6 / 10).toInt();
        assertThat(abs(data.speed)).isEqualTo(speedInKm)
        var tempInCelcius = round(((temperature - 32) * 5.0 / 9.0)).toInt();
        assertThat(data.temperature).isEqualTo(tempInCelcius)
        assertThat(data.temperature2).isEqualTo(tempInCelcius)
        assertThat(data.wheelDistanceDouble).isEqualTo(distance / 1000.0)
        assertThat(data.voltage).isEqualTo(voltage)
        assertThat(data.batteryLevel).isEqualTo(54)
    }
}