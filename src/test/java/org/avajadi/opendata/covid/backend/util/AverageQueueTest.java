package org.avajadi.opendata.covid.backend.util;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AverageQueueTest {
    int queueSize = 5;
    AverageQueue ut;
    double[] longList = new double[]{3, 1, 2, 4, 5, 8, 6, 2};
    double[] averages = new double[]{3.0, 2.0, 2.0, 2.5, 3.0, 4.0, 5.0, 5.0};

    @BeforeTest
    public void setup() {
        ut = new AverageQueue(queueSize);
    }

    @Test
    public void isOverFlowHandledProperly() {
        System.out.println("ut.size: " + ut.size());
        //assertEquals( ut.size(), 0);
        for (int i = 0; i < longList.length; i++) {
            ut.add(longList[i]);
        }
        assertEquals(ut.size(),queueSize);
        assertEquals( ut.average(),5.0);
    }

    @Test
    public void isAverageCorrect() {
        for (int i = 0; i < longList.length; i++) {
            ut.add(longList[i]);
            assertEquals( ut.average(),averages[i]);
          //  assertEquals(ut.size(),Math.min(i, queueSize));
        }

    }

}