package com.PokeMeng.OldManGO.Challenge;

public class ChallengeHistoryStep {
    private int stepNumber;
    // No-argument constructor
    public ChallengeHistoryStep() {
    }
    // Parameterized constructor
    public ChallengeHistoryStep(int stepNumber) {
        this.stepNumber = stepNumber;
    }
    public int getStepNumber() {
        return stepNumber;
    }
}