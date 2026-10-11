package task_xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Operations {

    public static void readXMLFile(String inputPath, String outputPath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            factory.setIgnoringElementContentWhitespace(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new StrictErrorHandler());

            Document doc = builder.parse(new File(inputPath));
            doc.getDocumentElement().normalize();

            Element student = doc.getDocumentElement();
            if (!"student".equals(student.getTagName())) {
                throw new IllegalArgumentException("Ожидался корневой элемент <student>, найден: <" + student.getTagName() + ">");
            }

            processStudent(doc, student);
            saveDocument(doc, outputPath);

            System.out.println("Результат сохранён: " + outputPath);
        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processStudent(Document doc, Element student) {
        String lastName = student.getAttribute("lastname");
        NodeList subjects = student.getElementsByTagName("subject");

        BigDecimal sum = BigDecimal.ZERO;
        int count = 0;

        for (int i = 0; i < subjects.getLength(); i++) {
            Element subject = (Element) subjects.item(i);
            String markStr = subject.getAttribute("mark");
            try {
                sum = sum.add(new BigDecimal(markStr));
                count++;
            } catch (NumberFormatException e) {
                System.err.printf("Некорректная оценка у студента %s: %s%n", lastName, markStr);
            }
        }

        if (count == 0) {
            System.out.printf("Студент %s: нет оценок, пропущен.%n", lastName);
            return;
        }

        BigDecimal realAverage = sum.divide(new BigDecimal(count), 1, RoundingMode.HALF_UP);
        NodeList averages = student.getElementsByTagName("average");

        if (averages.getLength() > 0) {
            Element average = (Element) averages.item(0);
            String currentText = average.getTextContent().trim();

            try {
                BigDecimal current = new BigDecimal(currentText);
                if (current.compareTo(realAverage) != 0) {
                    average.setTextContent(realAverage.toString());
                    System.out.printf("Студент %s: среднее исправлено с %s на %s%n", lastName, currentText, realAverage);
                } else {
                    System.out.printf("Студент %s: среднее %s корректно.%n", lastName, realAverage);
                }
            } catch (NumberFormatException e) {
                average.setTextContent(realAverage.toString());
                System.out.printf("Студент %s: некорректное значение среднего заменено на %s%n", lastName, realAverage);
            }
        } else {
            Element average = doc.createElement("average");
            average.setTextContent(realAverage.toString());
            student.appendChild(average);
            System.out.printf("Студент %s: добавлено <average> = %s%n", lastName, realAverage);
        }
    }

    private static void saveDocument(Document doc, String outputPath) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "student.dtd");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(doc), new StreamResult(new File(outputPath)));
    }

    private static class StrictErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException exception) {
            System.err.println("Предупреждение XML: " + exception.getMessage());
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw new SAXException("Ошибка валидации XML: " + exception.getMessage(), exception);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw new SAXException("Критическая ошибка парсинга XML: " + exception.getMessage(), exception);
        }
    }
}