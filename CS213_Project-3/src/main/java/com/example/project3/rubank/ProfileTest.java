package com.example.project3.rubank;//package com.example.project3.rubank;
//
//import org.junit.Before;
//import org.junit.Test;
//import util.Date;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * The ProfileTest class contains unit tests for the Profile class.
// * It tests the compareTo method to ensure profiles are compared correctly based on last name, first name, and date of birth.
// * The tests cover various scenarios including different last names, first names, and dates of birth.
// * @see Profile
// * @author Alison Chu, Byounguk Kim
// */
//public class ProfileTest {
//
//    private Profile pA;
//    private Profile pB;
//    private Profile pC;
//    private Profile pD;
//    private Profile pE;
//
//    /**
//     * Sets up the test profiles before each test.
//     * Initializes profiles with different names and dates of birth for comparison.
//     */
//    @Before
//    public void setUP(){
//        pA = new Profile("John", "Doe", new Date("2/19/2000"));
//        pB = new Profile("Kate", "Lindsey", new Date("8/31/2001"));
//        pC = new Profile("John", "Doe", new Date("7/8/1999"));
//        pD = new Profile("john", "doe", new Date("2/19/2000"));
//        pE = new Profile("April", "Doe", new Date("2/19/2000"));
//    }
//
//    /**
//     * Tests that pA is less than pB based on last name comparison.
//     */
//    @Test
//    public void testCompareToLastNameLess() {
//
//        assertEquals(-1, pA.compareTo(pB));
//    }
//
//    /**
//     * Tests that pB is greater than pA based on last name comparison.
//     */
//    @Test
//    public void testCompareToLastNameGreater() {
//
//        assertEquals( 1, pB.compareTo(pA));
//    }
//
//    /**
//     * Tests that pE is less than pA based on first name comparison.
//     */
//    @Test
//    public void testCompareToFirstNameLess() {
//
//        assertEquals( -1, pE.compareTo(pA));
//    }
//
//    /**
//     * Tests that pA is greater than pE based on first name comparison.
//     */
//    @Test
//    public void testCompareToFirstNameGreater() {
//
//        assertEquals(1, pA.compareTo(pE));
//    }
//
//    /**
//     * Tests that pC is less than pA based on date of birth comparison.
//     */
//    @Test
//    public void testCompareToDOBEarlier() {
//
//        assertEquals(-1, pC.compareTo(pA));
//    }
//
//    /**
//     * Tests that pA is greater than pC based on date of birth comparison.
//     */
//    @Test
//    public void testCompareToDOBLater() {
//
//        assertEquals( 1, pA.compareTo(pC));
//    }
//
//    /**
//     * Tests that pA is equal to pD ignoring case in names and same date of birth.
//     */
//    @Test
//    public void testCompareToEqualIgnoreCase() {
//
//        assertEquals(0, pA.compareTo(pD));
//    }
//}