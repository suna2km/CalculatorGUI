import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * JavaFX Calculator Application
 *
 * This application provides a graphical calculator interface using JavaFX.
 * It supports basic arithmetic operations, advanced math functions, memory
 * operations, theme switching, and calculation history tracking.
 *
 * @author Suna Aregay
 * @see <a href="{https://github.com/suna2km/CalculatorGUI</a>
 */
public class JavaFxCalculator extends Application {

    /** Displays current input and results */
    private TextField tfDisplay;     // top level text field

    // For computation
    private double result = 0.0;     // Result of computation
    private String inStr = "0";      // used to control what the text field(tfDisplay) is displaying
    private char lastOperator = ' '; // Previous operator: ' '(nothing), '+', '-', '*', '/', '='

    private Text memoryText;         // Displays current memory value in UI
    private double memory = 0.0;     //Stores memory value for M+, M-, MR, MC
    private TextArea calcHistory;    //Displays calculation history

    /**
     * Central event handler for all 28 calculator buttons.
     * Determines behavior based on button label.
     */
    EventHandler handler = evt -> {

        // Get button label dynamically
        String currentBtnLabel = ((Button)evt.getSource()).getText();

        // Convert current input string to number for calculations
        double inNum = Double.parseDouble(inStr);

        switch (currentBtnLabel) {

            // Handles decimal input, prevents multiple decimals
            case ".":
                if (inStr.equals("0") || !inStr.contains(".")) {
                    inStr += ".";
                    tfDisplay.setText(inStr);
                }
                break;

            // Number buttons
            case "0": case "1": case "2": case "3": case "4":
            case "5": case "6": case "7": case "8": case "9":
                // Prevent leading zero
                if (inStr.equals("0")) {
                    inStr = currentBtnLabel;  // no leading zero
                } else {
                    inStr += currentBtnLabel; // append input digit
                }
                tfDisplay.setText(inStr);

                // Clear buffer if last operator is '='
                if (lastOperator == '=') {
                    result = 0;
                    lastOperator = ' ';
                }
                break;

            // Operator buttons: '+', '-', 'x', '/' and '='
            case "+":
                compute();
                lastOperator = '+';
                break;
            case "-":
                compute();
                lastOperator = '-';
                break;
            case "*":
                compute();
                lastOperator = '*';
                break;
            case "÷":
                compute();
                lastOperator = '÷';
                break;
            case "^":
                compute();
                lastOperator = '^';
                break;
            case "√":
                if (this.lastOperator != '=') {
                    result += inNum;    // set result to current input before taking sqrt
                }
                double beforeSqrt = result;
                result = Math.sqrt(result);
                inStr = ""+ result;
                tfDisplay.setText(inStr);

                // Update history
                calcHistory.appendText("√" + beforeSqrt + " = " + result + "\n");

                lastOperator = '=';
                inStr = "0";
                break;
            case "=":
                compute();
                lastOperator = '=';
                break;

            // Memory add
            case "M+":
                if (this.lastOperator != '='){
                    memory += inNum;
                } else
                    memory += result;
                memoryText.setText("Memory = " + memory);
                break;

            // Memory subtract
            case "M-":
                if (this.lastOperator != '='){
                    memory -= inNum;
                } else
                    memory -= result;
                memoryText.setText("Memory = " + memory);
                break;

            // Memory recall
            case "MR":
                inStr = String.valueOf(memory);
                tfDisplay.setText(inStr);
                break;

            // Memory clear
            case "MC":
                memoryText.setText("Memory = 0.0");
                memory = 0;
                break;

            // Backspace logic
            case "←":
                if (inStr.length() == 1){
                    inStr = "0";
                } else
                    inStr = inStr.substring(0,inStr.length()-1);
                tfDisplay.setText(inStr);
                break;

            // Clear button
            case "C":
                result = 0;
                inStr = "0";
                lastOperator = ' ';
                tfDisplay.setText("0");
                break;

            // Clear current entry
            case "CE":
                inStr = "0";
                tfDisplay.setText("0");
                break;
        }
    };

    /** User pushes '+', '-', '*', '/' or '=' button.
    * Perform computation on the previous result and the current input number,
    * based on the previous operator.
    */
    private void compute() {
        double inNum = Double.parseDouble(inStr);

        // Build readable expression before updating result to use in calcHistory
        String expression = result + " " + lastOperator + " " + inNum;

        inStr = "0";
        if (lastOperator == ' ') {
            result = inNum;
        } else if (lastOperator == '+') {
            result += inNum;
        } else if (lastOperator == '-') {
            result -= inNum;
        } else if (lastOperator == '*') {
            result *= inNum;
        } else if (lastOperator == '÷') {
            result /= inNum;
        } else if(lastOperator == '^'){
            result = Math.pow(result,inNum);
        } else if (lastOperator == '=') {
            // Keep the result for the next operation
        }
        tfDisplay.setText(result + "");

        // Append only meaningful operations to history
        if (lastOperator != ' ') {
        calcHistory.appendText(expression + " = " + result + "\n");
        }
    }

    /**
     * Initializes and builds the JavaFX UI.
     *
     * @param primaryStage main application window
     */
    @Override
    public void start(Stage primaryStage) {

        Button[] btns;              // 28 buttons
        String[] btnLabels = {      // Labels of 28 buttons
                "Off","Dark", "Light", "+",
                "7", "8", "9", "-",
                "4", "5", "6", "*",
                "1", "2", "3","÷",  //÷ div
                ".", "0", "=", "√", //√ square root
                "C","CE","←","^",   //← backspace ^ power
                "M+","M-","MR","MC"
        };

        // Setup the Display TextField
        tfDisplay = new TextField("0");
        tfDisplay.setEditable(false);
        tfDisplay.setAlignment(Pos.CENTER_RIGHT);

        // Setup a GridPane for 4x4 Buttons
        int numCols = 4;
        GridPane paneButton = new GridPane();
        paneButton.setPadding(new Insets(15, 0, 15, 0));  // top, right, bottom, left
        paneButton.setVgap(5);  // Vertical gap between nodes
        paneButton.setHgap(5);  // Horizontal gap between nodes

        // Setup 4 columns of equal width, fill parent
        ColumnConstraints[] columns = new ColumnConstraints[numCols];
        for (int i = 0; i < numCols; ++i) {
            columns[i] = new ColumnConstraints();
            columns[i].setHgrow(Priority.ALWAYS) ;  // Allow column to grow
            columns[i].setFillWidth(true);          // Ask nodes to fill space for column
            paneButton.getColumnConstraints().add(columns[i]);
        }

        // Setup up the scene graph rooted at a BorderPane (of 5 zones)
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15, 15, 15, 15));  // top, right, bottom, left

        // Setup 28 Buttons and add to GridPane; and event handler
        btns = new Button[28];
        for (int i = 0; i < btns.length; ++i) {
            btns[i] = new Button(btnLabels[i]); //instantiate the new button
            btns[i].setOnAction(handler);       // Register event handler
            btns[i].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);// full-width

            // Override behavior for special buttons
            switch (btnLabels[i]){

                case "M+": case "M-": case "MR": case "MC": //set color for more than one button
                    btns[i].setStyle("-fx-color: #6495ed");
                    break;

                case "+": case "-": case "*": case "÷": case "√": case "^":
                    btns[i].setStyle("-fx-color: #FFB6C1");
                    break;

                case ".": case "=":
                    btns[i].setStyle("-fx-color: #ffe4c4");
                    break;

                case "C": case "CE": case "←":
                    btns[i].setStyle("-fx-color: #DDA0DD");
                    break;

                case "Off":
                    btns[i].setStyle("-fx-color: #8a2b2b");
                    btns[i].setOnAction(actionEvent -> Platform.exit());
                    break;

                case "Dark":
                    btns[i].setStyle("-fx-text-fill: white; -fx-background-color: black;");
                    btns[i].setOnAction(actionEvent -> {
                        root.setStyle("-fx-text-fill: white; -fx-background-color: black;");
                        memoryText.setStyle("-fx-fill: white;");
                    });
                    break;

                case "Light":
                    btns[i].setStyle("-fx-background-color: #F5F5DC;");
                    btns[i].setOnAction(actionEvent -> {
                        root.setStyle("-fx-text-fill: black; -fx-background-color: white;");
                        memoryText.setStyle("-fx-fill: black;");
                    });
                    break;
            }
            paneButton.add(btns[i], i % numCols, i / numCols);  // control, col, row
        }

        root.setTop(tfDisplay);                   // Top zone contains the TextField
        root.setCenter(paneButton);               // Center zone contains the GridPane of Buttons

        memoryText = new Text("Memory = 0.0"); //instantiate a new memory control
        root.setBottom(memoryText);               //add the memory Text GUI control to bottom of screen

        calcHistory = new TextArea(" ");       //set up calculation history field
        calcHistory.setEditable(false);

        VBox bottomBox = new VBox();              //new Vbox that will contain both memoryText & calcHistory
        bottomBox.getChildren().addAll(memoryText, calcHistory);

        root.setBottom(bottomBox);                //places memoryText & calcHistory at the bottom

        // Set up scene and stage
        primaryStage.setScene(new Scene(root, 300, 400));
        primaryStage.setTitle("JavaFX Calculator");
        primaryStage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}