package com.training.day2.tasks;

import com.training.day2.tasks.model.*;
import com.training.day2.tasks.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.hibernate.LazyInitializationException;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = JPAUtil.getEmFactory();
        try (EntityManager em = emf.createEntityManager()) {
            Category javaProgramming = new Category("Java Programming");
            Category springCore = new Category("Spring Core");
            Category softwareSecurity = new Category("Software Security");
            Category persistence = new Category("Data Persistence & JPA");
            Category systemDesign = new Category("System Design & Architecture");
            Category cloudComputing = new Category("Cloud Computing & Microservices");

            Publisher manning = new Publisher("Manning Publications", 1990);
            Publisher oReilly = new Publisher("O'Reilly Media", 1978);

            Author spilca = new Author("Laurentiu Spilca");
            Author bauer = new Author("Christian Bauer");
            Author king = new Author("Gavin King");
            Author walls = new Author("Craig Walls");
            Author richards = new Author("Mark Richards");
            Author ford = new Author("Neal Ford");

            Book startHere = new Book("Spring Start Here", 416);
            Book springSecurity = new Book("Spring Security in Action, Second Edition", 528);
            Book javaPersistence = new Book("Java Persistence with Hibernate, Third Edition", 600);
            Book springInAction = new Book("Spring in Action, Sixth Edition", 520);
            Book softwareArchitecture = new Book("Software Architecture Patterns", 250);
            Book buildingMicroservices = new Book("Building Microservices: Designing Fine-Grained Systems", 610);
            Book functionalThinking = new Book("Functional Thinking: Paradigm Over Syntax", 180);

            Employee adminManager = new Employee("Sarah Jenkins", 34, "Library Manager");
            Employee deskClerk = new Employee("Ahmed Saad", 25, "Front Desk Clerk");

            Customer customer1 = new Customer("Amr Gamal", 20, "1233415 Applewood St.");
            Customer customer2 = new Customer("Aya Zaki", 22, "789 Blue Nile Boulevard");
            Customer customer3 = new Customer("John Doe", 29, "456 Oak Avenue");

            startHere.setPublisher(manning);
            springSecurity.setPublisher(manning);
            javaPersistence.setPublisher(manning);
            springInAction.setPublisher(manning);
            buildingMicroservices.setPublisher(manning);

            softwareArchitecture.setPublisher(oReilly);
            functionalThinking.setPublisher(oReilly);


            spilca.addBook(startHere);
            spilca.addBook(springSecurity);
            bauer.addBook(javaPersistence);
            walls.addBook(springInAction);
            king.addBook(buildingMicroservices);
            richards.addBook(softwareArchitecture);
            ford.addBook(functionalThinking);

            startHere.addCategory(javaProgramming);
            startHere.addCategory(springCore);

            springSecurity.addCategory(javaProgramming);
            springSecurity.addCategory(springCore);
            springSecurity.addCategory(softwareSecurity);

            javaPersistence.addCategory(javaProgramming);
            javaPersistence.addCategory(persistence);

            springInAction.addCategory(javaProgramming);
            springInAction.addCategory(springCore);

            buildingMicroservices.addCategory(cloudComputing);
            buildingMicroservices.addCategory(systemDesign);

            softwareArchitecture.addCategory(systemDesign);

            functionalThinking.addCategory(javaProgramming);

            em.getTransaction().begin();
            em.persist(spilca);
            em.persist(bauer);
            em.persist(king);
            em.persist(walls);
            em.persist(richards);
            em.persist(functionalThinking);

            em.persist(adminManager);
            em.persist(deskClerk);
            em.persist(customer1);
            em.persist(customer2);
            em.persist(customer3);
            em.getTransaction().commit();


        }


        try (EntityManager em = emf.createEntityManager()) {
            // Find all books by a given author's name using JPQL.
            Query query = em.createQuery("SELECT a.books FROM Author a WHERE a.name =:name");
            query.setParameter("name", "Laurentiu Spilca");
            List<Book> booksByAuthor =  query.getResultList();
            System.out.println(booksByAuthor);

            // Find all books belonging to a given publisher.
            query = em.createQuery("SELECT b FROM Book b JOIN FETCH b.publisher p WHERE p.name =:name");
            query.setParameter("name", "O'Reilly Media");
            List<Book> booksByPublisher =  query.getResultList();
            System.out.println(booksByPublisher);
            System.out.println(booksByPublisher.stream().map(b->b.getPublisher()).toList());

            // Find a specific Book by id using a positional parameter.
            query = em.createQuery("SELECT b FROM Book b WHERE b.id =?1");
            query.setParameter(1, 2);
            Book bookById = (Book) query.getSingleResult();
            System.out.println(bookById);

            // Fetch an Author together with all of their Books using JOIN FETCH.
            query = em.createQuery("SELECT a FROM Author a JOIN FETCH a.books WHERE a.name =:name");
            query.setParameter("name", "Christian Bauer");
            Author authorWithBooks = (Author) query.getSingleResult();
            System.out.println("Author: " + authorWithBooks);
            System.out.println("Books: " + authorWithBooks.getBooks());


            // Write an aggregate query that returns each author's name and the number of books they have using COUNT and
            // GROUP BY.
            query = em.createQuery("SELECT a.name, COUNT(b.id) FROM Author a JOIN a.books b ON a = b.author GROUP BY a");
            for (Object[] rows : (List<Object[]>)query.getResultList()) {
                System.out.println("Author name: " + (String)rows[0]+ (", No. Books written: " + (long)rows[1]));
            }

            // Create a Criteria API query that finds books by title.
            // Extend the Criteria API query so that the title and author-name filters are optional and predicates are added
            // dynamically.
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Book> bookCriteriaQuery = cb.createQuery(Book.class);
            Root<Book> root = bookCriteriaQuery.from(Book.class);

            String titleFilter = "Spring Start Here";
            String authorNameFilter = "";
            List<Predicate> predicates = new ArrayList<>();
            if (titleFilter != null && !titleFilter.isEmpty()) {
                predicates.add(cb.equal(root.get("title"), titleFilter));
            }

            Join<Book, Author> authorJoin = root.join("author");

            if (authorNameFilter != null && !authorNameFilter.isEmpty()) {
                predicates.add(cb.equal(authorJoin.get("name"), authorNameFilter));
            }

            bookCriteriaQuery.where(predicates.toArray(new Predicate[0])).select(root);

            Query bookQuery = em.createQuery(bookCriteriaQuery);
            List<Book> booksUsingCriteria = bookQuery.getResultList();

            System.out.println(booksUsingCriteria);

        }

        //  Compare a normal LAZY query with the JOIN FETCH version and explain why JOIN FETCH can prevent
        // LazyInitializationException for that use case.
        try (EntityManager em = emf.createEntityManager()) {

            Query query = em.createQuery("SELECT b FROM Book b WHERE b.id=3");
            //query = em.createQuery("SELECT b FROM Book b JOIN FETCH b.categories WHERE b.id=3");
            Book book = (Book) query.getSingleResult();
            em.close();
            List<Category> categories = book.getCategories();
            System.out.println(categories);
        } catch (LazyInitializationException lzie) {
            System.out.println("A normal query does not fetch the categories right away since its fetch type is lazy");
        }

        try (EntityManager em = emf.createEntityManager()) {
//            Query query = em.createQuery("SELECT b FROM Book b WHERE b.id=3");
            Query query = em.createQuery("SELECT b FROM Book b JOIN FETCH b.categories WHERE b.id=3");
            Book book = (Book) query.getSingleResult();
            em.close();
            List<Category> categories = book.getCategories();
            System.out.println(categories);
        }

        // Write one query using standard JPQL and explain how a Hibernate-specific HQL feature would differ.
        try (EntityManager em = emf.createEntityManager()) {
//            Query query = em.createQuery("SELECT b FROM Book b WHERE b.id=3");
            Query jpqlQuery = em.createQuery("SELECT b FROM Book b JOIN FETCH b.publisher p WHERE p.name=?1");
            jpqlQuery.setParameter(1, "O'Reilly Media");

            Query hqlQuery = em.createQuery("FROM Book b WHERE b.publisher.name=?1");
            hqlQuery.setParameter(1, "O'Reilly Media");


            List<Book> books1 = jpqlQuery.getResultList();
            List<Book> books2 = hqlQuery.getResultList();

            System.out.println(books1);
            System.out.println(books2);
        }
    }
}
