package com.example.bookshop.services;

import com.example.bookshop.entities.*;
import com.example.bookshop.repositories.AuthorRepository;
import com.example.bookshop.repositories.BookRepository;
import com.example.bookshop.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeedServiceImpl implements SeedService {

    private static final String RESOURCE_FOR_AUTHOR_FILE_PATH = "src\\main\\resources\\files\\authors.txt";
    private static final String RESOURCE_FOR_CATEGORIES_FILE_PATH = "src\\main\\resources\\files\\categories.txt";
    private static final String RESOURCE_FOR_BOOKS_FILE_PATH = "src\\main\\resources\\files\\books.txt";


    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void seedAuthors() throws IOException {
        Files.readAllLines(Path.of(RESOURCE_FOR_AUTHOR_FILE_PATH))
                .stream()
                .filter(s -> !s.isBlank())
                .map(s -> s.split(" "))
                .map(names -> new Author(names[0], names[1]))
                .forEach(authorRepository::save);
    }

    @Override
    public void seedCategories() throws IOException {
        Files.readAllLines(Path.of(RESOURCE_FOR_CATEGORIES_FILE_PATH))
                .stream()
                .filter(s -> !s.isBlank())
                .map(Category::new)
                .forEach(categoryRepository::save);
    }

    @Override
    public void seedBooks() throws IOException {
        Files.readAllLines(Path.of(RESOURCE_FOR_BOOKS_FILE_PATH))
                .stream()
                .filter(s -> !s.isBlank())
                .map(this::getBookObject)
                .forEach(bookRepository::save);
    }

    private Book getBookObject(String line) {

        String[] bookParts = line.split(" ");

        int editionTypeIndex = Integer.parseInt(bookParts[0]);
        EditionType editionType = EditionType.values()[editionTypeIndex];

        LocalDate publishDate = LocalDate.parse(bookParts[1], DateTimeFormatter.ofPattern("d/M/yyyy"));

        int copies = Integer.parseInt(bookParts[2]);

        BigDecimal price = new BigDecimal(bookParts[3]);

        int ageRestrictionIndex = Integer.parseInt(bookParts[4]);
        AgeRestriction ageRestriction = AgeRestriction.values()[ageRestrictionIndex];

        String title = Arrays.stream(bookParts)
                .collect(Collectors.joining(" "));

        Author author = authorService.getRandomAuthor();
        Set<Category> categories = categoryService.getRandomCategories();

        return new Book(title, editionType, price, copies, publishDate, ageRestriction, author, categories);
    }
}
