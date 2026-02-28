package com.gurkan.yapp.ai.network

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class NetworkInstanceTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        // Re-initialise for each test to get a clean state
        NetworkInstance.init(
            ConfigModel(
                baseUrl = mockWebServer.url("/").toString(),
                connectTimeout = 5,
                readTimeout = 5,
                writeTimeout = 5
            )
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `get returns response body string`() = runTest {
        val expectedBody = """[{"title":"A New Hope"},{"title":"The Empire Strikes Back"}]"""
        mockWebServer.enqueue(MockResponse().setBody(expectedBody))

        val result = NetworkInstance.get("/films")

        assertEquals(expectedBody, result)
    }

    @Test
    fun `get sends request to correct path`() = runTest {
        mockWebServer.enqueue(MockResponse().setBody("{}"))

        NetworkInstance.get("/films")

        val request = mockWebServer.takeRequest()
        assertEquals("/films", request.path)
    }

    @Test
    fun `init throws when baseUrl is blank`() {
        assertThrows(IllegalArgumentException::class.java) {
            NetworkInstance.init(ConfigModel(baseUrl = ""))
        }
    }

    @Test
    fun `ConfigModel default timeouts are 30 seconds`() {
        val config = ConfigModel(baseUrl = "https://api.example.com/")
        assertEquals(30L, config.connectTimeout)
        assertEquals(30L, config.readTimeout)
        assertEquals(30L, config.writeTimeout)
    }

    @Test
    fun `ConfigModel custom timeouts are applied`() {
        val config = ConfigModel(
            baseUrl = "https://api.example.com/",
            connectTimeout = 10,
            readTimeout = 20,
            writeTimeout = 15
        )
        assertEquals(10L, config.connectTimeout)
        assertEquals(20L, config.readTimeout)
        assertEquals(15L, config.writeTimeout)
    }
}
