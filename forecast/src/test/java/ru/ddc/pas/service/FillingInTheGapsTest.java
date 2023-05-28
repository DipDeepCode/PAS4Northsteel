package ru.ddc.pas.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FillingInTheGapsTest {

    @Autowired
    private FillingGapsService fillingInTheGapsInXTest;
    @Test
    public void testFillingTheGaps() {
        fillingInTheGapsInXTest.predict();
    }
}