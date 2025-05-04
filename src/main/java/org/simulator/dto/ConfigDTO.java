package org.simulator.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.simulator.SimulatedQueue;

public class ConfigDTO {
  private Map<String, SimulatedQueue> queues;
  private List<NetworkDTO> network = new ArrayList<>();
  private Map<String, Double> arrivals;
  private List<Double> randomValues;
  private int randomCount;

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

  public int getRandomCount() {
    return randomCount;
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

  public void setRandomCount(int randomCount) {
    this.randomCount = randomCount;
  }
}
