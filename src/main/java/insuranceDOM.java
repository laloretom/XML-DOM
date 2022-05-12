import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class insuranceDOM {
    static final String CLASS_NAME = insuranceDOM.class.getSimpleName();
    static final Logger LOG = Logger.getLogger(CLASS_NAME);


    public static void main(String[] args) {

        if (args.length == 0) {
            LOG.severe("No model to process. Usage is:" + "\njava DeptSalesReport <keyword>");
            return;
        }
        String marca = args[0].toLowerCase();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File("insurance.xml"));
            doc.getDocumentElement().normalize();
            costoPromedioSeguroXModelo(doc);
            reporteVentasXMes(doc);
            reporteVentasXMesXMarca(doc, marca);
        } catch (ParserConfigurationException e) {
            LOG.severe(e.getMessage());
        } catch (IOException e) {
            LOG.severe(e.getMessage());
        } catch (SAXException e) {
            LOG.severe(e.getMessage());
        }
    }

    public static void reporteVentasXMes(Document doc) {
        Element root = doc.getDocumentElement();
        NodeList salesData = root.getElementsByTagName("insurance_record");
        int n = salesData.getLength();
        HashMap<String, Double> ventas = new HashMap<>();

        String insurance;
        LocalDate date;
        String mes;


        for (int index = 0; index < n; index++) {
            Node node = salesData.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                insurance = element.getElementsByTagName("insurance").item(0).getTextContent();
                date = LocalDate.parse(element.getElementsByTagName("contract_date").item(0).getTextContent());
                mes = date.getMonth().toString();

                double val = Double.parseDouble(insurance);

                if (ventas.containsKey(mes)) {
                    double x = ventas.get(mes);
                    ventas.put(mes, val + x);
                } else {
                    ventas.put(mes, val);
                }
            }
        }

        System.out.println("Total de ventas por mes");
        for (HashMap.Entry<String, Double> entry : ventas.entrySet()) {
            System.out.printf("%-15.15s $%,9.2f\n", entry.getKey(), entry.getValue());
        }
        System.out.println("-------------------------------------------------------------");

    }

    public static void costoPromedioSeguroXModelo(Document doc) {
        Element root = doc.getDocumentElement();
        NodeList salesData = root.getElementsByTagName("insurance_record");
        int n = salesData.getLength();
        HashMap<String, Double> insuranceList = new HashMap<>();
        HashMap<String, Integer> contadorModel = new HashMap<>();

        String insurance;
        String model;

        for (int index = 0; index < n; index++) {
            Node node = salesData.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                insurance = element.getElementsByTagName("insurance").item(0).getTextContent();
                model = element.getElementsByTagName("model").item(0).getTextContent();

                double val = Double.parseDouble(insurance);

                if (insuranceList.containsKey(model)) {
                    double x = insuranceList.get(model);
                    insuranceList.put(model, val + x);
                    int a = contadorModel.get(model);
                    contadorModel.put(model, a + 1);
                } else {
                    insuranceList.put(model, val);
                    contadorModel.put(model, 1);
                }
            }

        }
        System.out.println("Costo promedio del seguro para cada modelo (año) de los autos");
        for (HashMap.Entry<String, Double> entry : insuranceList.entrySet()) {
            double promedio = entry.getValue() / contadorModel.get(entry.getKey());
            System.out.printf("%-15.15s $%,9.2f\n", entry.getKey(), promedio);
        }
        System.out.println("-------------------------------------------------------------");
    }

    public static void reporteVentasXMesXMarca(Document doc, String marca) {
        Element root = doc.getDocumentElement();
        NodeList salesData = root.getElementsByTagName("insurance_record");
        int n = salesData.getLength();
        HashMap<String, Double> ventas = new HashMap<>();

        String insurance;
        LocalDate date;
        String mes;

        for (int index = 0; index < n; index++) {
            Node node = salesData.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                if (element.getElementsByTagName("car").item(0).getTextContent().toLowerCase(Locale.ROOT).equals(marca)) {
                    insurance = element.getElementsByTagName("insurance").item(0).getTextContent();

                    date = LocalDate.parse(element.getElementsByTagName("contract_date").item(0).getTextContent());
                    mes = date.getMonth().toString();

                    double val = Double.parseDouble(insurance);

                    if (ventas.containsKey(mes)) {
                        double x = ventas.get(mes);
                        ventas.put(mes, val + x);
                    } else {
                        ventas.put(mes, val);
                    }
                }

            }

        }
        System.out.println("Ventas mensuales para la marca [" + marca + "]");
        for (HashMap.Entry<String, Double> entry : ventas.entrySet()) {
            System.out.printf("%-15.15s $%,9.2f\n", entry.getKey(), entry.getValue());
        }
        System.out.println("-------------------------------------------------------------");

    }

}
