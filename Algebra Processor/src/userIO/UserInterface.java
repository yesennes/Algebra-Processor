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
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
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
     * Label at the bottom of the processor which states the current mode.
     */
    private Label currentMode;
    /**
     * Buttons to be displayed based on which mode the processor is in.
     */
    private ToolBar buttons;
    /**
     * Button which appends the imaginary number i to the TextField in Algebra
     * mode.
     */
    private Button imag;
    /**
     * Button which appends the symbol for pi to the TextField in Algebra mode.
     */
    private Button pi;
    /**
     * Button which appends the symbol for e to the TextField in Algebra mode.
     */
    private Button bE;
    /**
     * Button which appends the null set symbol to the Set mode TextField.
     */
    private Button nullSet;
    /**
     * Button used in Matrix mode to create matrices.
     */
    private Button create;
    /**
     * Button in Matrix mode to view matrices created thus far.
     */
    private Button view;
    /**
     * Button used only in Matrix mode to add two matrices together.
     */
    private Button add;
    /**
     * Button in Matrix mode to subtract two matrices.
     */
    private Button subtract;
    /**
     * Button used to multiply two matrices in Matrix mode.
     */
    private Button multiply;
    /**
     * Button used to solve a matrix in Matrix mode.
     */
    private Button gaussJordan;
    /**
     * Button in Matrix mode to factor a matrix.
     */
    private Button factor;
    /**
     * Button to find the determinant of a matrix.
     */
    private Button determinant;
    /**
     * Button to find the inverse of a matrix.
     */
    private Button inverse;
    /**
     * Help button for Matrix mode.
     */
    private Button matHelp;

    /**
     * Method which creates a stage where the user may enter his or her
     * algebraic expressions.
     * @param stage The default stage.
     */
    @Override
    public void start(Stage stage) {
        output = new TextArea();
        output.setWrapText(true);
        output.setEditable(false);

        Button help = new Button("?");
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

        imag = new Button(imagUnit);
        imag.setOnAction(e -> algebra.appendText(imagUnit));

        pi = new Button(String.valueOf(Term.PI));
        pi.setOnAction(e -> algebra.appendText(String.valueOf(Term.PI)));

        bE = new Button(Term.E);
        bE.setOnAction(e -> algebra.appendText(Term.E));

        nullSet = new Button("Null Set");
        nullSet.setOnAction(ns -> {
            algebra.appendText("\u2205");
        });

        create = new Button("Create Matrix");
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

        view = new Button("View");
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

        add = new Button("Add");
        add.setOnAction(a -> {
            if (matrices.size() == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Matrices");
                alert.setHeaderText("No matrices to add.");
                alert.setContentText("Click \"Create Matrix\" button.");
                alert.showAndWait();
            } else {
                Stage addition = new Stage();
                addition.setTitle("Addition");
                HBox scenery = new HBox();
                VBox layers = new VBox();
                TextArea display = new TextArea();
                display.setFont(font);
                display.setEditable(false);
                Button[] buttons = new Button[Math.min(4, matrices.size())];
                int i = 0, j = 0;
                boolean[] first = {true};
                Matrix[] firstMatrix = {null};
                for (Map.Entry<String, Matrix> entry : matrices.entrySet()) {
                    Button button = new Button(entry.getKey());
                    button.setOnAction(c -> {
                        try {
                            if (first[0]) {
                                first[0] = false;
                                firstMatrix[0] = matrices.get(button.getText());
                                display.setText(firstMatrix[0] + "\n\n");
                            } else {
                                first[0] = true;
                                Matrix secondMatrix
                                    = matrices.get(button.getText());
                                String toString = secondMatrix.toString();
                                display.appendText(toString + " + \n");
                                for (int g = 0; g < 2 * secondMatrix.col + 5;
                                        g++) {
                                    display.appendText("-");
                                }
                                display.appendText("\n"
                                        + firstMatrix[0].add(secondMatrix));
                            }
                        } catch (IllegalDimensionException iDE) {
                            display.setText(iDE.getMessage());
                        }
                    });
                    buttons[i] = button;
                    i++;
                    j++;
                    if (i > 3) {
                        layers.getChildren().addAll(new ToolBar(buttons));
                        buttons = new Button[Math.min(4, matrices.size() - j)];
                        i = 0;
                    }
                }
                if (i != 0) {
                    layers.getChildren().addAll(new ToolBar(buttons));
                }
                scenery.getChildren().addAll(layers, display);
                addition.setScene(new Scene(scenery));
                addition.show();
            }
        });

        subtract = new Button("Subtract");
        subtract.setOnAction(s -> {
            if (matrices.size() == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Matrices");
                alert.setHeaderText("No matrices to subtract.");
                alert.setContentText("Click \"Create Matrix\" button.");
                alert.showAndWait();
            } else {
                Stage subtraction = new Stage();
                subtraction.setTitle("Subtraction");
                HBox scenery = new HBox();
                VBox layers = new VBox();
                TextArea display = new TextArea();
                display.setFont(font);
                display.setEditable(false);
                Button[] buttons = new Button[Math.min(4, matrices.size())];
                int i = 0, j = 0;
                boolean[] first = {true};
                Matrix[] firstMatrix = {null};
                for (Map.Entry<String, Matrix> entry : matrices.entrySet()) {
                    Button button = new Button(entry.getKey());
                    button.setOnAction(c -> {
                        try {
                            if (first[0]) {
                                first[0] = false;
                                firstMatrix[0] = matrices.get(button.getText());
                                display.setText(firstMatrix[0] + "\n\n");
                            } else {
                                first[0] = true;
                                Matrix secondMatrix = matrices.get(
                                        button.getText());
                                String toString = secondMatrix.toString();
                                display.appendText(toString + " - \n");
                                for (int g = 0; g < 2 * secondMatrix.col + 5;
                                        g++) {
                                    display.appendText("-");
                                }
                                display.appendText("\n" + firstMatrix[0]
                                        .subtract(secondMatrix).toString());
                            }
                        } catch (IllegalDimensionException iDE) {
                            display.setText(iDE.getMessage());
                        }
                    });
                    buttons[i] = button;
                    i++;
                    j++;
                    if (i > 3) {
                        layers.getChildren().addAll(new ToolBar(buttons));
                        buttons = new Button[Math.min(4, matrices.size() - j)];
                        i = 0;
                    }
                }
                if (i != 0) {
                    layers.getChildren().addAll(new ToolBar(buttons));
                }
                scenery.getChildren().addAll(layers, display);
                subtraction.setScene(new Scene(scenery));
                subtraction.show();
            }
        });

        multiply = new Button("Multiply");
        multiply.setOnAction(m -> {
            if (matrices.size() == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Matrices");
                alert.setHeaderText("No matrices to multiply.");
                alert.setContentText("Click \"Create Matrix\" button.");
                alert.showAndWait();
            } else {
                Stage multiplication = new Stage();
                multiplication.setTitle("Multiplication");
                HBox scenery = new HBox();
                VBox layers = new VBox();
                TextArea display = new TextArea();
                display.setFont(font);
                display.setEditable(false);
                Button[] buttons = new Button[Math.min(4, matrices.size())];
                int i = 0, j = 0;
                boolean[] first = {true};
                Matrix[] firstMatrix = {null};
                for (Map.Entry<String, Matrix> entry : matrices.entrySet()) {
                    Button button = new Button(entry.getKey());
                    button.setOnAction(c -> {
                        try {
                            if (first[0]) {
                                first[0] = false;
                                firstMatrix[0] = matrices.get(button.getText());
                                display.setText(firstMatrix[0].toString()
                                        + "\n\n");
                            } else {
                                first[0] = true;
                                Matrix secondMatrix
                                    = matrices.get(button.getText());
                                String toString = secondMatrix.toString();
                                display.appendText(toString + " * \n");
                                for (int g = 0; g < 2 * secondMatrix.col + 5;
                                        g++) {
                                    display.appendText("-");
                                }
                                display.appendText(
                                        "\n" + firstMatrix[0].multiply(
                                                secondMatrix).toString());
                            }
                        } catch (IllegalDimensionException iDE) {
                            display.setText(iDE.getMessage());
                        }
                    });
                    buttons[i] = button;
                    i++;
                    j++;
                    if (i > 3) {
                        layers.getChildren().addAll(new ToolBar(buttons));
                        buttons = new Button[Math.min(4, matrices.size() - j)];
                        i = 0;
                    }
                }
                if (i != 0) {
                    layers.getChildren().addAll(new ToolBar(buttons));
                }
                scenery.getChildren().addAll(layers, display);
                multiplication.setScene(new Scene(scenery));
                multiplication.show();
            }
        });

        gaussJordan = new Button("Gauss-Jordan");
        gaussJordan.setOnAction(g -> {
            if (matrices.size() == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Matrices");
                alert.setHeaderText("No matrices to solve.");
                alert.setContentText("Click \"Create Matrix\" button.");
                alert.showAndWait();
            } else {
                Stage gauss = new Stage();
                gauss.setTitle("Gauss-Jordan Elimination");
                HBox scenery = new HBox();
                VBox layers = new VBox();
                TextArea display = new TextArea();
                display.setFont(font);
                display.setEditable(false);
                Button[] buttons = new Button[Math.min(4, matrices.size())];
                int i = 0, j = 0;
                for (Map.Entry<String, Matrix> entry : matrices.entrySet()) {
                    Button button = new Button(entry.getKey());
                    button.setOnAction(c -> {
                        Matrix jordan = matrices.get(button.getText());
                        display.appendText(button.getText() + "\n" + jordan
                                + "\n\n" + jordan.gaussJordan());
                    });
                    buttons[i] = button;
                    i++;
                    j++;
                    if (i > 3) {
                        layers.getChildren().addAll(new ToolBar(buttons));
                        buttons = new Button[Math.min(4, matrices.size() - j)];
                        i = 0;
                    }
                }
                if (i != 0) {
                    layers.getChildren().addAll(new ToolBar(buttons));
                }
                scenery.getChildren().addAll(layers, display);
                gauss.setScene(new Scene(scenery));
                gauss.show();
            }
        });

        factor = new Button("LU Factorization");
        factor.setOnAction(f -> {
            if (matrices.size() == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Matrices");
                alert.setHeaderText("No matrices to factorize.");
                alert.setContentText("Click \"Create Matrix\" button.");
                alert.showAndWait();
            } else {
                Stage factorization = new Stage();
                factorization.setMinHeight(300);
                factorization.setTitle("LU Factorization");
                HBox scenery = new HBox();
                VBox layers = new VBox();
                TextArea display = new TextArea();
                display.setFont(font);
                display.setEditable(false);
                Button[] buttons = new Button[Math.min(4, matrices.size())];
                int i = 0, j = 0;
                for (Map.Entry<String, Matrix> entry : matrices.entrySet()) {
                    Button button = new Button(entry.getKey());
                    button.setOnAction(c -> {
                        Matrix factorize = matrices.get(button.getText());
                        Matrix[] lu = factorize.LUFactorization();
                        display.appendText("A:\n" + factorize + "\n\nP:\n"
                                + lu[0] + "\n\nL:\n" + lu[1] + "\n\nU:\n"
                                + lu[2]);
                    });
                    buttons[i] = button;
                    i++;
                    j++;
                    if (i > 3) {
                        layers.getChildren().addAll(new ToolBar(buttons));
                        buttons = new Button[Math.min(4, matrices.size() - j)];
                        i = 0;
                    }
                }
                if (i != 0) {
                    layers.getChildren().addAll(new ToolBar(buttons));
                }
                scenery.getChildren().addAll(layers, display);
                factorization.setScene(new Scene(scenery));
                factorization.show();
            }
        });

        determinant = new Button("Determinant");
        determinant.setOnAction(d -> {
            if (matrices.size() == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Matrices");
                alert.setHeaderText("No matrices to compute determinant.");
                alert.setContentText("Click \"Create Matrix\" button.");
                alert.showAndWait();
            } else {
                Stage determin = new Stage();
                determin.setTitle("Determinant");
                HBox scenery = new HBox();
                VBox layers = new VBox();
                TextArea display = new TextArea();
                display.setFont(font);
                display.setEditable(false);
                Button[] buttons = new Button[Math.min(4, matrices.size())];
                int i = 0, j = 0;
                for (Map.Entry<String, Matrix> entry : matrices.entrySet()) {
                    Button button = new Button(entry.getKey());
                    button.setOnAction(c -> {
                        try {
                            display.setText(button.getText() + "\n"
                                    + matrices.get(button.getText())
                                    + "\n\nDeterminant: " + matrices
                                    .get(button.getText()).determinant());
                        } catch (IllegalDimensionException iDE) {
                            display.setText(iDE.getMessage());
                        }

                    });
                    buttons[i] = button;
                    i++;
                    j++;
                    if (i > 3) {
                        layers.getChildren().addAll(new ToolBar(buttons));
                        buttons = new Button[Math.min(4, matrices.size() - j)];
                        i = 0;
                    }
                }
                if (i != 0) {
                    layers.getChildren().addAll(new ToolBar(buttons));
                }
                scenery.getChildren().addAll(layers, display);
                determin.setScene(new Scene(scenery));
                determin.show();
            }
        });

        inverse = new Button("Inverse");
        inverse.setOnAction(i -> {
            if (matrices.size() == 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("No Matrices");
                alert.setHeaderText("No matrices to compute inverse.");
                alert.setContentText("Click \"Create Matrix\" button.");
                alert.showAndWait();
            } else {
                Stage determin = new Stage();
                determin.setTitle("Inverse");
                HBox scenery = new HBox();
                VBox layers = new VBox();
                TextArea display = new TextArea();
                display.setFont(font);
                display.setEditable(false);
                Button[] buttons = new Button[Math.min(4, matrices.size())];
                int j = 0, k = 0;
                for (Map.Entry<String, Matrix> entry : matrices.entrySet()) {
                    Button button = new Button(entry.getKey());
                    button.setOnAction(c -> {
                        try {
                            display.setText(button.getText() + "\n"
                                    + matrices.get(button.getText())
                                    + "\n\nInverse:\n" + matrices
                                    .get(button.getText()).inverse());
                        } catch (IllegalDimensionException iDE) {
                            display.setText(iDE.getMessage());
                        }

                    });
                    buttons[j] = button;
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

        matHelp = new Button("?");
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

        /*
        Button not = new Button("\u21C1");
        not.setOnAction(e -> algebra.appendText("\u21C1"));

        Button and = new Button("\u2227");
        and.setOnAction(e -> algebra.appendText("\u2227"));

        Button or = new Button("\u2228");
        or.setOnAction(e -> algebra.appendText("\u2228"));

        Button bIf = new Button("\u2192");
        bIf.setOnAction(e -> algebra.appendText("\u2192"));

        Button iff = new Button("\u2194");
        iff.setOnAction(e -> algebra.appendText("\u2194"));
        */

        Button settings = new Button("Settings");
        settings.setOnAction(e -> {
            Stage set = new Stage();
            set.setTitle("Settings");
            set.setResizable(false);
            VBox setting = new VBox(5);
            HBox first = new HBox(5), second = new HBox(5), third = new HBox(5);

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

            Label labelMode = new Label("Choose which mathematical mode the "
                    + "processor should be in:");
            ObservableList<String> modes = FXCollections.observableArrayList();
            modes.addAll("Algebra", "Matrix", "Set"); // "Logic"
            ComboBox<String> selectMode = new ComboBox<>(modes);
            selectMode.setValue(mode);
            selectMode.setOnAction(sm -> {
                mode = selectMode.getValue();
                currentMode.setText(mode);
                generateLayout(stage, settings, help);
                Platform.runLater(() -> set.requestFocus());
                // To get the focus on the settings menu.
            });
            first.getChildren().addAll(firstLabel, approx);
            second.getChildren().addAll(secondLabel, digits);
            third.getChildren().addAll(labelMode, selectMode);
            setting.getChildren().addAll(first, second, descrip, third);
            set.setScene(new Scene(setting));
            set.show();
        });

        buttons = new ToolBar(imag, pi, bE);

        currentMode = new Label(mode);
        generateLayout(stage, settings, help);

        stage.setTitle("Algebra Processor");
        stage.show();
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
     * @param settings Lower-left settings button.
     * @param help Lower-right help button.
     */
    public void generateLayout(Stage stage, Button settings, Button help) {
        BorderPane main = new BorderPane();
        AnchorPane bottomAnchor = new AnchorPane();
        HBox sett = new HBox(5);
        sett.getChildren().addAll(settings, currentMode);
        if (mode.equals("Algebra") || mode.equals("Set")) {
            if (mode.equals("Algebra")) {
                buttons = new ToolBar(imag, pi, bE);
            } else {
                buttons = new ToolBar(nullSet);
            }
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
                        boolean appro = approx.getValue().equals("Exact");
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

            topmost.getChildren().addAll(topAnchor, buttons);
            main.setTop(topmost);
        } else {
            output.setFont(font);
            output.setText("Create a matrix first by using"
                    + " the \"Create Matrix\" Button.");
            buttons = new ToolBar(create, view, add, subtract, multiply,
                    gaussJordan, factor, determinant, inverse, matHelp);
            main.setTop(buttons);
        }
        bottomAnchor.getChildren().addAll(help, sett);
        AnchorPane.setLeftAnchor(settings, 4.0);
        AnchorPane.setRightAnchor(help, 4.0);
        main.setCenter(output);
        main.setBottom(bottomAnchor);

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
