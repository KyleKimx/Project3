package com.example.project3.util;//package com.example.project3.util;
//
//import org.junit.Test;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
///**
// * The DateTest class contains unit tests for the Date class.
// * It tests various scenarios to ensure the Date class methods work correctly.
// * The tests include validation of months, days, leap years, and valid dates.
// * Uses JUnit framework for testing.
// * @author Alison Chu, Byounguk Kim
// */
//public class DateTest {
//
//    /**
//     * Tests if a date with month zero is invalid.
//     */
//    @Test
//    public void testInvalidMonthZero(){
//        Date date = new Date("0/15/2000");
//        assertFalse(date.isValid());
//    }
//
//
//    /**
//     * Tests if a date with month thirteen is invalid.
//     */
//    @Test
//    public void testInvalidMonthThirteen(){
//        Date date = new Date("13/15/2020");
//        assertFalse(date.isValid());
//    }
//
//
//    /**
//     * Tests if a date with day thirty-one in a month with thirty days is invalid.
//     */
//    @Test
//    public void testInvalidDayInNonFebMonth(){
//        Date date = new Date("4/31/2023");
//        assertFalse(date.isValid());
//    }
//
//
//    /**
//     * Tests if a date with day twenty-nine in February of a non-leap year is invalid.
//     */
//    @Test
//    public void testDaysInFeb_NonLeap(){
//        Date date = new Date("2/29/2023");
//        assertFalse(date.isValid());
//    }
//
//    /**
//     * Tests if a date with day twenty-nine in February of a leap year is valid.
//     */
//    @Test
//    public void testDaysInFeb_Leap(){
//        Date date = new Date("2/29/2020");
//        assertTrue(date.isValid());
//    }
//
//
//    /**
//     * Tests if a date with a valid day in a non-February month is valid.
//     */
//    @Test
//    public void testValidDayInNonFebMonth(){
//        Date date = new Date("7/4/1999");
//        assertTrue(date.isValid());
//    }
//}