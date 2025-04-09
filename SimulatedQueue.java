public class SimulatedQueue {
  
  private int servers;
  private int capacity;

  private double arrivalMin;
  private double arrivalMax;
  private double exitMin;
  private double exitMax;

  private int customers = 0;
  private int losses = 0;
  private double[] times;

  public SimulatedQueue(int servers, int capacity, double arrivalMin, double arrivalMax, double exitMin, double exitMax) {
    this.servers = servers;
    this.capacity = capacity;
    this.arrivalMin = arrivalMin;
    this.arrivalMax = arrivalMax;
    this.exitMin = exitMin;
    this.exitMax = exitMax;
    this.times = new double[capacity + 1];
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

  
}
