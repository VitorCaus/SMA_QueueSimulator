package org.simulator;

public class NextPassageQueue implements Comparable<NextPassageQueue> {
  private SimulatedQueue nextQueue;
  private double probability;

  public NextPassageQueue(SimulatedQueue nextQueue, double probability) {
    this.nextQueue = nextQueue;
    this.probability = probability;
  }

  public SimulatedQueue getNextQueue() {
    return nextQueue;
  }
  
  public double getProbability() {
    return probability;
  }

  @Override
  public String toString() {
    return "NextPassageQueue [nextQueue=" + (nextQueue != null ? nextQueue.getName(): null) + ", probability=" + probability + "]";
  }

  @Override
  public int compareTo(NextPassageQueue o) {
    return Double.compare(this.probability, o.probability);
  }
}
