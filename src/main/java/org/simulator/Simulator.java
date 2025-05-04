package org.simulator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.simulator.dto.ConfigDTO;
import org.simulator.dto.NetworkDTO;
import org.yaml.snakeyaml.Yaml;

public class Simulator {
  //Random number variables for Linear Congruential Method
  public static final long RANDOM_SEED = 123456;
  public static final long RANDOM_A = 1103515245;
  public static final long RANDOM_C = 12345;
  public static final double RANDOM_M = Math.pow(2, 31) -1;
  private static double previousRandom = RANDOM_SEED;
  private static int randomCount;

  //Random numbers for validation with Simulator.jar
  private static Queue<Double> randomValues;
  
  //Queue structure related variables
  private static double globalTime = 0.0;

  //Initialization of Scheduler
  private static PriorityQueue<Event> scheduler = new PriorityQueue<>();
  private static List<SimulatedQueue> queues = new LinkedList<>();

  public static void main(String[] args){
    if(args.length == 0){
      System.out.println("Error: usage = java -jar ./target/queue-simulator.jar <config.yml>");
      return;
    }
    configureSimulator(args[0]);
    // configureSimulator("src/main/resources/config.yml");


    //Add first arrivals
    queues.forEach(queue -> {
      if(queue.getFirstArrival() >= 0){
        scheduler.add(new Event(queue.getFirstArrival(), EventType.ARRIVAL, null, queue));
      }
    });

    simulate();
    printResults();
  }

  //Generate next random number following Linear Congruential Method
  public static double nextRandom(){
    if(randomValues != null){
      return randomValues.size() > 0 ? randomValues.poll() : 0.99;
    }
    randomCount--;
    previousRandom = ((RANDOM_A * previousRandom) + RANDOM_C) % RANDOM_M;
    return previousRandom / RANDOM_M;
  }

  //Start simulation
  public static void simulate(){
    System.out.println("---==| SIMULATION STARTED |==---\n");
    while(randomCount > 0 || (randomValues != null && !randomValues.isEmpty())){
      Event event = scheduler.poll();

      if(event == null){
        System.out.println("Scheduler is empty - Simulation finished.");
        break;
      }

      //Calls method according to event type
      switch (event.getType()) {
        case ARRIVAL: arrival(event); break;
        case EXIT: exit(event); break;
        case PASSAGE: passage(event); break;
        default: System.out.printf("Error: Unknown event type %s\n", event.getType());
      }
    }
  }


  /*
   * ARRIVAL EVENT HANDLING
   */
  public static void arrival(Event event){
    accumulateTime(event);

    SimulatedQueue destination = event.getDestinationQueue();

    if(destination.Capacity() < 0 || destination.Status() < destination.Capacity()){
      //Add to queue if not full
      destination.in();
      if (destination.Status() <= destination.Servers()){
        SimulatedQueue nextQueue = getPassageQueue(destination);
        scheduler.add(new Event(
          globalTime + timeGenerator(destination.getExitMin(), destination.getExitMax()), 
          nextQueue != null ? EventType.PASSAGE : EventType.EXIT, 
          destination, 
          nextQueue
        ));
      }
    } else {
      //queue loss
      destination.loss();
    }
      //Schedule an arrival
      scheduler.add(new Event(globalTime + timeGenerator(destination.getArrivalMin(), destination.getArrivalMax()), EventType.ARRIVAL, null, destination));    
  }

  /*
   * EXIT EVENT HANDLING
   */
  public static void exit(Event event){
    accumulateTime(event);
    
    SimulatedQueue origin = event.getOriginQueue();
    //Remove one from queue
    origin.out();
    //If there are more clients than servers
    if (origin.Status() >= origin.Servers()){
      SimulatedQueue nextQueue = getPassageQueue(origin);
      scheduler.add(new Event(
        globalTime + timeGenerator(origin.getExitMin(), origin.getExitMax()), 
        nextQueue != null ? EventType.PASSAGE : EventType.EXIT, 
        origin, 
        nextQueue
      ));
    }
  }

  public static void passage(Event event){
    accumulateTime(event);
    SimulatedQueue origin = event.getOriginQueue();
    SimulatedQueue destination = event.getDestinationQueue();
    origin.out();

    //schedule event leaving the origin queue
    if(origin.Status() >= origin.Servers()){
      SimulatedQueue nextQueue = getPassageQueue(origin);
      scheduler.add(new Event(
        globalTime + timeGenerator(origin.getExitMin(), origin.getExitMax()), 
        nextQueue != null ? EventType.PASSAGE : EventType.EXIT, 
        origin, 
        nextQueue
      ));
    }
    
    //schedule event going to destination queue
    if(destination.Capacity() < 0 || destination.Status() < destination.Capacity()){
      destination.in();
      if(destination.Status() <= destination.Servers()){
        SimulatedQueue nextQueue = getPassageQueue(destination);
        scheduler.add(new Event(
          globalTime + timeGenerator(destination.getExitMin(), destination.getExitMax()), 
          nextQueue != null ? EventType.PASSAGE : EventType.EXIT, 
          destination, 
          nextQueue
        ));
      }
    }
    else{
      destination.loss();
    }
  }

  /*
   * ARRIVAL/EXIT TIME GENERATOR
   */
  public static double timeGenerator(double A, double B){
    return A + ((B-A) * nextRandom());
  }

  /*
  * ACCUMULATE TIME TO GLOBALTIME
  */  
  private static void accumulateTime(Event event){
    //Calculate difference between event time and globalTime
    double delta_time = event.getTime() - globalTime;

    // System.out.println(event.toString());
    //Add time to queue states
    for(SimulatedQueue queue : queues){
      queue.accumStateTime(delta_time);
    }

    //Update globalTime
    globalTime = event.getTime();
  }

  /*
   * Get next passage queue
   */
  private static SimulatedQueue getPassageQueue(SimulatedQueue queue){
    double sum = 0.0;
    double prob = nextRandom();
    for(NextPassageQueue nextQueue : queue.getNextQueues()){
      sum += nextQueue.getProbability();
      if(prob < sum){
        return nextQueue.getNextQueue();
      }
    }
    return null;
  }

  /*
   * Print Results
   */
  private static void printResults(){
    System.out.println("---==| SIMULATION RESULTS |==---\n");
    System.out.printf("Total time: %.4f\n", globalTime);
    
    for(SimulatedQueue queue : queues){
      System.out.println("-----------------------------------------------------");
      System.out.printf("Queue %s (G/G/%d%s):\n", 
        queue.getName(), 
        queue.Servers(),
        queue.Capacity() > 0 ? String.format("/%d", (queue.Capacity())) : ""
      );
      printQueue(queue);
      System.out.printf("\nLosses in Queue %s: %d\n", queue.getName(), queue.Losses());
    }
  }

  /*
   * Print Queue
   */
  public static void printQueue(SimulatedQueue queue){
    if(queue.getArrivalMin() >= 0)
      System.out.printf("Arrival: %f - %f\n", queue.getArrivalMin(), queue.getArrivalMax());
    System.out.printf("Exit: %f - %f\n\n", queue.getExitMin(), queue.getExitMax());
    System.out.printf("%-10s %-20s %-20s\n", "Queue", "State Time", "Time (Probability)");
    for (int i = 0; i < queue.getTimes().length; i++) {
      if (queue.getTimes()[i] <= 1e-4){ 
        break;
      }
      String probability = String.format("%.2f%%", (queue.getTimes()[i] * 100) / globalTime);
      System.out.printf("%-10d %-20.4f %-25s\n", i, queue.getTimes()[i], probability);
    }
  }

  /*
   * Read config.yml and initialize simulator
   */
  public static void configureSimulator(String fileName){
    Yaml yaml = new Yaml();
    
    try {
      //load config data and separate in DTOs
      ConfigDTO data = yaml.loadAs(new FileInputStream(fileName), ConfigDTO.class);
      Map<String, SimulatedQueue> configQueues = data.getQueues();
      List<NetworkDTO> configNetwork = data.getNetwork();
      Map<String, Double> configArrivals = data.getArrivals();

      //configure random values
      randomValues = data.getRandomValues() != null ? new LinkedList<>(data.getRandomValues()) : null;
      randomCount = data.getRandomCount() > 0 && data.getRandomValues() == null ? data.getRandomCount() : -1;

      //configure queues and their first arrvals
      for(String key : configQueues.keySet()){
        SimulatedQueue queue = configQueues.get(key);
        queue.setName(key);
        queue.setFirstArrival(configArrivals.get(key) != null ? configArrivals.get(key) : -1.0);
        queues.add(queue);
      }

      //configure networks between queues
      for(NetworkDTO network : configNetwork){
        SimulatedQueue origin = configQueues.get(network.getOrigin());
        SimulatedQueue destination = configQueues.get(network.getDestination());
        if(destination == null){
          continue;
        }
        origin.addNextQueue(destination, network.getProbability());
      }

      //order queues networks by probability
      for(SimulatedQueue queue : queues){
        if(queue.getNextQueues() != null){
          queue.getNextQueues().sort((a, b) -> Double.compare(a.getProbability(), b.getProbability()));
        }
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}