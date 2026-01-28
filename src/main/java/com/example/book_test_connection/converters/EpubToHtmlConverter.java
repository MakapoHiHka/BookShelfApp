package com.example.book_test_connection.converters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.stream.Collectors;

@Component
public class EpubToHtmlConverter implements BookToHtmlConverter{

    private static final String CONTAINER_PATH = "META-INF/container.xml";
    private static final String DEFAULT_OPF_REL_PATH = "OEBPS/content.opf"; // fallback
    private static final int WORDS_PER_PAGE = 150;

    @Override
    public boolean supports(String extension) {
        return "epub".equalsIgnoreCase(extension);
    }


    /**
     * Конвертирует EPUB-файл в единый HTML-документ с отслеживанием прогресса.
     *
     * @param sourcePath путь к .epub файлу
     * @return итоговый HTML как строка
     * @throws Exception при ошибках чтения/парсинга
     */
    public String convertToHtml(Path sourcePath) throws Exception {
        Path tempDir = Files.createTempDirectory("epub_extract_");
        try {
            unzipEpub(sourcePath, tempDir);
            Path opfPath = findOpfFile(tempDir);
            if (opfPath == null) throw new IllegalStateException("OPF not found");

            List<Path> contentFiles = getContentFilesInOrder(tempDir, opfPath);
            if (contentFiles.isEmpty()) throw new IllegalStateException("No content");

            // Собираем полный HTML-документ
            Document fullDoc = new Document("");
            Element body = fullDoc.body();
            for (Path file : contentFiles) {
                String html = Files.readString(file, StandardCharsets.UTF_8);
                Document part = Jsoup.parse(html, "", Parser.xmlParser());
                if (part.body() != null) {
                    for (Node child : part.body().childNodes()) {
                        body.appendChild(child.clone());
                    }
                }
            }

            // Разбиваем на страницы с сохранением разметки
            Document pagedDoc = splitIntoWordPagesWithStructure(fullDoc, WORDS_PER_PAGE);

            addStylesAndScript(pagedDoc);
            return pagedDoc.html();
        } finally {
            deleteRecursively(tempDir);
        }
    }

    // === ОСНОВНОЙ МЕТОД: разбивка с сохранением структуры ===
    private Document splitIntoWordPagesWithStructure(Document fullDoc, int wordsPerPage) {
        // Создаём полноценный HTML-документ
        Document result = new Document("");
        Element html = result.appendElement("html");
        Element head = html.appendElement("head");
        head.appendElement("meta").attr("charset", "utf-8");
        head.appendElement("title").text("Book");
        Element body = html.appendElement("body");

        // Получаем все узлы из исходного тела
        List<Node> originalNodes = new ArrayList<>();
        for (Node node : fullDoc.body().childNodes()) {
            originalNodes.add(node.clone());
        }

        // Считаем общее количество слов
        int totalWords = countWordsInNodes(originalNodes);
        int totalPages = (int) Math.ceil((double) totalWords / wordsPerPage);

        // Распределяем узлы по страницам
        List<List<Node>> pages = distributeNodesByWordCount(originalNodes, wordsPerPage);

        // Добавляем каждую страницу в body
        for (int i = 0; i < pages.size(); i++) {
            Element pageDiv = createPageDiv(i + 1);
            for (Node node : pages.get(i)) {
                pageDiv.appendChild(node.clone());
            }
            body.appendChild(pageDiv);
        }

        return result;
    }

    // Вспомогательный класс для возврата результата обработки узла
    private static class PageSplitResult {
        List<Node> fragments = new ArrayList<>();
        int totalWords = 0;
    }

    // Рекурсивная обработка узла с подсчётом слов
    private PageSplitResult processNode(Node node, int currentWordCount, int wordsPerPage) {
        PageSplitResult result = new PageSplitResult();

        if (node instanceof TextNode) {
            TextNode textNode = (TextNode) node;
            String text = textNode.getWholeText();
            if (text == null || text.trim().isEmpty()) {
                result.fragments.add(textNode.clone());
                return result;
            }

            String[] words = text.split("\\s+");
            result.totalWords = words.length;

            // Создаём новый текстовый узел с тем же текстом
            result.fragments.add(new TextNode(text+""));
        } else if (node instanceof Element) {
            Element elem = (Element) node;
            Element clone = new Element(elem.tagName());
            clone.attributes().addAll(elem.attributes());

            // Рекурсивно обрабатываем дочерние узлы
            for (Node child : elem.childNodes()) {
                PageSplitResult childResult = processNode(child, result.totalWords, wordsPerPage);
                result.totalWords += childResult.totalWords;
                for (Node frag : childResult.fragments) {
                    clone.appendChild(frag);
                }
            }
            result.fragments.add(clone);
        } else {
            result.fragments.add(node.clone());
        }

        return result;
    }

    // Создание div-страницы
    private Element createPageDiv(int pageNumber) {
        Element div = new Element("div")
                .attr("id", "page-" + pageNumber)
                .addClass("book-page");

        String baseStyle = "padding: 20px; font-family: serif; line-height: 1.6;";
        div.attr("style", baseStyle); // видима

        return div;
    }

    private void addStylesAndScript(Document doc) {
        doc.head().append("""
        <style>
            body { margin: 0; background: #f9f9f9; }
            .book-page { /* стили уже заданы через style="" */ }
        </style>
        <script>
        (function() {
            const pages = document.querySelectorAll('.book-page');
            let totalPages = pages.length;
            let currentPage = 1;

            function showPage(pageNum) {
                if (pageNum < 1) pageNum = 1;
                if (pageNum > totalPages) pageNum = totalPages;

                pages.forEach((el, i) => {
                    el.style.display = (i === pageNum - 1) ? 'block' : 'none';
                });
                currentPage = pageNum;
                sendProgress(currentPage, totalPages);
            }

            function sendProgress(page, total) {
                const token = localStorage.getItem('jwtToken');
                if (!token) {
                    console.warn('No token found. User not authenticated.');
                    return;
                }
                fetch('/bookShelf/api/books/PROGRESS_ID/progress', {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer ' + token
                    },
                    body: JSON.stringify({\s
                        pageNumber: page
                    })
                }).catch(err => console.warn('Progress send failed:', err));
            }

            // Обработка URL
            const urlParams = new URLSearchParams(window.location.search);
            const pageParam = parseInt(urlParams.get('page'), 10);
            if (!isNaN(pageParam) && pageParam > 0) {
                showPage(pageParam);
            }

            // Стрелки
            document.addEventListener('keydown', e => {
                if (e.key === 'ArrowRight') showPage(currentPage + 1);
                if (e.key === 'ArrowLeft')  showPage(currentPage - 1);
            });
        })();
        </script>
    """);
    }

    // === Вспомогательные методы (без изменений) ===
    private void unzipEpub(Path epubPath, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(epubPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path dest = targetDir.resolve(entry.getName()).normalize();
                if (!dest.startsWith(targetDir)) {
                    throw new IOException("Unsafe ZIP entry: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(dest);
                } else {
                    Files.createDirectories(dest.getParent());
                    Files.copy(zis, dest, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    private Path findOpfFile(Path rootDir) throws Exception {
        Path container = rootDir.resolve(CONTAINER_PATH);
        if (!Files.exists(container)) return null;
        String xml = Files.readString(container, StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(xml, "", Parser.xmlParser());
        var roots = doc.select("rootfile[full-path][media-type=application/oebps-package+xml]");
        if (roots.isEmpty()) return null;
        return rootDir.resolve(roots.first().attr("full-path")).normalize();
    }

    private List<Path> getContentFilesInOrder(Path rootDir, Path opfPath) throws Exception {
        String opf = Files.readString(opfPath, StandardCharsets.UTF_8);
        Document opfDoc = Jsoup.parse(opf, "", Parser.xmlParser());

        List<String> idrefs = opfDoc.select("spine itemref").eachAttr("idref");

        Map<String, String> idToHref = new HashMap<>();
        for (Element item : opfDoc.select("manifest item")) {
            String id = item.attr("id");
            String href = item.attr("href");
            String mediaType = item.attr("media-type");
            if ("application/xhtml+xml".equals(mediaType) || "text/html".equals(mediaType)) {
                idToHref.put(id, href);
            }
        }

        Path base = opfPath.getParent();
        return idrefs.stream()
                .map(idToHref::get)
                .filter(Objects::nonNull)
                .map(href -> base.resolve(href).normalize())
                .filter(Files::exists)
                .collect(Collectors.toList());
    }

    private void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } else {
            Files.deleteIfExists(path);
        }
    }

    // Подсчёт слов в списке узлов
    private int countWordsInNodes(List<Node> nodes) {
        int count = 0;
        for (Node node : nodes) {
            if (node instanceof TextNode) {
                String text = ((TextNode) node).getWholeText();
                if (text != null && !text.trim().isEmpty()) {
                    count += text.trim().split("\\s+").length;
                }
            } else if (node instanceof Element) {
                count += countWordsInNodes(((Element) node).childNodes());
            }
        }
        return count;
    }
    // Распределение узлов по страницам с учётом количества слов
    private List<List<Node>> distributeNodesByWordCount(List<Node> nodes, int wordsPerPage) {
        List<List<Node>> pages = new ArrayList<>();
        List<Node> currentPage = new ArrayList<>();
        int currentWordCount = 0;

        for (Node node : nodes) {
            int nodeWords = countWordsInSingleNode(node);

            // Если добавление узла превысит лимит — начинаем новую страницу
            if (currentWordCount + nodeWords > wordsPerPage && !currentPage.isEmpty()) {
                pages.add(currentPage);
                currentPage = new ArrayList<>();
                currentWordCount = 0;
            }

            currentPage.add(node);
            currentWordCount += nodeWords;
        }

        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }

        return pages;
    }
    private int countWordsInSingleNode(Node node) {
        if (node instanceof TextNode) {
            String text = ((TextNode) node).getWholeText();
            return (text == null || text.trim().isEmpty()) ? 0 : text.trim().split("\\s+").length;
        } else if (node instanceof Element) {
            return countWordsInNodes(((Element) node).childNodes());
        }
        return 0;
    }
}