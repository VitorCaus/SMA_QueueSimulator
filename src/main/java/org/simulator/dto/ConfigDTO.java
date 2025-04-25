package org.simulator.dto;

import java.util.List;
import java.util.Map;

import org.simulator.SimulatedQueue;

public class ConfigDTO {
  private Map<String, SimulatedQueue> queues;
  private List<NetworkDTO> network;
  private Map<String, Double> arrivals;
  private List<Double> randomValues;
  private int randomPerSeed;

  public Map<String, SimulatedQueue> getQueues() {
    return queues;
  }

  public List<NetworkDTO> getNetwork() {
    return network;
  }

  public Map<String, Double> getArrivals() {
    return arrivals;
  }

  public List<Double> getRandomValues() {
    return randomValues;
  }

  public int getRandomPerSeed() {
    return randomPerSeed;
  }

  public void setQueues(Map<String, SimulatedQueue> queues) {
    this.queues = queues;
  }

  public void setNetwork(List<NetworkDTO> network) {
    this.network = network;
  }

  public void setArrivals(Map<String, Double> arrivals) {
    this.arrivals = arrivals;
  }

  public void setRandomValues(List<Double> randomValues) {
    this.randomValues = randomValues;
  }

  public void setRandomPerSeed(int randomPerSeed) {
    this.randomPerSeed = randomPerSeed;
  }
}
