import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


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
        System.out.println("Enter the training data file name:");
        scanner.nextLine(); //get rid of newline
        String trainingDataFileName = scanner.nextLine();

        System.out.println("Enter a file name to save the trained weight values:");
        String outputWeightFileName = scanner.nextLine();

        trainNetwork(outputWeightFileName, trainingDataFileName);

    }
    public static void testingSpecs(Scanner scanner){
       
    }

    /**
     * Reads in a file and transforms the data into numbers to work with
     * @param filename The name of the file to read
     * @return the input vectors, all transated into number representation
     */
    public static ArrayList<int[]> readData(String filename){
        int dimension; //the first line in the file
        int numOfInputs; //the third line in the file

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line; //reused var, for when contents from readline are read

            //Header data
            dimension = Integer.parseInt(reader.readLine().trim().split("\\s+")[0]);
            reader.readLine();
            numOfInputs = Integer.parseInt(reader.readLine().trim().split("\\s+")[0]);
            System.out.println("Number of patterns: "+numOfInputs);

            reader.readLine();

            ArrayList<int[]> list = new ArrayList<>(); //a list of input vectors
            //rest of the file
            for (int patternNum = 0; patternNum < numOfInputs; patternNum++) {
                System.out.println("Iteration number : "+patternNum);
                int[] tempArray = new int[dimension * dimension];
                int tempArrayIndex = 0;

                for (int i = 0; i < dimension; i++) {
                     line = reader.readLine();

                    for (int j = 0; j < line.length(); j++) {
                        char character = line.charAt(j);
                        if (character == 'O') {
                            tempArray[tempArrayIndex] = 1;
                        } else {
                            tempArray[tempArrayIndex] = -1;
                        }
                        tempArrayIndex++;
                    }
                }
                reader.readLine(); // skip to go to the next pattern

                list.add(tempArray); // Add the whole pattern vector
            }
            System.out.println("Training file was successfully read: "+filename);
            return list;
        } catch (IOException e) {
            // Handle file reading errors
            e.printStackTrace();
        }
        System.out.println("System failed to read training file data: "+filename);
        return null;

    }
    /**
     * Trains the network by creating a weight matrix and writes the saved weight matrix to a specified file
     * @param fileToWrite The name of the file to write to, creating it if it does not exist
     * @param trainingData The file that contains the data the network will train
     */
    public static void trainNetwork(String fileToWrite, String trainingData){
        ArrayList<int[]> inputVectors = readData(trainingData);
        int matrixDimension = (int) Math.sqrt(inputVectors.get(0).length);
        int numberOfPatterns = inputVectors.size();
        int vectorLength = inputVectors.get(0).length;
        int[][] weightMatrix = new int[matrixDimension*matrixDimension][matrixDimension*matrixDimension];
        System.out.println("INPUT VECTORS SIZE AFTER READ DATA: "+numberOfPatterns);

        //Build weight matrix with outer product
        for (int[] pattern : inputVectors) {
            for (int i = 0; i < vectorLength; i++) {
                for (int j = 0; j < vectorLength; j++) {
                    weightMatrix[i][j] += pattern[i] * pattern[j];
                }
            }
        }

        //Set diagonals in the weight matrix to zero
        for (int i = 0; i < vectorLength; i++) {
            weightMatrix[i][i] = 0;
        }
        writeWeightMatrixToFile(fileToWrite, weightMatrix);
    }

    /**
     * Writes the given weight matrix to a text file in a space-separated format,
     * where each row of the matrix is written as a separate line in the file.
     * @param fileToWrite   the path of the file to write the weight matrix to
     * @param weightMatrix  a 2D integer array representing the weight matrix
     */
    public static void writeWeightMatrixToFile(String fileToWrite, int[][] weightMatrix) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite))) {
            int size = weightMatrix.length;

            for (int i = 0; i < size; i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < size; j++) {
                    row.append(weightMatrix[i][j]);
                    if (j < size - 1) {
                        row.append(" ");
                    }
                }
                writer.write(row.toString());
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a weight matrix from a text file where each line represents a row of
     * space-separated integers. This method reconstructs and returns the matrix
     * as a 2D integer array.
     * @param filePath  the path to the file containing the saved weight matrix
     * @return a 2D integer array representing the weight matrix read from the file
     */
    public static int[][] readWeightMatrixFromFile(String filePath) {
        List<int[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                int[] row = new int[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i]);
                }
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert list to 2D array
        return rows.toArray(new int[rows.size()][]);
    }


    /**
     * Runs the testing algorithim of the network until convergence
     * @param resultsFile The file to display the results of the testing in
     * @param savedTrainingData The file that contains the weight matrix that the network is using for the test
     * @param testSamples the data file that the net will be using as testing samples
     */
    public void testNetwork(String savedTrainingData, String resultsFile, String testSamples){
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
