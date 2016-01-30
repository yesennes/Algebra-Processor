package userIO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import lang.AlgebraicFunction;
import lang.Constant;
import lang.Expression;
import lang.MathFormatException;
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
    private static final String imagUnit = Term.IMAG_UNIT;
    private TextArea output;
    private TextField algebra;
    private ComboBox<String> approx;
    private int ofDigits;

    /**
     * Method which creates a stage where the user may enter his or her algebraic expressions.
     * @param stage The default stage.
     */
    @Override
    public void start(Stage stage) {
        AnchorPane topAnchor = new AnchorPane();
        AnchorPane bottomAnchor = new AnchorPane();
        BorderPane main = new BorderPane();
        VBox topmost = new VBox();
        HBox top = new HBox(5);
        HBox other = new HBox(5);

        output = new TextArea("Enter an equation in the box above, or click on the \"?\" in the bottom right for more help.");
        output.setWrapText(true);
        output.setEditable(false);

        Button enter = new Button("Enter");
        enter.setOnAction(e -> enter());

        algebra = new TextField();
        algebra.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                enter();
            }
        });

        ObservableList<String> choices = FXCollections.observableArrayList();
        choices.addAll("Exact", "Approximation");
        approx = new ComboBox<>(choices);
        approx.setValue("Exact");
        approx.setPrefWidth(393);

        Label descrip = new Label("All non-rational numbers will be kept in as mathmatical constants and exponents.");

        Spinner<Integer> digits = new Spinner<>(0, 100, ofDigits);
        digits.setPrefWidth(375);
        digits.setDisable(approx.getValue().equals("Exact"));

        Button settings = new Button("Settings");
        settings.setOnAction(event -> {
            Stage set = new Stage();
            set.setTitle("Settings");
            set.setResizable(false);
            VBox setting = new VBox(5);
            HBox first = new HBox(5);
            HBox second = new HBox(5);

            Label firstLabel = new Label("Mode:");
            Label secondLabel = new Label("Precision:");

            approx.setOnAction(clicked -> {
                if (approx.getValue().equals("Exact")) {
                    descrip.setText("All non-rational numbers will be kept in as mathmatical constants and exponents.");
                    digits.setDisable(true);
                } else {
                    descrip.setText("All non-rational numbers will be approximated to the nearest whole number.");
                    digits.setDisable(false);
                }
            });

            digits.setOnMouseClicked(action -> {
                ofDigits = digits.getValue();
                descrip.setText("All non-rational numbers will be approximated to "
                        + ((ofDigits == 0) ? "the nearest whole number." : ofDigits
                                + ((ofDigits == 1) ? " place " : " places ") + "after the decimal."));
            });

            first.getChildren().addAll(firstLabel, approx);
            second.getChildren().addAll(secondLabel, digits);
            setting.getChildren().addAll(first, second, descrip);
            set.setScene(new Scene(setting));
            set.show();
        });

        Button help = new Button("?");
        help.setOnAction(event -> {
            Stage helper = new Stage();
            helper.setTitle("Help Menu");
            TextArea helpArea = new TextArea("\tWelcome to AlgebraProcessor Beta! Simply enter an expression or equation in "
                    + "the top box, press enter or hit the enter key, and it will write it standard form, simplify it, factor "
                    + "it, and, if it is an equation, solve it. Use the \"^\" to enter exponents. To take the nth root of a "
                    + "number, raise it to 1/n. For instance, to take the square root of x, enter \"x^(1/2)\". To enter "
                    + imagUnit + ", the imaginary unit, press the buttion with " + imagUnit + " on it. If " + imagUnit
                    + " is showing up as a " + new String(Character.toChars(0x1F700)) + " to you, then your computer doesn't "
                    + "have the font Cambria Math on it. Simply pretend squares are the imaginary unit. The same holds true "
                    + "for the mathmatical constant e. Later versions will remove dependency on Cambria Math. It ignores "
                    + "whitespace, but will treat other, non-letter symbols like \"!\" as variables. \n\n\tCurrently it its "
                    + "capable of simplifying about anything, factoring out gcd and factoring quadratics. It can solve "
                    + "quadratics and 2 step equations.\n\n\tGarbage in, Garbage out; currently, if you enter anything that "
                    + "doesn't make mathmatical sense, it may give an error or it may try to interpret you tried to enter. "
                    + "Later version may fix this. This product is still in beta, so it may give garbage out anyway. If you "
                    + "enter a proper expression or equation and it gives an error or wrong answer, please email what you "
                    + "entered to yesennes@gmail.com.\n\n\tUpdates will come periodically, to receive them send an email to "
                    + "yesennes@gmail.com requesting to be put on the email list. Upcoming features include support for "
                    + "functions such as sine, better display with fractions actually stacked, better input with superscript "
                    + "and \u221as, factoring and solving of more complex expression, constants like \u03c0 and "
                    + new String(Character.toChars(0x1d4ee)) + " and a button for approximate answers. If you would like to "
                    + "see any other features, send an email to yesennes@gmail.com.\n\n\tP.S. I am still trying to think of "
                    + "a good name for this program. Any suggustions would be welcome.");
            helpArea.setWrapText(true);
            helpArea.setEditable(false);
            helper.setScene(new Scene(helpArea));
            helper.show();
        });

        Button i = new Button(imagUnit);
        i.setOnAction(event -> algebra.appendText(imagUnit));

        Button pi = new Button(String.valueOf(Term.PI));
        pi.setOnAction(event -> algebra.appendText(String.valueOf(Term.PI)));

        Button e = new Button(Term.E);
        e.setOnAction(event -> algebra.appendText(Term.E));

        Button not = new Button("\u21C1");
        not.setOnAction(event -> algebra.appendText("\u21C1"));

        Button and = new Button("\u2227");
        and.setOnAction(event -> algebra.appendText("\u2227"));

        Button or = new Button("\u2227");
        or.setOnAction(event -> algebra.appendText("\u2227"));

        Button bIf = new Button("\u2228");
        bIf.setOnAction(event -> algebra.appendText("\u2228"));

        Button iff = new Button("\u2194");
        iff.setOnAction(event -> algebra.appendText("\u2194"));

        ToolBar buttons = new ToolBar(i, pi, e, not, and, or, bIf, iff);

        top.getChildren().addAll(algebra, enter);
        topAnchor.getChildren().addAll(algebra, enter);
        AnchorPane.setRightAnchor(enter, 4.0);
        AnchorPane.setLeftAnchor(algebra, 4.0);
        AnchorPane.setRightAnchor(algebra, 50.0);

        topmost.getChildren().addAll(topAnchor, buttons);
        other.getChildren().addAll(settings, help);

        bottomAnchor.getChildren().addAll(help, settings);
        AnchorPane.setLeftAnchor(settings, 4.0);
        AnchorPane.setRightAnchor(help, 4.0);


        main.setCenter(output);
        main.setTop(topmost);
        main.setBottom(bottomAnchor);

        Platform.runLater(() -> algebra.requestFocus()); // To get focus off the TextArea and on the TextField.

        stage.setTitle("Algebra Processor");
        stage.setScene(new Scene(main));
        stage.show();
    }

    class Axes extends Pane {
        private NumberAxis xAxis;
        private NumberAxis yAxis;

        public Axes(int width, int height, double xLow, double xHi, double xTickUnit, double yLow, double yHi,
                double yTickUnit) {
            setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
            setPrefSize(width, height);
            setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

            xAxis = new NumberAxis(xLow, xHi, xTickUnit);
            xAxis.setSide(Side.BOTTOM);
            xAxis.setMinorTickVisible(false);
            xAxis.setPrefWidth(width);
            xAxis.setLayoutY(height / 2);

            yAxis = new NumberAxis(yLow, yHi, yTickUnit);
            yAxis.setSide(Side.LEFT);
            yAxis.setMinorTickVisible(false);
            yAxis.setPrefHeight(height);
            yAxis.layoutXProperty().bind(Bindings.subtract((width / 2) + 1, yAxis.widthProperty()));

            getChildren().setAll(xAxis, yAxis);
        }

        public NumberAxis getXAxis() {
            return xAxis;
        }

        public NumberAxis getYAxis() {
            return yAxis;
        }
    }

    class Plot extends Pane {
        public Plot(AlgebraicFunction f, double xMin, double xMax, double xInc, Axes axes) {
            Path path = new Path();
            path.setStroke(Color.ORANGE.deriveColor(0, 1, 1, 0.6));
            path.setStrokeWidth(2);

            path.setClip(new Rectangle(0, 0, axes.getPrefWidth(), axes.getPrefHeight()));

            Constant x = new Constant(xMin);
            Constant y = f.evaluate(x);

            path.getElements().add(new MoveTo(mapX(x.doubleValue(), axes), mapY(y.doubleValue(), axes)));

            x = x.add(new Constant(xInc));
            while (x.compareTo(xMax) < 0) {
                y = f.evaluate(x);
                path.getElements().add(new LineTo(mapX(x.doubleValue(), axes), mapY(y.doubleValue(), axes)));
                x = x.add(new Constant(xInc));
            }

            setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
            setPrefSize(axes.getPrefWidth(), axes.getPrefHeight());
            setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

            getChildren().setAll(axes, path);
        }

        private double mapX(double x, Axes axes) {
            double tx = axes.getPrefWidth() / 2;
            double sx = axes.getPrefWidth() / (axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound());
            return x * sx + tx;
        }

        private double mapY(double y, Axes axes) {
            double ty = axes.getPrefHeight() / 2;
            double sy = axes.getPrefHeight() / (axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound());
            return -y * sy + ty;
        }
    }

    /**
     * Method called whenever the Enter button has been pressed or the Enter key has been pressed while focused on the
     * TextField where the algebraic expression is entered.
     */
    public void enter() {
        try {
            // Creates an Expression and factors it, then if it is a equation, solves it.
            boolean appro = approx.getValue().equals("Exact");
            Expression exp = new Expression(algebra.getText());
            output.setText("Standard Form:" + (appro ? exp : exp.approx().toStringDecimal(ofDigits)) + "\n");
            output.appendText("Factored:");
            ArrayList<Expression> facts = exp.factor();
            for (Expression fact : facts) {
                output.appendText("(" + (appro ? fact : fact.approx().toStringDecimal(ofDigits)) + ")");
            }
            if (exp.isEquation) {
                output.appendText("=0");
            }
            output.appendText("\n");
            if (exp.isEquation) {
                output.appendText("Solutions:");
                HashSet<Solution> sols = exp.solve();
                if (sols.size() == 0) {
                    output.appendText(" Was not able to solve");
                } else {
                    for (Solution current : sols) {
                        output.appendText((appro ? current : current.approx(ofDigits)) + "\n                    ");
                    }
                }
            }
        } catch (MathFormatException format) {
            output.setText("There was an error in your formatting. " + format.getMessage()
                + " If you think what you entered was formatted correctly, please email what you entered to "
                + "yesennes@gmail.com");
        } catch (OverflowException over) {
            output.setText("A number was to big. " + over.getMessage());
        } catch (Exception a) {
            output.setText("An error occured. Please email what you entered to yesennes@gmail.com");
            a.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
