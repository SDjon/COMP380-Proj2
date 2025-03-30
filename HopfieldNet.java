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
}
