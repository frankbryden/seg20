import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.awt.*;
import java.util.function.Consumer;


public class Printer {
    private Stage primaryStage;
    private String calculationsHeading;
    private String calculations;
    //This might be a bit confusing. It solves a slightly weird kinda frustrating problem
    //DM me if you have any questions about is - Frankie
    private Pair<Pane, Node> originalRecalculatedPane;
    private Canvas runway;
    private Font headerFont;
    /*
    What needs to be printed :
        - The airport and relevant runway
        - calculation breakdown -> done
        - original+recalculated values
        - the views (not sure about this)
     */

    public Printer(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.headerFont = Font.font("Verdana", FontWeight.BOLD, 14);
    }

    private Scene getContentsToPrint(){
        Group root = new Group();
        Scene scene = new Scene(root);

        Circle c = new Circle(50, 100, 100);
        c.setFill(Color.BLUE);

        root.getChildren().add(c);
        return scene;
    }

    private boolean printCalculations(PrinterJob job){
        Text heading = new Text(0, 10, this.calculationsHeading);
        heading.setFont(headerFont);
        Text text = new Text(0, 50, this.calculations);
        text.setFont(Font.font("Courier New", FontWeight.NORMAL, 12));
        Group root = new Group();
        root.getChildren().add(heading);
        root.getChildren().add(text);
        return job.printPage(root);
    }

    private boolean printRunway(PrinterJob job){
        //Transform rotate = Transform.rotate(90, 20, 20);
        //this.runway.getTransforms().add(rotate);//Transform.rotate(90, runway.getWidth()/2, runway.getHeight()/2));
        PageLayout pageLayout = job.getPrinter().createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, javafx.print.Printer.MarginType.DEFAULT);
        return job.printPage(pageLayout, this.runway);
    }

    private boolean printContents(PrinterJob job){
        return printCalculations(job) && printRunway(job) && printOriginalRecalculatedValues(job);
    }

    void printPos(GridPane gp){
        System.out.println("(" + gp.getLayoutX() + "; " + gp.getLayoutY() + ")");
    }

    private boolean printOriginalRecalculatedValues(PrinterJob job){
        Pane parent = originalRecalculatedPane.getKey();
        Node recalculatedVals = originalRecalculatedPane.getValue();
        Group root = new Group();
        Text header = new Text(0, 10, "Original and recalculated values");
        header.setFont(headerFont);
        root.getChildren().add(header);

        //store original position to restore the node after the print
        double originalX = originalRecalculatedPane.getValue().getLayoutX();
        double originalY = originalRecalculatedPane.getValue().getLayoutY();
        System.out.println("original position is " + originalX + "; " + originalY);

        recalculatedVals.setLayoutX(0);
        recalculatedVals.setLayoutY(50);
        GridPane gp = (GridPane) recalculatedVals;
        gp.setPrefWidth(400);
        root.getChildren().add(originalRecalculatedPane.getValue());

        boolean result = job.printPage(root);

        //when we add that pane to the root, it gets removed from its previous parent. It therefore needs to be added again.
        parent.getChildren().add(originalRecalculatedPane.getValue());

        //Restore to original position
        originalRecalculatedPane.getValue().setLayoutX(originalX);//originalPosition.x);
        originalRecalculatedPane.getValue().setLayoutY(originalY);//originalPosition.y);

        return result;
    }

    public void print(){
        PrinterJob job = PrinterJob.createPrinterJob();
        boolean performPrint = job.showPrintDialog(primaryStage);
        System.out.println("print ? " + performPrint);
        if (performPrint){
            boolean success = printContents(job);
            System.out.println("Print was done using printer " + job.getPrinter().getName());
            if (success){
                System.out.println("Print job was successful!");
                job.endJob();
            } else {
                System.err.println("Print job failed");
            }
        }
    }

    public void setCalculations(String calculations) {
        this.calculations = calculations;
    }

    public void setRunway(Canvas runway) {
        this.runway = runway;
    }

    public void setCalculationsHeading(String calculationsHeading) {
        this.calculationsHeading = calculationsHeading;
    }

    public void setOriginalRecalculatedPane(Pair<Pane, Node> originalRecalculatedPane) {
        this.originalRecalculatedPane = originalRecalculatedPane;
    }
}
