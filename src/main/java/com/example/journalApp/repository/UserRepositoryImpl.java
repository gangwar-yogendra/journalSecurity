package com.example.journalApp.repository;
import com.example.journalApp.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepositoryImpl{
    @Autowired
    private MongoTemplate mongoTemplate;

    // This interface will automatically inherit methods for CRUD operations from MongoRepository.
    // You can also define custom query methods here if needed.

    // Criteria and Query
    public List<User> getUserForSentimentAnalysis()
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("Yogi"));

        return mongoTemplate.find(query, User.class);
    }
}
