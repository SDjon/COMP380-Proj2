import java.util.Scanner;

public class HopfieldNet {

    public static void main(String[] args){
        receiveInput();
    }

    public static void receiveInput(){
        Scanner scanner = new Scanner(System.in);

        int action = 0;
        do{
            if (action == 1) {
                trainingSpecs(scanner);
            } else if (action == 2) {
                testingSpecs(scanner);
            }
            System.out.println("Welcome to our Hopfield Neural Network.");
            System.out.println("1) Enter 1 to train the net on a data file");
            System.out.println("2) Enter 2 to test the net on a data file");
            System.out.println("3) Enter 3 to quit");
            action = scanner.nextInt();
        }
        while (action != 3);
        System.out.println("Thank you for using the Net. Come back soon!");
        scanner.close();
    }

    public static void trainingSpecs(Scanner scanner){

    }
    public static void testingSpecs(Scanner scanner){
       
    }

    /**
     * Reads in a file and transforms the data into numbers to work with
     * @param filename The name of the file to read
     * @return dimension the dimension number of the square matrices
     */
    public int readData(String filename){
        int dimension = 0;
        return dimension;
    }
    /**
     * Trains the network by creating a weight matrix and writes the saved weight matrix to a specified file
     * @param fileToWrite The name of the file to write to, creating it if it does not exist
     */
    public void trainNetwork(String fileToWrite){
        /**
         * Call read data helper function which will return the dimension N and the parsed and transformed data
         * Create an N x N empty matrix
         * In a FOR LOOP: Calculate the weight matrix and update the initialized one accordingly
         * In another FOR LOOP: Set all the diagonal entries to 0
         * Write the saved weight matrix to a specified file
         */
    }

    /**
     * Runs the testing algorithim of the network until convergence
     * @param resultsFile The file to display the results of the testing in
     * @param savedTrainingData The file that contains the weight matrix that the network is using for the test
     */
    public void testNetwork(String savedTrainingData, String resultsFile){
        /**
         * initialize the weight matrix after taking it from the file
         * Store the original test vector before random selection
         * Randomly select an element of the test vector, x_i.  x = [1,3,5] <-- Randomly choose one. x_2 would be "3" in this case
         * Calculate Y_in_i = x_i + [x1(W_1_i) + x2(W_2_i) +...+xn(W_n_i)
         * Calculate y = F(Y_in_i)
         * Broadcast and change x_i to equal y
         * If the new test vector is the same as the original test vector at the start of the epoch,
         * then convergence! if not keep going and randomly select again but now the original test vector is the current
         */
    }


}
