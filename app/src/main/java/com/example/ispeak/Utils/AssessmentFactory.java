package com.example.ispeak.Utils;

import com.example.ispeak.Models.Assessment;
import com.example.ispeak.Models.BoDyS;

public class AssessmentFactory {

    public enum AssessmentNames {
        BoDyS, Assessment1
    }

    public static Assessment createAssessment(AssessmentNames assessment) {
        switch (assessment) {
            case BoDyS:
                return new BoDyS();
            default:
                return null;
        }
    }

}
