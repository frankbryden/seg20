import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileIO {

    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder = null;

    public FileIO(){
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public AirportConfig read(String filePath){
        return null;
    }

    public void write(AirportConfig airportConfig, String filePath){
        Document document = documentBuilder.newDocument();
        // root element
        Element root = document.createElement("AirportConfiguration");
        document.appendChild(root);

        for (RunwayDesignator runwayDesignator : airportConfig.getRunwayConfigs().keySet()){
            RunwayConfig runwayConfig = airportConfig.getRunwayConfigs().get(runwayDesignator);

            Element runway = document.createElement("runway");

            root.appendChild(runway);

            //Add designator
            Element designator = document.createElement("designator");
            designator.appendChild(document.createTextNode(runwayConfig.getRunwayDesignator().toString()));

            // Add TORA, TODA, ASDA, LDA

            //Create relevant nodes
            Element tora = document.createElement("TORA");
            Element toda = document.createElement("TODA");
            Element asda = document.createElement("ASDA");
            Element lda = document.createElement("LDA");

            //Add the values to the nodes
            tora.appendChild(document.createTextNode(String.valueOf(runwayConfig.getTORA())));
            toda.appendChild(document.createTextNode(String.valueOf(runwayConfig.getTODA())));
            asda.appendChild(document.createTextNode(String.valueOf(runwayConfig.getASDA())));
            lda.appendChild(document.createTextNode(String.valueOf(runwayConfig.getLDA())));

            runway.appendChild(designator);
            runway.appendChild(tora);
            runway.appendChild(toda);
            runway.appendChild(asda);
            runway.appendChild(lda);
        }
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // write document to file using a transformer
            tr.transform(new DOMSource(root), new StreamResult(new FileOutputStream(filePath)));

        } catch (TransformerException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    public void write(String data){

    }
}
