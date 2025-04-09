package com.example.project3.rubank;

/**
 * The Campus enum represents different campuses of a university.
 * Each campus has a unique code.
 * @author Alison Chu, Byounguk Kim
 */
public enum Campus {
    NEW_BRUNSWICK("1"),
    NEWARK("2"),
    CAMDEN("3");

    private final String code;

    /**
     * Constructs a Campus with the specified code.
     * @param code the code of the campus
     */
    Campus(String code) {
        this.code = code;
    }

    /**
     * Returns the code of the campus.
     * @return the code of the campus
     */
    public String getCode() {
        return code;
    }

    /**
     * Converts an integer to a Campus.
     * @param code the integer code to convert
     * @return the corresponding Campus, or null if no match is found
     */
    public static Campus fromInt(int code) {
        switch(code){
            case 1: 
                return NEW_BRUNSWICK;
            case 2: 
                return NEWARK;
            case 3: 
                return CAMDEN;
            default: 
                return null;
        }
    }
}
