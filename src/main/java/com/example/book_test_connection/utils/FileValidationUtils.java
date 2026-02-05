package com.example.book_test_connection.utils;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileValidationUtils {

    // Разрешённые расширения (в нижнем регистре)
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("epub")
    );

    public static boolean hasAllowedExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return false;
        }
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return false; // нет расширения
        }
        String extension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    public static String getAllowedExtensionsAsString() {
        return String.join(", ", ALLOWED_EXTENSIONS);
    }
}