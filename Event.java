public class Event implements Comparable<Event> {
  private double time;
  private EventType type;

  public Event(double time, EventType type){
    this.time = time;
    this.type = type;
  }

  public double getTime(){
    return time;
  }

  public EventType getType(){
    return type;
  }
  
  public int compareTo(Event e){
    return Double.compare(this.time, e.time);
  }

  @Override
  public String toString() {
    return String.format("Event: [time: %.4f | type: %s]", this.time, this.type);
  }
}
