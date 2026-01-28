package com.example.book_test_connection.service;


import com.example.book_test_connection.converters.BookToHtmlConverter;
import com.example.book_test_connection.entity.Book;
import com.example.book_test_connection.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class HtmlConversionService {

    private static final Logger log = LoggerFactory.getLogger(HtmlConversionService.class);

    private final BookRepository bookRepository;
    private final List<BookToHtmlConverter> converters;

    @Value("${app.upload.dir:./uploads/books}")
    private String uploadDir;

    public HtmlConversionService(BookRepository bookRepository, List<BookToHtmlConverter> converters) {
        this.bookRepository = bookRepository;
        this.converters = converters;
    }

    /**
     * Асинхронно конвертирует исходный файл книги в HTML.
     * Вызывается после сохранения книги с файлом.
     *
     * @param bookId ID книги, у которой есть filePath
     */
    @Async
    public void convertBookToHtml(Long bookId) {
        try {
            log.info("Начата конвертация книги ID={}", bookId);

            // 1. Найти книгу
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Книга не найдена для конвертации: " + bookId));

            // 2. Проверить наличие исходного файла
            if (book.getFilePath() == null || book.getFilePath().isBlank()) {
                log.warn("Пропуск конвертации: у книги ID={} нет filePath", bookId);
                return;
            }

            Path sourcePath = Paths.get(book.getFilePath());
            if (!Files.exists(sourcePath)) {
                log.warn("Исходный файл не найден: {}", sourcePath);
                return;
            }

            // 3. Извлечь расширение
            String filename = sourcePath.getFileName().toString();
            String extension = "";
            int lastDotIndex = filename.lastIndexOf('.');
            if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
                extension = filename.substring(lastDotIndex + 1).toLowerCase();
            }

            // 4. Найти подходящий конвертер
            BookToHtmlConverter converter = findConverter(extension);
            log.debug("Выбран конвертер {} для формата .{}", converter.getClass().getSimpleName(), extension);

            // 5. Выполнить конвертацию
            String htmlContent = converter.convertToHtml(sourcePath);
            log.debug("Конвертация завершена, длина HTML: {} символов", htmlContent.length());

            // 6. Сохранить HTML на диск
            String htmlFilename = UUID.randomUUID() + ".html";
            Path htmlPath = Paths.get(uploadDir, htmlFilename);
            Files.createDirectories(htmlPath.getParent()); // гарантируем, что папка существует
            Files.writeString(htmlPath, htmlContent);
            log.info("HTML сохранён: {}", htmlPath);

            // 7. Обновить книгу в БД
            book.setHtmlPath(htmlPath.toString());
            bookRepository.save(book);
            log.info("Книга ID={} успешно обновлена с htmlPath", bookId);

        } catch (Exception e) {
            log.error("Критическая ошибка при конвертации книги ID={}", bookId, e);
            // Здесь можно добавить уведомление (email, метрики и т.д.)
        }
    }

    private BookToHtmlConverter findConverter(String extension) {
        return converters.stream()
                .filter(converter -> converter.supports(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Конвертация формата ." + extension + " не поддерживается. Доступные форматы определяются зарегистрированными конвертерами."
                ));
    }
}
