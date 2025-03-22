import java.util.PriorityQueue;

public class Simulator {
  //Random number variables for Linear Congruential Method
  public static final long RANDOM_SEED = 123456;
  public static final long RANDOM_A = 1103515245;
  public static final long RANDOM_C = 12345;
  public static final double RANDOM_M = Math.pow(2, 31) -1;
  
  private static double previousRandom = RANDOM_SEED;
  private static int countRandom = 20;

  //Queue structure related variables
  private static double globalTime = 0.0;
  private static int losses = 0;
  private static int queueState = 0;
  private static int capacity = 5;
  private static int servers = 2;
  private static double[] states = new double[capacity + 1];
  
  //Queue events related variables
  private static double firstArrival = 2.0;
  private static double[] arrivalInterval = {2, 5};
  private static double[] exitInterval = {3, 5};

  //Initialization of Scheduler
  private static PriorityQueue<Event> scheduler = new PriorityQueue<>();

  public static void main(String[] args){
    //Add first event that arrives
    scheduler.add(new Event(firstArrival, EventType.ARRIVAL));
    simulate();
    
    System.out.println("---==| SIMULATION RESULTS |==---\n");
    
    System.out.println("State = Time (Percentage)");
    for(int i = 0; i<=capacity; i++){
      System.out.printf("%d = %.4f (%.2f%%)\n", i, states[i], (states[i] / globalTime) * 100);
    }
    System.out.printf("\nTotal time: %.4fs\nLosses: %d\n\n", globalTime, losses);
  }


  //Generate next random number following Linear Congruential Method
  public static double nextRandom(){
    previousRandom = ((RANDOM_A * previousRandom) + RANDOM_C) % RANDOM_M;
    return previousRandom / RANDOM_M;
  }

  //Start simulation
  public static void simulate(){
    while(countRandom-- > 0){
      Event event = nextEvent();
      //Calls method according to event type
      if(event.getType() == EventType.ARRIVAL){
        arrival(event);
      } else{
        exit(event);
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
    accumulateTime(event.getTime());
    if (queueState < capacity){
      //Add to queue if not full
      queueState++;
      if (queueState <= servers){
        //If possible, schedule an exit
        scheduler.add(new Event(globalTime + timeGenerator(exitInterval[0], exitInterval[1]), EventType.EXIT));
      }
    } else {
      //queue loss
      losses++;
    }
      //Schedule an arrival
      scheduler.add(new Event(globalTime + timeGenerator(arrivalInterval[0], arrivalInterval[1]), EventType.ARRIVAL));    
  }

  /*
   * EXIT EVENT HANDLING
   */
  public static void exit(Event event){
    accumulateTime(event.getTime());
    //Remove one from queue
    queueState--;
    //If there are more clients than servers
    if (queueState >= servers){
      //Schedule an exit
      scheduler.add(new Event(globalTime + timeGenerator(exitInterval[0], exitInterval[1]), EventType.EXIT));
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
  private static void accumulateTime(double time){
    //Calculate difference between event time and globalTime
    double delta_time = time - globalTime;
    //Add the difference to that position in the queue
    states[queueState] += delta_time;
    //Assign globalTime to event time
    globalTime = time;
  }
}