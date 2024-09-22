package com.example.myapplication0412.Challenge;

public class ChallengeHistoryStep {
    private long stepDate;
    private int stepNumber;

    // No-argument constructor
    public ChallengeHistoryStep() {
    }

    // Parameterized constructor
    public ChallengeHistoryStep(long stepDate, int stepNumber) {
        this.stepDate = stepDate;
        this.stepNumber = stepNumber;
    }

    // Getters and setters
    public long getStepDate() {
        return stepDate;
    }

    public void setStepDate(long stepDate) {
        this.stepDate = stepDate;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }
}