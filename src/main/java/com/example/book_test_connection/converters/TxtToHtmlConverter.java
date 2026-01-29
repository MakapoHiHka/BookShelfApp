//package com.example.book_test_connection.converters;
//
//import org.springframework.stereotype.Component;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//@Component
//public class TxtToHtmlConverter implements BookToHtmlConverter {
//
//    @Override
//    public boolean supports(String extension) {
//        return "txt".equalsIgnoreCase(extension);
//    }
//
//    @Override
//    public String convertToHtml(Path sourcePath) throws Exception {
//        if (sourcePath == null || !Files.exists(sourcePath)) {
//            throw new IllegalArgumentException("Файл не существует: " + sourcePath);
//        }
//
//        String content = Files.readString(sourcePath);
//
//        // Экранируем специальные HTML-символы
//        String escapedContent = content
//                .replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;")
//                .replace("\"", "&quot;")
//                .replace("'", "&#39;")
//                .replace("\n", "<br>\n");
//
//        return """
//            <!DOCTYPE html>
//            <html lang="ru">
//            <head>
//                <meta charset="UTF-8">
//                <title>Конвертированный документ</title>
//                <style>
//                    body {
//                        font-family: monospace;
//                        white-space: pre-wrap;
//                        margin: 1em;
//                    }
//                </style>
//            </head>
//            <body>
//            %s
//            </body>
//            </html>
//            """.formatted(escapedContent);
//    }
//}