import org.w3c.dom.*;
import org.xml.sax.SAXException;

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
import java.io.IOException;

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
        Document document;
        try {
            document = documentBuilder.parse(filePath);
        } catch (SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
        NodeList runways = document.getElementsByTagName("runway");
        AirportConfig airportConfig = new AirportConfig("Heathrow");
        for (int i = 0; i < runways.getLength(); i++){
            int tora, toda, asda, lda;
            tora = toda = asda = lda = -1;
            RunwayDesignator runwayDesignator = null;
            NodeList runwayData = runways.item(i).getChildNodes();
            for (int j = 0; j < runwayData.getLength(); j++){
                Node node = runwayData.item(j);
                switch (node.getNodeName()){
                    case "TORA":
                        tora = Integer.parseInt(node.getTextContent());
                        break;
                    case "TODA":
                        toda = Integer.parseInt(node.getTextContent());
                        break;
                    case "ASDA":
                        asda = Integer.parseInt(node.getTextContent());
                        break;
                    case "LDA":
                        lda = Integer.parseInt(node.getTextContent());
                        break;
                    case "designator":
                        runwayDesignator = new RunwayDesignator(node.getTextContent());
                        break;
                    default:
                        break;
                }
                System.out.println(node.getNodeName() + ": " + node.getTextContent());
            }
            RunwayConfig runwayConfig = new RunwayConfig(runwayDesignator, tora, toda, asda, lda);
            airportConfig.addRunway(runwayConfig);
        }

        return airportConfig;
    }

    public void write(AirportConfig airportConfig, String filePath){
        Document document = documentBuilder.newDocument();
        // root element
        Element root = document.createElement("AirportConfiguration");
        document.appendChild(root);

        Element airportName = document.createElement("name");
        airportName.appendChild(document.createTextNode(airportConfig.getName()));
        root.appendChild(airportName);

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
