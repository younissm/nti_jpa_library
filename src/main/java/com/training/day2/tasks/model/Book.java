package com.training.day2.tasks.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Book {
    public Book(String title, int numPages) {
        this.title = title;
        this.numPages = numPages;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Book() {
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.getBooks().add(this);

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;
    private int numPages;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Publisher publisher;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private final List<Category> categories = new ArrayList<>();

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", numPages=" + numPages +
                '}';
    }

//    @ManyToOne
    private Author author;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumPages() {
        return numPages;
    }

    public void setNumPages(int numPages) {
        this.numPages = numPages;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
