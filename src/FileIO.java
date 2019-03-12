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
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileIO {

    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder = null;
    private Document document;
    private Element root;

    public FileIO(){
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            root = document.createElement("Application");

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
        String airportName = document.getElementsByTagName("name").item(0).getTextContent();
        AirportConfig airportConfig = new AirportConfig(airportName);
        //Retrieve list of runway pairs
        NodeList runwayPairs = document.getElementsByTagName("RunwayPair");
        for (int i = 0; i < runwayPairs.getLength(); i++){
            //For each runway pair, extract 2 runway configs
            NodeList runwayConfigs = runwayPairs.item(i).getChildNodes();
            Node[] runwayConfigNodes = new Node[2];
            int k = 0;
            for (int j = 0; j <runwayConfigs.getLength(); j++){
                if (runwayConfigs.item(j).getNodeName().equals("RunwayConfig")){
                    runwayConfigNodes[k] = runwayConfigs.item(j);
                    k += 1;
                }
            }
            RunwayConfig r1 = parseRunwayConfig(runwayConfigNodes[0]);
            RunwayConfig r2 = parseRunwayConfig(runwayConfigNodes[1]);
            System.out.println(r1.getRunwayDesignator());
            System.out.println(r2.getRunwayDesignator());
            airportConfig.addRunwayPair(new RunwayPair(r1, r2));
        }
        return airportConfig;
    }

    public RunwayConfig parseRunwayConfig(Node runwayConfigNode){
        int tora, toda, asda, lda, displacementThreshold;
        tora = toda = asda = lda = displacementThreshold = -1;
        RunwayDesignator runwayDesignator = null;
        NodeList runwayData = runwayConfigNode.getChildNodes();
        for (int j = 0; j < runwayData.getLength(); j++) {
            Node node = runwayData.item(j);
            switch (node.getNodeName()) {
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
                case "displacement":
                    displacementThreshold = Integer.parseInt(node.getTextContent());
                default:
                    break;
            }
        }
        return new RunwayConfig(runwayDesignator, tora, toda, asda, lda, displacementThreshold);
    }


    public void write(AirportConfig airportConfig, String filePath){
        // airportRoot element
        Element airportRoot = document.createElement("AirportConfiguration");
        root.appendChild(airportRoot);

        Element airportName = document.createElement("name");
        airportName.appendChild(document.createTextNode(airportConfig.getName()));
        airportRoot.appendChild(airportName);

        for (String runwayPairName : airportConfig.getRunways().keySet()){
            RunwayPair runwayPair = airportConfig.getRunways().get(runwayPairName);
            RunwayConfig r1conf = runwayPair.getR1();
            RunwayConfig r2conf = runwayPair.getR2();

            Element physicalRunway = document.createElement("RunwayPair");

            airportRoot.appendChild(physicalRunway);

            physicalRunway.appendChild(createRunwayConfigData(r1conf));
            physicalRunway.appendChild(createRunwayConfigData(r2conf));
        }

        write(airportRoot, filePath);

    }

    public void write(Obstacle obstacle, String filePath){
        // obstacleRoot element
        Element obstacleRoot = document.createElement("Obstacle");
        root.appendChild(obstacleRoot);

        Element obstacleName = document.createElement("name");
        obstacleName.appendChild(document.createTextNode(obstacle.getName()));
        obstacleRoot.appendChild(obstacleName);

        Element obstacleHeight = document.createElement("height");
        obstacleHeight.appendChild(document.createTextNode(String.valueOf(obstacle.getHeight())));
        obstacleRoot.appendChild(obstacleHeight);

        write(root, filePath);
    }

    public void write(Element root, String filePath){
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // write document to file using a transformer
            tr.transform(new DOMSource(root), new StreamResult(new FileOutputStream(filePath)));
            System.out.println("Wrote to " + filePath + "...");

        } catch (TransformerException | FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public Map<String, AirportConfig> readRunwayDB(String filePath){
        File file = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        Map<String, AirportConfig> airportConfigs = new HashMap<>();
        String line;
        RunwayPair runwayPair = new RunwayPair();
        try {
            while ((line = bufferedReader.readLine()) != null){
                String[] parts = line.split(",");
                //TORA TODA ASDA LDA
                String name = parts[0];
                String designator = parts[1];
                int tora = Integer.valueOf(parts[2]);
                int toda = Integer.valueOf(parts[3]);
                int asda = Integer.valueOf(parts[4]);
                int lda = Integer.valueOf(parts[5]);
                RunwayConfig runwayConfig = new RunwayConfig(new RunwayDesignator(designator), tora, toda, asda, lda, 0);

                if (runwayPair.getR1() == null){
                    runwayPair.setR1(runwayConfig);
                } else if (runwayPair.getR2() == null){
                    runwayPair.setR2(runwayConfig);
                    //Needed to configure name, now that both runways have been added
                    runwayPair.init();
                    if (airportConfigs.containsKey(name)){
                        airportConfigs.get(name).addRunwayPair(runwayPair);
                    } else {
                        AirportConfig airportConfig = new AirportConfig(name);
                        airportConfig.addRunwayPair(runwayPair);
                        airportConfigs.put(name, airportConfig);
                    }

                    runwayPair = new RunwayPair();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return airportConfigs;
    }

    public void write(String data){

    }

    public Element createRunwayConfigData(RunwayConfig runwayConfig){
        Element parent = document.createElement("RunwayConfig");
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

        parent.appendChild(designator);
        parent.appendChild(tora);
        parent.appendChild(toda);
        parent.appendChild(asda);
        parent.appendChild(lda);

        return parent;
    }
}
