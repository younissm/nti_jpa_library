# JPA Day 2 — Library Management System

This project implements the required library domain using JPA, Hibernate, and an H2 in-memory database. The model covers entity relationships, ownership, cascading, inheritance, fetch strategy, and JPQL/Criteria queries.

## Relationship mapping

### Author and Book

The Author–Book association is modeled as a bidirectional one-to-many relationship.

- Author.books is the inverse side
- Book.author is the owning side
- the foreign key is stored in the Book table as author_id
- the inverse collection uses mappedBy = "author"

This is required because the owning side is the one that contains the foreign key. The Book table stores the author_id column, so Book is the owning side.

The Author side is configured with:
- cascade = CascadeType.ALL
- fetch = FetchType.LAZY

The Book side is configured with:
- @ManyToOne(fetch = FetchType.LAZY)
- @JoinColumn(name = "author_id")

The addBook() helper keeps both sides synchronized by setting the child author and adding the book to the parent collection.

### Publisher

The Book to Publisher relation is a many-to-one association.

- Book.publisher is the owning side
- the foreign key is stored in the Book table

### Category

The Book to Category relation is a many-to-many association.

- a join table is required
- the join table stores the relationship between Book and Category
- the join table contains the foreign keys from both sides

## Fetching

LAZY fetching is used for collection relationships and for the to-one association where appropriate.

This means the application must explicitly fetch the related data when needed. This is important for the use case where an Author is queried together with their Books, and for comparisons between normal LAZY access and JOIN FETCH access.

## Inheritance

The Employee / Customer hierarchy uses TABLE_PER_CLASS inheritance.

This strategy was chosen because:
- Employee and Customer have different properties
- each subclass can have its own table
- the subclass data remains explicit and separate

## Persistence configuration

The persistence.xml file is configured for H2 in-memory usage.

The required configuration includes:
- H2 in-memory database URL
- H2 JDBC driver
- Hibernate schema generation/update
- resource-local transaction type

## Query design

Finding books by author name uses JPQL because the query must traverse the owning relationship from Book back to its Author. The author name parameter is set as a named parameter to avoid SQL injection.

Finding books by publisher name uses the same JPQL approach, with a JOIN to access the publisher entity.

Finding a specific Book by id uses a positional parameter because the query is simple and a single value is being searched.

Fetching an Author together with all of their Books requires JOIN FETCH because without it, the books collection would remain lazy and could not be accessed after the EntityManager closes. JOIN FETCH forces Hibernate to load the collection in the initial query.

The COUNT and GROUP BY query aggregates books per author. The GROUP BY is necessary to organize the results by author, and COUNT(b.id) computes the total number of books for each author in the group.

The Criteria API query for books by title uses the CriteriaBuilder to construct a type-safe query. This approach is chosen because it allows the query to be built programmatically without string concatenation or risk of syntax errors.

The dynamic filtering in the Criteria query adds predicates only if the filter values are non-empty. This allows the same query builder to support multiple search scenarios: title only, author only, both, or neither.

## LazyInitializationException and JOIN FETCH

A normal LAZY query may throw LazyInitializationException when the association is accessed after the EntityManager is closed.

The JOIN FETCH version avoids this problem for the relevant use case because the association is loaded in the same query as the root entity, so it is available when the result is used.

## JPQL vs HQL

The project includes standard JPQL as required by the JPA specification. A Hibernate-specific HQL form is also used for comparison to show the difference between portable JPA syntax and Hibernate-specific query behavior.

Standard JPQL requires an explicit SELECT clause and uses JOIN syntax to fetch related entities. Hibernate HQL allows implicit navigation through associations without an explicit join in some cases, which is a convenience but reduces portability to other JPA providers.