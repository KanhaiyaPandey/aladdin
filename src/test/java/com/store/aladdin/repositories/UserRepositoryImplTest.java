package com.store.aladdin.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.store.aladdin.queries.UserRepositoryImpl;

@SpringBootTest
public class UserRepositoryImplTest {

    @Autowired 
    private UserRepositoryImpl userRepositoryImpl;
    
    @Test
    public void testSaveNewUser(){
       userRepositoryImpl.getUserForSA();
    }
}



