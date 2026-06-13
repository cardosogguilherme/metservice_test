package com.cardosogui.citybikesexplorer

import com.cardosogui.citybikesexplorer.data.remote.LocalJsonInterceptor
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalJsonInterceptorTest {

    @Test
    fun `maps top-level endpoint to a root asset file`() {
        assertEquals("stations.json", LocalJsonInterceptor.assetPathFor(listOf("stations")))
    }

    @Test
    fun `maps nested endpoint to a nested asset path`() {
        assertEquals(
            "stations/st-001/bikes.json",
            LocalJsonInterceptor.assetPathFor(listOf("stations", "st-001", "bikes")),
        )
    }

    @Test
    fun `strips the v2 api version prefix`() {
        assertEquals(
            "stations/st-001/bikes.json",
            LocalJsonInterceptor.assetPathFor(listOf("v2", "stations", "st-001", "bikes")),
        )
    }

    @Test
    fun `ignores empty segments from trailing slashes`() {
        assertEquals("stations.json", LocalJsonInterceptor.assetPathFor(listOf("stations", "")))
    }
}
