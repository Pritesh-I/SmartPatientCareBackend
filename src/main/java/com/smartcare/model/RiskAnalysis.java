package com.smartcare.model;

import java.util.List;

public class RiskAnalysis {

    private String riskLevel;
    private List<String> alerts;
    private String recommendation;

    public RiskAnalysis() {
    }

    public RiskAnalysis(String riskLevel, List<String> alerts, String recommendation) {
        this.riskLevel = riskLevel;
        this.alerts = alerts;
        this.recommendation = recommendation;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<String> alerts) {
        this.alerts = alerts;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
