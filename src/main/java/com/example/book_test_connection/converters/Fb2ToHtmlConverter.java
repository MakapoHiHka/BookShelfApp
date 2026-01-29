//package com.example.book_test_connection.converters;
//
//import org.springframework.stereotype.Component;
//
//import javax.xml.transform.*;
//import javax.xml.transform.stream.StreamResult;
//import javax.xml.transform.stream.StreamSource;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.StringWriter;
//import java.nio.file.Path;
//
//@Component
//public class Fb2ToHtmlConverter implements BookToHtmlConverter{
//
//    @Override
//    public boolean supports(String extension) {
//        return "fb2".equalsIgnoreCase(extension);
//    }
//
//    @Override
//    public String convertToHtml(Path sourcePath) throws Exception {
//        try (InputStream xsltStream = Fb2ToHtmlConverter.class
//                .getClassLoader()
//                .getResourceAsStream("fb2.xsl")) {
//
//            if (xsltStream == null) {
//                throw new IllegalStateException("XSLT файл 'fb2-to-html.xsl' не найден в classpath");
//            }
//
//            try (InputStream fb2Stream = new FileInputStream(sourcePath.toFile())) {
//                Source xmlSource = new StreamSource(fb2Stream);
//                Source xsltSource = new StreamSource(xsltStream);
//
//                TransformerFactory factory = TransformerFactory.newInstance();
//                factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
//
//                Transformer transformer = factory.newTransformer(xsltSource);
//                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//                transformer.setOutputProperty(OutputKeys.METHOD, "html"); // ← важно для корректного HTML
//
//                StringWriter writer = new StringWriter();
//                Result result = new StreamResult(writer);
//
//                transformer.transform(xmlSource, result);
//                return writer.toString();
//            }
//        }
//    }
//}
