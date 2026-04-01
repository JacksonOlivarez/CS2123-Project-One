import java.io.*;
import java.util.*;

/* ResourceManagement
 *
 * Stores the information needed to decide which items to purchase for the given budget and departments
 */
public class ResourceManagement
{
    private PriorityQueue<Department> departmentPQ; /* priority queue of departments */
    private Double remainingBudget;                 /* the budget left after purchases are made (should be 0 after the constructor runs) */
    private Double budgetSpent;                          /* the total budget allocated */

    public static void printName( )
    {
        System.out.println("This solution was completed by:");
        System.out.println("Jackson Olivarez");
        System.out.println("Adrian Rice");
    }

    /* Constructor for a ResourceManagement object
     * TODO
     * Simulates the algorithm from the pdf to determine what items are purchased
     * for the given budget and department item lists.
     */
    public ResourceManagement(String[] fileNames, Double budget )
    {
        System.out.println("ITEMS PURCHASED\n----------------------------\n");
        departmentPQ = new PriorityQueue<>();
        remainingBudget = budget;
        budgetSpent = 0.0;
        for (String fileName : fileNames) {
            departmentPQ.add(new Department(fileName));
        }

        while (remainingBudget > 0.0){
            Department current = departmentPQ.poll();
            assert current != null;
            if (!current.itemsDesired.isEmpty() && current.itemsDesired.peek().price < remainingBudget){
                // Buy the desired item.
                Item item = current.itemsDesired.poll();

                current.priority += item.price;
                remainingBudget -= item.price;
                budgetSpent += item.price;

                current.itemsReceived.add(item);

                System.out.printf("%-30s - %-30s - $%.2f\n", current.name, item.name, item.price);

            } else if (current.itemsDesired.isEmpty()){
                if (remainingBudget >= 1000){
                    // $1000 Scholarship
                    current.priority += 1000.00;
                    remainingBudget -= 1000.00;
                    budgetSpent += 1000.00;

                    current.itemsReceived.add(new Item("SCHOLARSHIP", 1000.00));

                    System.out.printf("%-30s - %-30s - $%.2f\n", current.name, "SCHOLARSHIP", 1000.00);
                } else {

                    current.priority += remainingBudget; // Add money spent to dpt priority
                    budgetSpent += remainingBudget; // Add to money spent.


                    current.itemsReceived.add(new Item("SCHOLARSHIP", remainingBudget));

                    System.out.printf("%-30s - %-30s - $%.2f\n", current.name, "SCHOLARSHIP", remainingBudget);

                    remainingBudget = 0.0;
                }

            } else if (current.itemsDesired.peek().price > remainingBudget){
                Item item = current.itemsDesired.poll();
                current.itemsRemoved.add(item);
            }

            departmentPQ.add(current); // Resorts the Priority Queue with updated values.

        }

    }

    /* printSummary
     * TODO
     * Print a summary of what each department received and did not receive.
     * Be sure to also print remaining items in each itemsDesired Queue.
     */
    public void printSummary(){
        while (!departmentPQ.isEmpty()){
            Department current = departmentPQ.poll();
            System.out.println(current.name + "\n");

            System.out.printf("%-20s =$%.2f\n", "Total Spent: ", current.priority);
            System.out.printf("%-30s = %.2f %%\n", "Percentage of Budget", (current.priority / budgetSpent) * 100);

            System.out.println("----------------------------");

            System.out.println("ITEMS RECEIVED");
            for (Item item : current.itemsReceived){
                System.out.printf("%-30s - $%.2f\n", item.name, item.price);
            }
            System.out.println("ITEMS NOT RECEIVED");
            for (Item item : current.itemsRemoved){
                System.out.printf("%-30s - $%.2f\n", item.name, item.price);
            }

        }
    }
}

/* Department
 *
 * Stores the information associated with a Department at the university
 */
class Department implements Comparable<Department>
{
    String name;                /* name of this department */
    Double priority;            /* total money spent on this department */
    Queue<Item> itemsDesired = new LinkedList<Item>();   /* list of items this department wants */
    Queue<Item> itemsReceived = new LinkedList<Item>();  /* list of items this department received */
    Queue<Item> itemsRemoved = new LinkedList<Item>();  /* list of items that were skipped because they exceeded the remaining budget */

    /* TODO
     * Constructor to build a Department from the information in the given fileName
     */
    public Department( String fileName ) {
        File f = new File(fileName);
        try(FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr)) {
            name = br.readLine();
            priority = 0.0;
            String line;
            while((line = br.readLine()) != null){//!(line.equals("\n")) - !((line=br.readLine()).equals("\n"))
                if(line.length() != 0){
                    String listedItem = line;
                    String itemPrice = br.readLine();
                    double listedPrice = Double.parseDouble(itemPrice);
                    Item i = new Item(listedItem,listedPrice);
                    itemsDesired.add(i);
                }
            }

        } catch (FileNotFoundException e) {throw new RuntimeException(e);}
        catch (IOException e) {throw new RuntimeException(e);}

    }


    /*
     * Compares the data in the given Department to the data in this Department
     * Returns -1 if this Department comes first
     * Returns 0 if these Departments have equal priority
     * Returns 1 if the given Department comes first
     *
     * This function is to ensure the departments are sorted by the priority when put in the priority queue
     */
    public int compareTo( Department dept ){
        return this.priority.compareTo(dept.priority);
    }

    public boolean equals( Department dept ){
        return this.name.compareTo( dept.name ) == 0;
    }

    @Override
    @SuppressWarnings("unchecked") //Suppresses warning for cast
    public boolean equals(Object aThat) {
        if (this == aThat) //Shortcut the future comparisons if the locations in memory are the same
            return true;
        if (!(aThat instanceof Department))
            return false;
        Department that = (Department)aThat;
        return this.equals( that ); //Use above equals method
    }

    @Override
    public int hashCode() {
        return name.hashCode(); /* use the hashCode for data stored in this name */
    }

    /* Debugging tool
     * Converts this Department to a string
     */
    @Override
    public String toString() {
        return "NAME: " + name + "\nPRIORITY: " + priority + "\nDESIRED: " + itemsDesired + "\nRECEIVED " + itemsReceived + "\nREMOVED " + itemsRemoved + "\n";
    }
}

/* Item
 *
 * Stores the information associated with an Item which is desired by a Department
 */
class Item
{
    String name;    /* name of this item */
    Double price;   /* price */

    public Item(String name, Double price){
        this.name = name;
        this.price = price;
    }
}
