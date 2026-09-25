package com.training.day2.tasks.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory emFactory = buildEmFactory();

    private static EntityManagerFactory buildEmFactory() {
        try {
            return Persistence.createEntityManagerFactory("tasks");
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static EntityManagerFactory getEmFactory() {
        return emFactory;
    }
}