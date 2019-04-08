import javafx.geometry.Orientation;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class Printer {
    private Stage primaryStage;
    private String calculations;
    private Canvas runway;
    /*
    What needs to be printed :
        - The airport and relevant runway
        - calculation breakdown
        - original+recalculated values
        - the views (not sure about this)
     */

    public Printer(Stage primaryStage){
        this.primaryStage = primaryStage;
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
        Text text = new Text(0, 10, this.calculations);
        text.setFont(Font.font(12));
        return job.printPage(text);
    }

    private boolean printRunway(PrinterJob job){
        Transform rotate = Transform.rotate(90, 20, 20);
        this.runway.getTransforms().add(rotate);//Transform.rotate(90, runway.getWidth()/2, runway.getHeight()/2));
        PageLayout pageLayout = job.getPrinter().createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, javafx.print.Printer.MarginType.DEFAULT);
        return job.printPage(pageLayout, this.runway);
    }

    private boolean printContents(PrinterJob job){
        return printCalculations(job) && printRunway(job);
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
}
