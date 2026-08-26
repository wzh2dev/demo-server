package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AppTest {

    @Test
    void mainRunsSuccessfully() {
        assertDoesNotThrow(() -> App.main(new String[0]));
    }
}
