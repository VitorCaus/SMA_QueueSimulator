package org.simulator;

public class Event implements Comparable<Event> {
  private double time;
  private EventType type;
  private SimulatedQueue originQueue;
  private SimulatedQueue destinationQueue;

  public Event(double time, EventType type, SimulatedQueue originQueue, SimulatedQueue destinationQueue) {
    this.time = time;
    this.type = type;
    this.originQueue = originQueue;
    this.destinationQueue = destinationQueue;
  }

  public double getTime(){
    return time;
  }

  public EventType getType(){
    return type;
  }

  public SimulatedQueue getOriginQueue(){
    return originQueue;
  }

  public SimulatedQueue getDestinationQueue(){
    return destinationQueue;
  }
  
  public int compareTo(Event e){
    return Double.compare(this.time, e.time);
  }

  @Override
  public String toString() {
    return "Event{" +
            "time=" + time +
            ", type=" + type +
            ", originQueue=" + (originQueue != null ? originQueue.getName() : "null") +
            ", destinationQueue=" + (destinationQueue != null ? destinationQueue.getName() : "null") +
            '}';
  }


}
