package com.training.day2.tasks.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    public Author(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    public Author() {
    }

//    @OneToMany(mappedBy = "author",
//    orphanRemoval = true,
//    cascade = CascadeType.ALL,
//    fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        book.setAuthor(this);
        books.add(book);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
