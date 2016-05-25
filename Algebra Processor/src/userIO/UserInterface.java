package userIO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lang.Constant;
import lang.Expression;
import lang.IllegalDimensionException;
import lang.MathFormatException;
import lang.MathSet;
import lang.Matrix;
import lang.OverflowException;
import lang.Solution;
import lang.Term;

/**
 * GUI for running the Algebra Processor.
 *
 * @author Nikola Istvanic, Luke Senseney
 */
public class UserInterface extends Application implements Serializable {
    private static final long serialVersionUID = 01L;
    /**
     * String representing the character for i (square root of -1).
     */
    private static final String imagUnit = Term.IMAG_UNIT;
    /**
     * TextArea where algebraic output is displayed in the main GUI window.
     */
    private TextArea output;
    /**
     * TextField where algebraic expressions are entered.
     */
    private TextField algebra;
    /**
     * ComboBox which determines if rational numbers will be represented by
     * decimals or fractions.
     */
    private ComboBox<String> approx;
    /**
     * String seen inside approx.
     */
    private String ofApprox = "Exact";
    /**
     * Number of digits that a non-integer will be represented in.
     */
    private int ofDigits;
    /**
     * List of matrices created by the user and their names.
     */
    private Map<String, Matrix> matrices = new HashMap<>();
    /**
     * Two-dimensional array of TextFields which represents the matrix to be
     * entered by the user.
     */
    private TextField[][] input;
    /**
     * Font to display matrices so that every column of characters lines up.
     */
    private Font font = new Font("Courier New", 12.0);
    /**
     * String that represents which mode the processor is in.
     */
    private String mode = "Algebra";
    /**
     * Menu always present at the top of the processor.
     */
    private MenuBar menuBar = new MenuBar();

    /**
     * Method which creates a stage where the user may enter his or her
     * algebraic expressions.
     * @param stage The default stage.
     */
    @Override
    public void start(Stage stage) {
        MenuItem help = new MenuItem("Help");
        help.setOnAction(e -> {
            Stage helper = new Stage();
            helper.setTitle("Help Menu");
            TextArea helpArea = new TextArea("\tWelcome to AlgebraProcessor "
                    + "Beta! Simply enter an expression or equation in "
                    + "the top box, press enter or hit the enter key, and it "
                    + "will write it standard form, simplify it, factor "
                    + "it, and, if it is an equation, solve it. Use the \"^\" "
                    + "to enter exponents. To take the nth root of a "
                    + "number, raise it to 1/n. For instance, to take the "
                    + "square root of x, enter \"x^(1/2)\". To enter "
                    + imagUnit + ", the imaginary unit, press the buttion with "
                    + imagUnit + " on it. If " + imagUnit + " is showing up "
                    + "as a " + new String(Character.toChars(0x1F700))
                    + " to you, then your computer doesn't have the font "
                    + "Cambria Math on it. Simply pretend squares are the "
                    + "imaginary unit. The same holds true "
                    + "for the mathmatical constant e. Later versions will "
                    + "remove dependency on Cambria Math. It ignores "
                    + "whitespace, but will treat other, non-letter symbols"
                    + " like \"!\" as variables. \n\n\tCurrently it its "
                    + "capable of simplifying about anything, factoring out gcd"
                    + " and factoring quadratics. It can solve "
                    + "quadratics and 2 step equations.\n\n\tGarbage in, "
                    + "Garbage out; currently, if you enter anything that "
                    + "doesn't make mathmatical sense, it may give an error or"
                    + " it may try to interpret you tried to enter. "
                    + "Later version may fix this. This product is still in"
                    + " beta, so it may give garbage out anyway. If you "
                    + "enter a proper expression or equation and it gives an "
                    + "error or wrong answer, please email what you "
                    + "entered to yesennes@gmail.com.\n\n\tUpdates will come "
                    + "periodically, to receive them send an email to "
                    + "yesennes@gmail.com requesting to be put on the email "
                    + "list. Upcoming features include support for "
                    + "functions such as sine, better display with fractions "
                    + "actually stacked, better input with superscript "
                    + "and \u221as, factoring and solving of more complex "
                    + "expression, constants like \u03c0 and "
                    + new String(Character.toChars(0x1d4ee))
                    + " and a button for approximate answers. If you would "
                    + "like to see any other features, send an email to "
                    + "yesennes@gmail.com.\n\n\tP.S. I am still trying to think"
                    + " of a good name for this program. Any suggustions would"
                    + " be welcome.");
            helpArea.setWrapText(true);
            helpArea.setEditable(false);
            helper.setScene(new Scene(helpArea));
            helper.show();
        });

        MenuItem fullscreen = new MenuItem("Fullscreen");
        fullscreen.setOnAction(e -> stage.setFullScreen(true));
        fullscreen.setAccelerator(new KeyCodeCombination(KeyCode.F11));

        output = new TextArea();
        output.setWrapText(true);
        output.setEditable(false);

        Button imag = new Button(imagUnit);
        imag.setOnAction(e -> algebra.appendText(imagUnit));

        Button pi = new Button(String.valueOf(Term.PI));
        pi.setOnAction(e -> algebra.appendText(String.valueOf(Term.PI)));

        Button bE = new Button(Term.E);
        bE.setOnAction(e -> algebra.appendText(Term.E));

        Button nullSet = new Button("Null Set");
        nullSet.setOnAction(ns -> algebra.appendText("\u2205"));

        Button create = new Button("Create Matrix");
        create.setOnAction(a -> {
            Stage adding = new Stage();
            adding.setTitle("Create Matrix");
            adding.setMinHeight(400);
            adding.setMaxWidth(500);
            boolean[] created = {false};
            boolean[] displayed = {false};
            Button generate = new Button("Create matrix");
            HBox rows = new HBox(10), cols = new HBox(10),
                    names = new HBox(10);
            VBox together = new VBox(5);
            Label rowLabel = new Label("Enter the number of rows:"),
                    colLabel = new Label("Enter the number of columns:"),
                    nameLabel = new Label("Enter the name of the matrix:");
            TextField row = new TextField(), col = new TextField(),
                    name = new TextField();
            Button template = new Button("Template");

            col.disableProperty().bind(Bindings.isEmpty(row.textProperty()));

            template.disableProperty().bind(Bindings.or(Bindings.isEmpty(row
                    .textProperty()), Bindings.or(
                            Bindings.isEmpty(col.textProperty()),
                            Bindings.isEmpty(name.textProperty()))));

            generate.disableProperty().bind(Bindings.or(Bindings.isEmpty(row
                    .textProperty()), Bindings.or(
                            Bindings.isEmpty(col.textProperty()),
                            Bindings.isEmpty(name.textProperty()))));

            template.setOnAction(k -> {
                try {
                    if (!displayed[0]) {
                        input = new TextField[Integer.parseInt(row.getText())]
                                [Integer.parseInt(col.getText())];
                        created[0] = true;
                        VBox vert = new VBox(2);
                        for (int i = 0; i < input.length; i++) {
                            HBox temp = new HBox(4);
                            for (int j = 0; j < input[0].length; j++) {
                                input[i][j] = new TextField();
                                temp.getChildren().add(input[i][j]);
                            }
                            vert.getChildren().add(temp);
                        }
                        together.getChildren().add(vert);
                        displayed[0] = true;
                        input[input.length - 1][input[0].length - 1]
                                .setOnKeyReleased(p -> {
                            if (p.getCode() == KeyCode.ENTER) {
                                generate.fire();
                            }
                        });
                    }
                } catch (NumberFormatException exception) {
                    created[0] = false;
                    displayed[0] = false;
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Error");
                    alert.setHeaderText("Template " + name.getText()
                        + " not created.");
                    alert.setContentText(
                            "Dimension did not contain only integer values.");
                    alert.showAndWait();
                }
            });

            generate.setOnAction(p -> {
                Constant[][] temp = new Constant[input.length][input[0].length];
                if (created[0]) {
                    try {
                        for (int i = 0; i < input.length; i++) {
                            for (int j = 0; j < input[0].length; j++) {
                                temp[i][j] = new Constant(Integer.parseInt(
                                                input[i][j].getText()));
                            }
                        }
                        created[0] = false;
                        displayed[0] = false;
                        matrices.put(name.getText(), new Matrix(temp));
                    } catch (NumberFormatException exception) {
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Error");
                        alert.setHeaderText("Matrix " + name.getText()
                                + " not created.");
                        alert.setContentText("A value did not contain an "
                                + "integer. Matrix not added.");
                        alert.showAndWait();
                    }
                }
                adding.close();
            });

            name.setOnKeyReleased(k -> {
                if (k.getCode() == KeyCode.ENTER) {
                    template.fire();
                }
            });
            rows.getChildren().addAll(rowLabel, row, template);
            cols.getChildren().addAll(colLabel, col, generate);
            names.getChildren().addAll(nameLabel, name);
            together.getChildren().addAll(rows, cols, names);
            adding.setScene(new Scene(together));
            adding.show();
        });

        Button view = new Button("View");
        view.setOnAction(s -> {
            if (matrices.size() == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Matrices");
                alert.setHeaderText("No matrices to view.");
                alert.setContentText("Click \"Create Matrix\" button.");
                alert.showAndWait();
            } else {
                output.clear();
                for (Map.Entry<String, Matrix> entry : matrices.entrySet()) {
                    Matrix m = entry.getValue();
                    output.appendText(entry.getKey() + " " + m.row + " x "
                            + m.col + "\n" + m + "\n");
                }
            }
        });

        int i = 0;
        Button add = new Button("Add");
        setMatrixButton(add, i++);

        Button subtract = new Button("Subtract");
        setMatrixButton(subtract, i++);

        Button multiply = new Button("Multiply");
        setMatrixButton(multiply, i++);

        Button gaussJordan = new Button("Gauss-Jordan");
        setMatrixButton(gaussJordan, i++);

        Button factor = new Button("LU Factorization");
        setMatrixButton(factor, i++);

        Button determinant = new Button("Determinant");
        setMatrixButton(determinant, i++);

        Button inverse = new Button("Inverse");
        setMatrixButton(inverse, i++);

        Button matHelp = new Button("?");
        matHelp.setOnAction(h -> {
            Stage helpMenu = new Stage();
            helpMenu.setTitle("Help Menu");
            TextArea helpArea = new TextArea("\t Matrix menu where matrices"
                    + " can be created, operated on, and solved. Enter the "
                    + "values for the number of rows and number of columns "
                    + "in the matrix to be created. Matrices can be added, "
                    + "subtracted, and multiplied by one another. For each"
                    + "matrix, its LU Factorization, determinant, inverse,"
                    + "and solution via The Gauss-Jordan Elimination method"
                    + "can be computed. \n \t As of now, the eigenvalues, "
                    + "eigenspace, and null space of a matrix cannot be "
                    + "computed. \n \t In order to use this menu, first"
                    + "create a new matrix (or two if another is required),"
                    + " then click the button of the desired operation and"
                    + "select the matrix/matrices wanted to perform the "
                    + "operation on.");
            helpArea.setWrapText(true);
            helpArea.setEditable(false);
            helpMenu.setScene(new Scene(helpArea));
            helpMenu.setResizable(false);
            helpMenu.show();
        });

        MenuItem approximation = new MenuItem("Approximation");
        approximation.setOnAction(e -> {
            Stage set = new Stage();
            set.setResizable(false);
            VBox setting = new VBox(5);
            HBox first = new HBox(5), second = new HBox(5);

            ObservableList<String> choices
                = FXCollections.observableArrayList();
            choices.addAll("Exact", "Approximation");
            approx = new ComboBox<>(choices);
            approx.setValue(ofApprox);
            approx.setPrefWidth(393);

            Label descrip = new Label("All non-rational numbers will be kept "
                    + "in as mathmatical constants and exponents.");

            Spinner<Integer> digits = new Spinner<>(0, 100, ofDigits);
            digits.setPrefWidth(375);
            digits.setDisable(approx.getValue().equals("Exact"));
            Label firstLabel = new Label("Mode:"), secondLabel
                    = new Label("Precision:");

            approx.setOnAction(event -> {
                if (approx.getValue().equals("Exact")) {
                    descrip.setText("All non-rational numbers will be kept in "
                            + "as mathmatical constants and exponents.");
                    digits.setDisable(true);
                } else {
                    descrip.setText("All non-rational numbers will be "
                            + "approximated to the nearest whole number.");
                    digits.setDisable(false);
                }
                ofApprox = approx.getValue();
            });

            digits.setOnMouseClicked(event -> {
                ofDigits = digits.getValue();
                descrip.setText("All non-rational numbers will be approximated "
                        + "to " + ((ofDigits == 0) ? "the nearest whole number."
                                : ofDigits + ((ofDigits == 1) ? " place "
                                        : " places ")
                                + "after the decimal."));
            });
            first.getChildren().addAll(firstLabel, approx);
            second.getChildren().addAll(secondLabel, digits);
            setting.getChildren().addAll(first, second, descrip);
            set.setScene(new Scene(setting));
            set.show();
        });

        ToggleGroup tg = new ToggleGroup();
        RadioMenuItem mAlgebra = new RadioMenuItem("Algebra");
        mAlgebra.setSelected(true);
        mAlgebra.setToggleGroup(tg);
        mAlgebra.setOnAction(e -> {
            mode = mAlgebra.getText();
            generateLayout(stage, new Button[]{imag, pi, bE});
        });
        RadioMenuItem mMatrix = new RadioMenuItem("Matrix");
        mMatrix.setToggleGroup(tg);
        mMatrix.setOnAction(e -> {
            mode = mMatrix.getText();
            generateLayout(stage, new Button[]{create, view, add,
                    subtract, multiply, gaussJordan, factor,
                    determinant, inverse, matHelp});
        });
        RadioMenuItem mSet = new RadioMenuItem("Set");
        mSet.setToggleGroup(tg);
        mSet.setOnAction(e -> {
            mode = mSet.getText();
            generateLayout(stage, new Button[]{nullSet});
        });

        Menu modes = new Menu("Modes");
        modes.getItems().addAll(mAlgebra, mMatrix, mSet);

        Menu settings = new Menu("Settings");
        settings.getItems().addAll(approximation, fullscreen, help);

        menuBar.getMenus().addAll(modes, settings);

        generateLayout(stage, new Button[]{imag, pi, bE});

        stage.setTitle("Algebra Processor");
        stage.show();
    }

    /**
     * Method that sets the onActionListener of the button given in the
     * parameter, based on which button it is (given by the int parameter).
     * @param button The button whose onActionListener is set.
     * @param i Int whose value determines which button it is: 0 is add,
     * 1 is subtract, 2 is multipy, 3 is gaussJordan, 4 is factor, 5 is
     * determinant, 6 is inverse.
     */
    public void setMatrixButton(Button button, int i) {
        button.setOnAction(e -> {
            if (matrices.size() == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Matrices");
                alert.setHeaderText("No matrices to " + (i == 0 ? "add"
                        : i == 1 ? "subtract" : i == 2 ? "multiply"
                        : i == 3 ? "solve" : i == 4 ? " factor"
                        : i == 5 ? "compute determinant" : "compute inverse")
                        + ".");
                alert.setContentText("Click \"Create Matrix\" button.");
                alert.showAndWait();
            } else {
                Stage determin = new Stage();
                determin.setTitle(i == 0 ? "Addition" : i == 1 ? "Subtraction"
                        : i == 2 ? "Multiplication" : i == 3
                        ? "Gauss-Jordan Elimination" : i == 4
                        ? "LU Factorization" : i == 5 ? "Determinant"
                                : "Inverse");
                HBox scenery = new HBox();
                VBox layers = new VBox();
                TextArea display = new TextArea();
                display.setFont(font);
                display.setEditable(false);
                Button[] buttons = new Button[Math.min(4, matrices.size())];
                int j = 0, k = 0;
                boolean[] first = {true};
                Matrix[] firstMatrix = {null};
                for (Map.Entry<String, Matrix> entry : matrices.entrySet()) {
                    Button name = new Button(entry.getKey());
                    name.setOnAction(c -> {
                        try {
                            if (i == 0 || i == 1 || i == 2) {
                                // Binary operations
                                if (first[0]) {
                                    first[0] = false;
                                    firstMatrix[0] = matrices.get(
                                            name.getText());
                                    display.setText(firstMatrix[0] + "\n\n");
                                } else {
                                    first[0] = true;
                                    Matrix secondMatrix
                                        = matrices.get(name.getText());
                                    display.appendText(secondMatrix + (i == 0 ? " +"
                                            : i == 1 ? " -" : " *") + "\n");
                                    for (int g = 0;
                                            g < 2 * secondMatrix.col + 5;
                                            g++) {
                                        display.appendText("-");
                                    }
                                    display.appendText(
                                            "\n" + (i == 0 ? firstMatrix[0]
                                                    .add(secondMatrix)
                                            : i == 1 ? firstMatrix[0]
                                                    .subtract(secondMatrix)
                                            : firstMatrix[0]
                                                    .multiply(secondMatrix)));
                                }
                            } else {
                                display.setText(name.getText() + "\n");
                                if (i == 3) {
                                    Matrix jordan = matrices.get(
                                            name.getText());
                                    display.appendText("\n" + jordan
                                            + "\n\n" + jordan.gaussJordan());
                                } else if (i == 4) {
                                    Matrix factorize = matrices.get(
                                            name.getText());
                                    Matrix[] lu = factorize.LUFactorization();
                                    display.appendText("A:\n" + factorize
                                            + "\n\nP:\n" + lu[0] + "\n\nL:\n"
                                            + lu[1] + "\n\nU:\n" + lu[2]);
                                } else if (i == 5) {
                                    display.setText(name.getText() + "\n"
                                            + matrices.get(name.getText())
                                            + "\n\nDeterminant: " + matrices
                                            .get(name.getText())
                                            .determinant());
                                } else {
                                    display.setText(name.getText() + "\n"
                                            + matrices.get(name.getText())
                                            + "\n\nInverse:\n" + matrices
                                            .get(name.getText()).inverse());
                                }
                            }
                        } catch (IllegalDimensionException iDE) {
                            display.setText(iDE.getMessage());
                        }
                    });
                    buttons[j] = name;
                    j++;
                    k++;
                    if (j > 3) {
                        layers.getChildren().addAll(new ToolBar(buttons));
                        buttons = new Button[Math.min(4, matrices.size() - k)];
                        j = 0;
                    }
                }
                if (j != 0) {
                    layers.getChildren().addAll(new ToolBar(buttons));
                }
                scenery.getChildren().addAll(layers, display);
                determin.setScene(new Scene(scenery));
                determin.show();
            }
        });
    }

    /**
     * Method that generates the layout of individual modes. Algebra and Set
     * mode share the same basic layout. If the mode is Algebra, the method sets
     * the action listener of the Enter button to evaluate whatever expression
     * is entered in the TextField. If the mode is Set, the method sets the on
     * action listener of the Enter button to create a new MathSet from the text
     * in the TextField. These two modes share the same BorderPane layout.
     * If the mode is Matrix, the window is set such that it only contains a bar
     * of buttons (no TextField or Enter button). All modes share the same lower
     * area of the BorderPane.
     * @param stage The default stage.
     * @param buttons Array of buttons that belong to the toolbar placed at the
     * top of the stage.
     */
    public void generateLayout(Stage stage, Button[] buttons) {
        BorderPane main = new BorderPane();
        Label currMode = new Label("Mode: " + mode);
        if (mode.equals("Algebra") || mode.equals("Set")) {
            AnchorPane topAnchor = new AnchorPane();
            VBox topmost = new VBox();
            HBox top = new HBox(5);

            output.setFont(new Font("System Regular", 12.0));
            output.setText(mode.equals("Algebra") ? "Enter an equation in the "
                    + "box above, or click on the \"?\" in the bottom right \""
                    + "for more help." : "Enter a set in the form "
                            + "of \"{A, B, C}\"");

            Button enter = new Button("Enter");
            enter.setOnAction(e -> {
                if (mode.equals("Algebra")) {
                    try {
                        // Creates an Expression and factors it,
                        // then if it is a equation, solves it.
                        boolean appro = ofApprox.equals("Exact");
                        Expression exp = new Expression(algebra.getText());
                        output.setText("Standard Form:" + (appro ? exp
                                : exp.approx().toStringDecimal(ofDigits))
                                + "\n");
                        output.appendText("Factored:");
                        ArrayList<Expression> facts = exp.factor();
                        for (Expression fact : facts) {
                            output.appendText("(" + (appro ? fact
                                    : fact.approx().toStringDecimal(ofDigits))
                                    + ")");
                        }
                        if (exp.isEquation) {
                            output.appendText("=0");
                        }
                        output.appendText("\n");
                        if (exp.isEquation) {
                            output.appendText("Solutions:");
                            HashSet<Solution> sols = exp.solve();
                            if (sols.size() == 0) {
                                output.appendText(" Was not able to solve.");
                            } else {
                                for (Solution current : sols) {
                                    output.appendText((appro ? current
                                            : current.approx(ofDigits))
                                            + "\n                    ");
                                }
                            }
                        }
                    } catch (MathFormatException format) {
                        output.setText("There was an error in your formatting. "
                                + format.getMessage()
                                + " If you think what you entered was "
                                + "formatted correctly, please email what you"
                                + " entered to yesennes@gmail.com");
                    } catch (OverflowException over) {
                        output.setText("A number was to big. "
                                + over.getMessage());
                    } catch (Exception a) {
                        output.setText("An error occured. Please email what "
                                + "you entered to yesennes@gmail.com");
                        a.printStackTrace();
                    }
                } else {
                    try {
                        MathSet set = algebra.getText().isEmpty()
                                ? new MathSet()
                                : new MathSet(algebra.getText());
                        output.setText(set.toString());
                    } catch (IllegalArgumentException iae) {
                        output.setText(iae.getMessage());
                    }
                }
            });

            algebra = new TextField();
            algebra.setOnKeyReleased(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    enter.fire();
                }
            });

            top.getChildren().addAll(algebra, enter);
            topAnchor.getChildren().addAll(algebra, enter);
            AnchorPane.setRightAnchor(enter, 4.0);
            AnchorPane.setLeftAnchor(algebra, 4.0);
            AnchorPane.setRightAnchor(algebra, 50.0);

            topmost.getChildren().addAll(
                    topAnchor, new ToolBar(buttons), output);
            main.setTop(new VBox(menuBar, topmost));
        } else {
            output.setFont(font);
            output.setText("Create a matrix first by using"
                    + " the \"Create Matrix\" Button.");
            main.setTop(new VBox(menuBar, new ToolBar(buttons)));
        }
        main.setBottom(currMode);
        main.setCenter(output);

        Platform.runLater(() -> algebra.requestFocus());
        // To get focus off the TextArea and on the TextField.

        stage.setScene(new Scene(main));
    }

    /**
     * Main method to launch the GUI.
     * @param args Command-line String array.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
