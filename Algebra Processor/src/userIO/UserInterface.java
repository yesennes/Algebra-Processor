package userIO;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import lang.Term;

public class UserInterface extends Application {
    private static final String imagUnit = Term.IMAG_UNIT;

    @Override
    public void start(Stage stage) {
        VBox main = new VBox(5);
        HBox top = new HBox(5);
        TextField algebra = new TextField();
        algebra.setPrefWidth(384);

        Button enter = new Button("Enter");
        enter.setOnAction(e -> {
            enter.setText("enter");
            //Expression expression = new Expression(algebra.getText());
        });

        Button i = new Button();

        top.getChildren().addAll(algebra, enter);
        main.getChildren().addAll(top);
        stage.setScene(new Scene(main));
        stage.setTitle("Algebra Processor");
        stage.show();
    }
}
