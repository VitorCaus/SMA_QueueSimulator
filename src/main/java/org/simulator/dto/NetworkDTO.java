package org.simulator.dto;

public class NetworkDTO {
  private String origin;
  private String destination;
  private double probability;

  public String getOrigin() {
    return origin;
  }

  public String getDestination() {
    return destination;
  }

  public double getProbability() {
    return probability;
  }

  public void setOrigin(String origin) {
    this.origin = origin;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public void setProbability(double probability) {
    this.probability = probability;
  }

  
}
