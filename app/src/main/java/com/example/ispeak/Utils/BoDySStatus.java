package com.example.ispeak.Utils;

public enum BoDySStatus {
    PREFILL("Prefill"),
    EVALUATED("Evaluated"),
    SKIPPED("Skipped"),
    UNKNOWN("Unknown");

    private final String statusName;

    BoDySStatus(String statusName){
        this.statusName = statusName;
    }

    public String getStatusName(){
        return statusName;
    }

    public static BoDySStatus fromString(String colorString) {
        for (BoDySStatus status : BoDySStatus.values()) {
            if (status.getStatusName().equalsIgnoreCase(colorString)) {
                return status;
            }
        }
        return UNKNOWN;
    }

    public boolean isPrefill(){
        return this == PREFILL;
    }

    public boolean isEvaluated(){
        return this == EVALUATED;
    }

    public boolean isUnknown(){
        return this == UNKNOWN;
    }

    public boolean isSkipped(){
        return this == SKIPPED;
    }
}
