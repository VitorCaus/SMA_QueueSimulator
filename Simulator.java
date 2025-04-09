import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Simulator {
  //Random number variables for Linear Congruential Method
  public static final long RANDOM_SEED = 123456;
  public static final long RANDOM_A = 1103515245;
  public static final long RANDOM_C = 12345;
  public static final double RANDOM_M = Math.pow(2, 31) -1;
  private static double previousRandom = RANDOM_SEED;
  private static int countRandom = 100000;

  //Random numbers for validation with Simulator.jar
  private static Queue<Double> randomNumbers = new LinkedList<>(List.of(
    0.3,
    0.4,
    0.1,
    0.9,
    0.9,
    0.1,
    1.0
));
  //Debug
  // -> if true, uses randomNumbers when calling nextRandom()
  // -> if false, uses LCM method to generate random numbers in nextRandom()
  private static final boolean DEBUG = false;
  

  //Queue structure related variables
  private static double globalTime = 0.0;
  
  //Queue events related variables
  private static double firstArrival = 1.5;

  //Queues structures
  private static SimulatedQueue queue1 = new SimulatedQueue(
    2, 
    3, 
    1.0, 
    4.0, 
    3.0, 
    4.0
    );
  private static SimulatedQueue queue2 = new SimulatedQueue(
    1, 
    5, 
    -1.0, 
    -1.0, 
    2.0, 
    3.0
    );

  //Initialization of Scheduler
  private static PriorityQueue<Event> scheduler = new PriorityQueue<>();
  private static List<SimulatedQueue> queues;

  public static void main(String[] args){
    queues = new LinkedList<>(List.of(queue1, queue2));

    //Add first event
    scheduler.add(new Event(firstArrival, EventType.ARRIVAL));
    simulate();
    printResults();
  }


  //Generate next random number following Linear Congruential Method
  public static double nextRandom(){
    if(DEBUG){
      return randomNumbers.poll();
    }
    previousRandom = ((RANDOM_A * previousRandom) + RANDOM_C) % RANDOM_M;
    return previousRandom / RANDOM_M;
  }

  //Start simulation
  public static void simulate(){
    // while(randomNumbers.size() > 0){
    while(countRandom-- > 0){
      Event event = nextEvent();
      //Calls method according to event type
      if(event.getType() == EventType.ARRIVAL){
        arrival(event);
      } else if (event.getType() == EventType.EXIT){
        exit(event);
      } else if (event.getType() == EventType.PASSAGE){
        passage(event);
      }
    }
  }

  //Get next event from Queue (by shortest time)
  public static Event nextEvent(){
    return scheduler.poll();
  }

  /*
   * ARRIVAL EVENT HANDLING
   */
  public static void arrival(Event event){
    accumulateTime(event);
    if (queue1.Status() < queue1.Capacity()){
      //Add to queue if not full
      queue1.in();;
      if (queue1.Status() <= queue1.Servers()){
        //If possible, schedule an exit
        scheduler.add(new Event(globalTime + timeGenerator(queue1.getExitMin(), queue1.getExitMax()), EventType.PASSAGE));
      }
    } else {
      //queue loss
      queue1.loss();
    }
      //Schedule an arrival
      scheduler.add(new Event(globalTime + timeGenerator(queue1.getArrivalMin(), queue1.getArrivalMax()), EventType.ARRIVAL));    
  }

  /*
   * EXIT EVENT HANDLING
   */
  public static void exit(Event event){
    accumulateTime(event);
    //Remove one from queue
    queue2.out();
    //If there are more clients than servers
    if (queue2.Status() >= queue2.Servers()){
      //Schedule an exit
      scheduler.add(new Event(globalTime + timeGenerator(queue2.getExitMin(), queue2.getExitMax()), EventType.EXIT));
    }
  }

  public static void passage(Event event){
    accumulateTime(event);
    queue1.out();
    if(queue1.Status() >= queue1.Servers()){
      scheduler.add(new Event(globalTime + timeGenerator(queue1.getExitMin(), queue1.getExitMax()), EventType.PASSAGE));
    }
    if(queue2.Status() < queue2.Capacity()){
      queue2.in();
      if(queue2.Status() <= queue2.Servers()){
        scheduler.add(new Event(globalTime + timeGenerator(queue2.getExitMin(), queue2.getExitMax()), EventType.EXIT));
      }
    }
    else{
      queue2.loss();
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

    //Add time to queue states
    queue1.accumStateTime(delta_time);
    queue2.accumStateTime(delta_time);

    //Update globalTime
    globalTime = event.getTime();
  }



  /*
   * Print Results
   */

  private static void printResults(){
    System.out.println("---==| SIMULATION RESULTS |==---\n");
    System.out.printf("Total time: %.4f\n", globalTime);
    int i = 1;
    for(SimulatedQueue queue : queues){
      System.out.println("-----------------------------------------------------");
      System.out.printf("Queue %d (G/G/%d/%d):\n", i, queue.Servers(), queue.Capacity());
      printQueue(queue);
      System.out.printf("\nLosses in Queue %d: %d\n", i++, queue.Losses());

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
    for (int i = 0; i <= queue.Capacity(); i++) {
        String probability = String.format("%.2f%%", (queue.getTimes()[i] * 100) / globalTime);
        System.out.printf("%-10d %-20.4f %-25s\n", i, queue.getTimes()[i], probability);
    }
  }
}