
/**
 * Project 2: Discrete Hopfield Neural Networks 
 * Authors: Jonathan Rivera, Gianpaolo Tabora, Kian Drees, Precee-Noel Ginigeme
 * Class: COMP 380, Neural Networks
 * Prof: Eric Jiang
 * Due Date: 4/10/2025
 */
import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class HopfieldNet {
    
    public static int rowDim;
    public static int colDim;

    public static void main(String[] args) {
        receiveInput();
    }

    public static void receiveInput() {
        Scanner scanner = new Scanner(System.in);

        int action = 0;
        do {
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
        } while (action != 3);
        System.out.println("Thank you for using the Net. Come back soon!");
        scanner.close();
    }

    public static void trainingSpecs(Scanner scanner) {
        scanner.nextLine(); // get rid of newline read from previous nextInt()
        System.out.println("Enter the training data file name:");
        String trainingDataFileName = scanner.nextLine();

        System.out.println("Enter a file name to save the trained weight values:");
        String outputWeightFileName = scanner.nextLine();

        trainNetwork(outputWeightFileName, trainingDataFileName);

    }

    public static void testingSpecs(Scanner scanner) {
        scanner.nextLine(); // get rid of newline read from previous nextInt()
        System.out.println("Enter the saved weights file name:");
        String savedWeightsFilename = scanner.nextLine();

        System.out.println("Enter the testing data file name:");
        String testingDataFilename = scanner.nextLine();

        System.out.println("Enter the file name to save your results to:");
        String resultsFilename = scanner.nextLine();

        testNetwork(savedWeightsFilename, testingDataFilename, resultsFilename);

    }

    public static ArrayList<int[]> readData(String filename) {
        /**
         * Reads in a file and transforms the data into numbers to work with
         * 
         * @param filename The name of the file to read
         * @return the input vectors, all transated into number representation
         */

        int rowDimension; //the first line in the file
        int colDimension; //the second line in the file
        int numOfInputs; //the third line in the file

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line; // reused var, for when contents from readline are read

            //Header data
            rowDimension = Integer.parseInt(reader.readLine().trim().split("\\s+")[0]);
            colDimension = Integer.parseInt(reader.readLine().trim().split("\\s+")[0]);
            rowDim = rowDimension;
            colDim = colDimension;
          
            numOfInputs = Integer.parseInt(reader.readLine().trim().split("\\s+")[0]);

            reader.readLine();

            ArrayList<int[]> list = new ArrayList<>(); // a list of input vectors
            // rest of the file
            for (int patternNum = 0; patternNum < numOfInputs; patternNum++) {

                int[] tempArray = new int[rowDimension * colDimension];
              
                int tempArrayIndex = 0;

                for (int i = 0; i < rowDimension; i++) {
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
            System.out.println("Training file was successfully read: " + filename);
            return list;
        } catch (IOException e) {
            // Handle file reading errors
            e.printStackTrace();
        }
        System.out.println("System failed to read training file data: " + filename);
        return null;

    }

    public static void trainNetwork(String fileToWrite, String trainingData) {
        /**
         * Trains the network by creating a weight matrix and writes the saved weight
         * matrix to a specified file
         * 
         * @param fileToWrite  The name of the file to write to, creating it if it does
         *                     not exist
         * @param trainingData The file that contains the data the network will train
         */
        ArrayList<int[]> inputVectors = readData(trainingData);
        int numberOfPatterns = inputVectors.size();
        int vectorLength = inputVectors.get(0).length;

        int[][] weightMatrix = new int[colDim*rowDim][colDim*rowDim];

        // Build weight matrix with outer product
        for (int[] pattern : inputVectors) {
            for (int i = 0; i < vectorLength; i++) {
                for (int j = 0; j < vectorLength; j++) {
                    weightMatrix[i][j] += pattern[i] * pattern[j];
                }
            }
        }

        // Set diagonals in the weight matrix to zero
        for (int i = 0; i < vectorLength; i++) {
            weightMatrix[i][i] = 0;
        }
        writeWeightMatrixToFile(fileToWrite, weightMatrix);
    }

    public static void writeWeightMatrixToFile(String fileToWrite, int[][] weightMatrix) {
        /**
         * Writes the given weight matrix to a text file in a space-separated format,
         * where each row of the matrix is written as a separate line in the file.
         * 
         * @param fileToWrite  the path of the file to write the weight matrix to
         * @param weightMatrix a 2D integer array representing the weight matrix
         */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToWrite))) {
            int size = weightMatrix.length;
            
            writer.write(rowDim + " //row length");
            writer.newLine();
            writer.write(colDim + " //col length");
            writer.newLine();
            writer.newLine();

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

    public static int[][] readWeightMatrixFromFile(String filePath) {
        /**
         * Reads a weight matrix from a text file where each line represents a row of
         * space-separated integers. This method reconstructs and returns the matrix
         * as a 2D integer array.
         * 
         * @param filePath the path to the file containing the saved weight matrix
         * @return a 2D integer array representing the weight matrix read from the file
         */
        List<int[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            rowDim = Integer.parseInt(reader.readLine().trim().split("\\s+")[0]);
            colDim = Integer.parseInt(reader.readLine().trim().split("\\s+")[0]);
            reader.readLine();
            
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

    public static void testNetwork(String savedWeightsFilename, String testingDataFilename, String resultsFilename) {
        /**
         * Runs the testing algorithim of the network until convergence
         * 
         * @param savedWeightsFilename The file that contains the weight matrix that the
         *                             network is using for the test
         * @param testingDataFilename  the data file that the net will be using as
         *                             testing samples
         * @param resultsFilename      The file to display the results of the testing in
         */

        // initialize the weight matrix after taking it from the file
        int[][] weightMatrix = readWeightMatrixFromFile(savedWeightsFilename);

        // Store the original test vector before random selection
        ArrayList<int[]> testingData = readData(testingDataFilename);

        Random rand = new Random();
        int testIndex = 1;

        // try-catch for writing output file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFilename))) {
            for (int[] testVector : testingData) {
                int[] originalInput = testVector.clone();
                boolean converged = false;

                while (!converged) {
                    // Generate list of all indices and shuffle
                    List<Integer> indices = new ArrayList<>();
                    for (int idx = 0; idx < testVector.length; idx++) {
                        indices.add(idx);
                    }
                    java.util.Collections.shuffle(indices, rand);

                    int[] newVector = testVector.clone();

                    for (int i : indices) {
                        // Compute y_in_i = x_i + sum of y_j * w_ji
                        int y_in_i = testVector[i]; // This is x_i
                        for (int j = 0; j < testVector.length; j++) {
                            if (j != i) {
                                y_in_i += testVector[j] * weightMatrix[j][i];
                            }
                        }

                        // Apply activation function
                        int y_i = activationFunction(y_in_i, testVector[i]);
                        newVector[i] = y_i; // Update in new vector
                    }

                    // Check convergence
                    converged = true;
                    for (int k = 0; k < testVector.length; k++) {
                        if (newVector[k] != testVector[k]) {
                            converged = false;
                            break;
                        }
                    }

                    // Replace testVector with updated values
                    testVector = newVector.clone();
                }

                // Format and write results to output file
                writer.write("Test image #" + testIndex++ + ":\n");
                writer.write("Input test image:\n");
                writeVectorAsImage(writer, originalInput, rowDim,colDim);
                writer.write("\nThe associated stored image:\n");
                writeVectorAsImage(writer, testVector, rowDim,colDim);
                writer.write("\n-------------------------------------------\n\n");
            }
        }

        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function that converts vector into an image
     * 
     * @param writer
     * @param vector
     * @param dimension
     * @throws IOException
     */
    public static void writeVectorAsImage(BufferedWriter writer, int[] vector, int rowDim,int colDim) throws IOException {
        for (int i = 0; i < rowDim; i++) {
            StringBuilder line = new StringBuilder();
            for (int j = 0; j < colDim; j++) {
                int val = vector[i * colDim + j];
                line.append(val == 1 ? "O" : " ");
            }
            writer.write(line.toString());
            writer.newLine();
        }
    }

    /**
     * Helper function to apply the activation function
     * 
     * @param y_in
     * @param previous_y_i
     * @return 1, -1, or previous_y_i
     */
    public static int activationFunction(int y_in, int previous_y_i) {
        if (y_in > 0) {
            return 1;
        } else if (y_in < 0) {
            return -1;
        } else {
            return previous_y_i;
        }
    }
}
