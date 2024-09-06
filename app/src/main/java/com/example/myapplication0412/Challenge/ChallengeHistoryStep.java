package com.example.myapplication0412.Challenge;

public class ChallengeHistoryStep {
    long StepDate;
    int StepNumber;
    public ChallengeHistoryStep(long StepDate, int StepNumber) {
        this.StepDate = StepDate;
        this.StepNumber = StepNumber;
    }
    public long getStepDate() {  return StepDate;}
    public int getStepNumber() {    return StepNumber;}
}