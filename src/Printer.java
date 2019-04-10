import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
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

import javafx.scene.image.ImageView;
import java.util.Calendar;

public class Printer {
    private Stage primaryStage;
    private String calculationsHeading;
    private String calculations;
    //This might be a bit confusing. It solves a slightly weird kinda frustrating problem
    //DM me if you have any questions about is - Frankie
    private Pair<Pane, Node> originalRecalculatedPane;
    private Canvas runway;
    private Font headerFont, titleFont, subtitleFont;
    /*
    What needs to be printed :
        - The airport and relevant runway
        - calculation breakdown -> done
        - original+recalculated values
        - the views (not sure about this)
     */

    public Printer(Stage primaryStage){
        this.primaryStage = primaryStage;
        this.titleFont    = Font.font("Verdana", FontWeight.BOLD, 21);
        this.subtitleFont = Font.font("Verdana", FontWeight.BOLD, 16);
        this.headerFont   = Font.font("Verdana", FontWeight.BOLD, 14);
    }

    private Scene getContentsToPrint(){
        Group root = new Group();
        Scene scene = new Scene(root);

        Circle c = new Circle(50, 100, 100);
        c.setFill(Color.BLUE);

        root.getChildren().add(c);
        return scene;
    }

    private boolean printContents(PrinterJob job){
        return printCoverPage(job) && printCalculations(job) && printRunway(job) && printOriginalRecalculatedValues(job);
    }

    private boolean printCoverPage(PrinterJob job){
        PageLayout pl = job.getPrinter().getDefaultPageLayout();
        double pageWidth = pl.getPrintableWidth();
        double pageHeight = pl.getPrintableHeight();
        Group root = new Group();

        //Title
        Text title = new Text("Runway Redeclaration Tool Report");
        title.setX(centerNode(title, pl) - 30);
        title.setY(pageHeight/4);
        title.setFont(titleFont);
        System.out.println("Computed width of text is " + title.getLayoutBounds().getWidth() + ", and width of page is " + pageWidth);

        //Subtitle - date
        Calendar currentDate = Calendar.getInstance();
        int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = currentDate.get(Calendar.MINUTE);
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);
        int currentMonth = currentDate.get(Calendar.MONTH);
        int currentYear = currentDate.get(Calendar.YEAR);
        String currentDateString = padOutDate(currentDay) + "/" + padOutDate(currentMonth) + "/" + currentYear;
        String currentTimeString = currentHour + ":" + padOutDate(currentMinutes);
        System.out.println("Current date is " + currentDateString);

        //Logo
        ImageView planeView = new ImageView();
        Image planeImg = new Image("rec/plane.png");
        planeView.setImage(planeImg);
        planeView.setScaleX(0.25);
        planeView.setScaleY(0.25);
        planeView.setX(-260);//centerNode(planeView, pl));
        planeView.setY(-150);// + pageHeight/2);//pageHeight/2);
        System.out.println("width : " + planeView.getBoundsInLocal().getWidth());
        System.out.println("height : " + planeView.getBoundsInLocal().getHeight());
        System.out.println("X : " + planeView.getX());
        System.out.println("Y : " + planeView.getY());

        //Subtitle - date and time
        Text dateText = new Text(currentTimeString + " - " + currentDateString);
        dateText.setX(centerNode(dateText, pl) + 30);
        dateText.setY(pageHeight/4 + dateText.getLayoutBounds().getHeight()*2);
        dateText.setFont(subtitleFont);

        /*Text timeText = new Text(currentTimeString);
        timeText.setX(centerNode(timeText, pl) + 20);
        timeText.setY(dateText.getY() + 20);
        timeText.setFont(subtitleFont); */

        System.out.println("Title");
        printPos(title);
        System.out.println("Subtitle");
        printPos(dateText);

        Pane titlePane = new Pane(title);
        Pane subtitlePane = new Pane(dateText);
        titlePane.setStyle("-fx-background-color: red");
        subtitlePane.setStyle("-fx-background-color: blue");
        //root.getChildren().addAll(titlePane, subtitlePane);
        root.getChildren().addAll(title, dateText, planeView);
        System.out.println("Title");
        printPos(title);
        System.out.println("Subtitle");
        printPos(dateText);
        return job.printPage(root);
    }

    private void printPos(Node node){
        System.out.println("(" + node.getLayoutX() + "; " + node.getLayoutY() + ")");
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

    private String padOutDate(int dateItem){
        return String.format("%02d", dateItem);
    }

    private double centerNode(Node node, PageLayout pl){
        return (pl.getPrintableWidth() - node.getLayoutBounds().getWidth())/2 - pl.getLeftMargin();
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
