package org.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimulatedQueue {
  
  private String name = "";
  private int servers;
  private int capacity = 50;
  private double firstArrival = -1;

  private double arrivalMin = -1;
  private double arrivalMax = -1;
  private double exitMin;
  private double exitMax;

  private int customers;
  private int losses;
  private double[] times = new double[50];
  private List<Double> stateTimes = new ArrayList<>();

  private ArrayList<NextPassageQueue> nextQueues = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void in(){
    this.customers++;
  }

  public void out(){
    this.customers--;
  }

  public void loss(){
    this.losses++;
  }

  public void accumStateTime(double time){
    // if(stateTimes.size() == customers){
    //   stateTimes.add(customers, time);
    // }
    // else{
    //   stateTimes.set(customers, stateTimes.get(customers) + time);
    // }
    // stateTimes.add(customers, time);
    times[customers] += time;
  }

  public int Servers() {
    return servers;
  }

  public int Capacity() {
    return capacity;
  }

  public double getArrivalMin() {
    return arrivalMin;
  }

  public double getArrivalMax() {
    return arrivalMax;
  }

  public double getExitMin() {
    return exitMin;
  }

  public double getExitMax() {
    return exitMax;
  }

  public int Status() {
    return customers;
  }

  public int Losses() {
    return losses;
  }

  public double[] getTimes() {
    return times;
  }

  public List<Double> getStateTimes() {
    return stateTimes;
  }

  public List<NextPassageQueue> getNextQueues() {
    return this.nextQueues;
  }

  public void addNextQueue(SimulatedQueue queue, double probability) {
    this.nextQueues.add(new NextPassageQueue(queue, probability));
  }

  public double getFirstArrival() {
    return firstArrival;
  }

  public void setFirstArrival(double firstArrival) {
    this.firstArrival = firstArrival;
  }

  public void setServers(int servers) {
    this.servers = servers;
  }

  public void setCapacity(int capacity) {
    this.capacity = capacity > 0 ? capacity : 50;
    this.times = new double[capacity];
    // this.stateTimes = new ArrayList<>(capacity == -1 ? 10: capacity + 1);
  }

  public void setArrivalMin(double arrivalMin) {
    this.arrivalMin = arrivalMin;
  }

  public void setArrivalMax(double arrivalMax) {
    this.arrivalMax = arrivalMax;
  }

  public void setExitMin(double exitMin) {
    this.exitMin = exitMin;
  }

  public void setExitMax(double exitMax) {
    this.exitMax = exitMax;
  }

  public void setCustomers(int customers) {
    this.customers = customers;
  }

  public void setLosses(int losses) {
    this.losses = losses;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "SimulatedQueue" + name + " [firstArrival=" + firstArrival + "servers=" + servers + ", capacity=" + capacity + ", arrivalMin=" + arrivalMin
        + ", arrivalMax=" + arrivalMax + ", exitMin=" + exitMin + ", exitMax=" + exitMax + ", customers=" + customers
        + ", losses=" + losses + ", times=" + Arrays.toString(times) + nextQueues.toString()+"]";
  }

  
}
