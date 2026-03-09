package FlowAlignFinal;
import java.util.*;
import java.io.*;
/* ==============================
   CO2 - Abstract Data Type
   Task class represents a data structure
================================ */
class Task {
    String name;
    String description;
    String priority;
    String deadline;
    boolean completed;

    Task(String name, String description, String priority, String deadline) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.deadline = deadline;
        this.completed = false;
    }
}
/* ==============================
   CO3 - Queue Data Structure
   Used for timetable scheduling
================================ */
class Queue1Demo {
    int size;
    int front, rear;
    Task QData[];
    public Queue1Demo(int size) {
        this.size = size;
        front = -1;
        rear = -1;
        QData = new Task[size];
    }
    public void enqueue(Task data) {
        if (rear == size - 1) {
            System.out.println("Queue Overflow");
        }
        else {
            if (front == -1 && rear == -1) {
                front = 0;
                rear = 0;
            }
            else {
                rear++;
            }
            QData[rear] = data;
        }
    }
    public Task dequeue() {
        if (front == -1) {
            System.out.println("Queue Underflow");
            return null;
        }
        Task ddata = QData[front];
        front++;
        if (front > rear) {
            front = -1;
            rear = -1;
        }
        return ddata;
    }
    public boolean isEmpty() {
        return front == -1;
    }
}
/* ==============================
   CO3 - Stack Data Structure
   Used for completed tasks
================================ */
class StackEx {
    int size;
    Task stackData[];
    int top;
    public StackEx(int size) {
        this.size = size;
        stackData = new Task[size];
        top = -1;
    }
    public void push(Task data) {
        if (top == size - 1) {
            System.out.println("Stack Overflow");
        }
        else {
            top++;
            stackData[top] = data;
        }
    }
    public Task pop() {
        if (top == -1) {
            System.out.println("Stack Underflow");
            return null;
        }
        Task data = stackData[top];
        top--;
        return data;
    }
}
/* ==============================
   Main FlowAlign Application
================================ */
public class FlowAlign {
    static Scanner sc = new Scanner(System.in);
    /* CO4 - Java Collections */
    static ArrayList<Task> tasks = new ArrayList<>();
    static StackEx completedStack = new StackEx(50);
    static int focusMinutes = 0;
    static boolean timerRunning = false;
    static boolean resetTimer = false;
    static Thread timerThread;
    static int remainingSeconds = 0;
    public static void main(String[] args) {
        /* CO4 - File handling */
        loadTasks();
        loginMenu();
        String energy = selectEnergy();
        addTasks();
        generateTimetable(energy);
        dashboard();
    }
    /* ==============================
       LOGIN / SIGNUP SYSTEM
       CO5 - Real application feature
    ================================= */
    static void loginMenu() {
        System.out.println("1 Sign Up");
        System.out.println("2 Login");
        int choice = sc.nextInt();
        sc.nextLine();
        if (choice == 1)
            signUp();
        else
            login();
    }
    static void signUp() {
        try {
            System.out.print("Username: ");
            String user = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();
            FileWriter fw = new FileWriter("logindata.txt", true);
            fw.write(user + "," + pass + "\n");
            fw.close();
        }
        catch (Exception e) {
            System.out.println("Error saving login");
        }
    }
    static void login() {
        try {
            System.out.print("Username: ");
            String user = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();

            BufferedReader br = new BufferedReader(new FileReader("logindata.txt"));

            String line;

            boolean found = false;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                if (data[0].equals(user) && data[1].equals(pass)) {
                    found = true;
                    break;
                }
            }

            br.close();

            if (!found) {
                System.out.println("Invalid login");
                System.exit(0);
            }

        }

        catch (Exception e) {
            System.out.println("Login error");
        }
    }

    /* ==============================
       ENERGY LEVEL
    ================================= */

    static String selectEnergy() {

        System.out.println("\nSelect Energy Level");
        System.out.println("1 High");
        System.out.println("2 Medium");
        System.out.println("3 Low");

        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1)
            return "High";
        if (choice == 2)
            return "Medium";
        return "Low";
    }

    /* ==============================
       ADD TASKS
       CO2 - Insert operation
    ================================= */

    static void addTasks() {

        // First task must be added

        System.out.println("\nAdd First Task");

        System.out.print("Task Name: ");
        String name = sc.nextLine();

        System.out.print("Description: ");
        String desc = sc.nextLine();

        System.out.print("Priority (High/Medium/Low): ");
        String priority = sc.nextLine();

        System.out.print("Deadline (YYYY-MM-DD): ");
        String deadline = sc.nextLine();

        tasks.add(new Task(name, desc, priority, deadline));

        saveTasks();

        // After first task ask user what to do

        while (true) {

            System.out.println("\n1 Add Another Task");
            System.out.println("2 Generate Timetable");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 2) {
                break;
            }

            System.out.print("Task Name: ");
            name = sc.nextLine();

            System.out.print("Description: ");
            desc = sc.nextLine();

            System.out.print("Priority (High/Medium/Low): ");
            priority = sc.nextLine();

            System.out.print("Deadline (YYYY-MM-DD): ");
            deadline = sc.nextLine();

            tasks.add(new Task(name, desc, priority, deadline));

            saveTasks();
        }
    }
    /* ==============================
       CO1 - Sorting Algorithm
       Sort tasks by priority
    ================================= */

    static void sortTasksByPriority() {

        Collections.sort(tasks, (a, b) -> {

            List<String> order = Arrays.asList("High", "Medium", "Low");

            return order.indexOf(a.priority) - order.indexOf(b.priority);
        });
    }

    /* ==============================
       TIMETABLE GENERATION
       CO3 - Queue usage
    ================================= */

    static void generateTimetable(String energy) {

        sortTasksByPriority();

        Queue1Demo queue = new Queue1Demo(50);

        // CO2: Traversal of ADT
// Only incomplete tasks are added to timetable

        for (Task t : tasks) {

            if (!t.completed) {   // skip completed tasks
                queue.enqueue(t);
            }
        }

        int breakTime = 20;

        if (energy.equals("High"))
            breakTime = 10;
        else if (energy.equals("Medium"))
            breakTime = 20;
        else
            breakTime = 30;

        int hour = 9;

        System.out.println("\nTimetable\n");

        while (!queue.isEmpty()) {

            Task t = queue.dequeue();

            System.out.println(hour + ":00 Task: " + t.name + " Priority: " + t.priority);

            hour++;

            System.out.println("Break " + breakTime + " minutes\n");
        }
    }

    /* ==============================
       DASHBOARD MENU
       CO5 - Application design
    ================================= */

    static void dashboard() {

        while (true) {

            System.out.println("\nDashboard");
            System.out.println("1 Tasks");
            System.out.println("2 Timer");
            System.out.println("3 Productivity");
            System.out.println("4 Reminders");
            System.out.println("5 Logout");

            int choice = sc.nextInt();

            if (choice == 1)
                taskMenu();
            if (choice == 2)
                timerMenu();
            if (choice == 3)
                productivity();
            if (choice == 4)
                reminders();
            if (choice == 5)
                System.exit(0);
        }
    }

    /* ==============================
       TASK COMPLETION
       CO3 - Stack usage
    ================================= */

    static void taskMenu() {

        for (int i = 0; i < tasks.size(); i++) {

            Task t = tasks.get(i);

            System.out.println((i + 1) + " " + t.name + " " + t.priority +
                    (t.completed ? " Done" : ""));
        }

        System.out.print("Select task number completed: ");

        int num = sc.nextInt();

        Task t = tasks.get(num - 1);

        t.completed = true;

        completedStack.push(t);

        saveTasks();
    }

    /* ==============================
       TIMER SIMULATION
       CO1 - Time complexity simulation
    ================================= */

    static void timerMenu()
    {
        while(true)
        {
            System.out.println("\nFocus Timer Menu");
            System.out.println("1 High Focus (45 min)");
            System.out.println("2 Medium Focus (30 min)");
            System.out.println("3 Low Focus (20 min)");
            System.out.println("4 Stop Timer");
            System.out.println("5 Reset Timer");
            System.out.println("6 Back to Dashboard");

            int choice = sc.nextInt();

            switch(choice)
            {
                case 1:
                    startTimer(45);
                    break;

                case 2:
                    startTimer(30);
                    break;

                case 3:
                    startTimer(20);
                    break;

                case 4:
                    stopTimer();
                    break;

                case 5:
                    resetTimer();
                    break;

                case 6:
                    return;
            }
        }
    }

    static void startTimer(int minutes)
    {
        remainingSeconds = minutes * 60;

        timerRunning = true;
        resetTimer = false;

        timerThread = new Thread(() -> {

            try {

                while(remainingSeconds > 0 && timerRunning)
                {
                    int m = remainingSeconds / 60;
                    int s = remainingSeconds % 60;

                    System.out.print("\rTimer: " + m + ":" + String.format("%02d", s));

                    Thread.sleep(1000);

                    remainingSeconds--;
                }

                if(resetTimer)
                {
                    System.out.println("\nTimer Reset.");
                }

                else if(!timerRunning)
                {
                    System.out.println("\nTimer Stopped.");
                }

                else
                {
                    System.out.println("\nSession Completed!");
                }

            }

            catch(Exception e){}

        });

        timerThread.start();
    }
    static void stopTimer()
    {
        timerRunning = false;
    }
    static void resetTimer()
    {
        timerRunning = false;
        resetTimer = true;
        remainingSeconds = 0;
    }
    /* ==============================
       PRODUCTIVITY STATUS
       CO5 - Practical DS application
    ================================= */

    static void productivity() {

        int done = 0;

        for (Task t : tasks)

            if (t.completed)
                done++;

        System.out.println("Tasks Completed: " + done);

        System.out.println("Focus Minutes: " + focusMinutes);
    }

    /* ==============================
       REMINDERS
       CO2 - Traversal operation
    ================================= */

    static void reminders() {

        System.out.println("\nPending Tasks");

        for (Task t : tasks)

            if (!t.completed)

                System.out.println(t.name + " Deadline: " + t.deadline);
    }

    /* ==============================
       FILE STORAGE
       CO4 - Persistent storage
    ================================= */

    static void saveTasks() {

        try {

            FileWriter fw = new FileWriter("tasks.txt");

            for (Task t : tasks)

                fw.write(t.name + "," + t.description + "," + t.priority + "," + t.deadline + "," + t.completed + "\n");

            fw.close();
        }

        catch (Exception e) {
        }
    }

    static void loadTasks() {
        tasks.clear();
        try {

            File file = new File("tasks.txt");

            if (!file.exists())
                return;

            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                Task t = new Task(data[0], data[1], data[2], data[3]);

                t.completed = Boolean.parseBoolean(data[4]);

                tasks.add(t);
            }

            br.close();
        }

        catch (Exception e) {
        }
    }
}