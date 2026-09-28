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
        //query.addCriteria(Criteria.where("name").is("Yogi"));

        // OR

        /*query.addCriteria(Criteria.where("email").exists(true));
        query.addCriteria(Criteria.where("email").ne("null"));
        query.addCriteria(Criteria.where("email").ne(""));
        query.addCriteria(Criteria.where("sentimentalAnalysisEnabled").is(true));*/

        // OR

        query.addCriteria(
                Criteria.where("email")
                        .regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$", "i")
        );

        query.addCriteria(
                Criteria.where("sentimentalAnalysisEnabled").is(true)
        );
        // OR expression checking if you want and above is AND expression checking
        /*Criteria criteria = new Criteria();
        query.addCriteria(criteria.orOperator(
                Criteria.where("email").exists(true),
                Criteria.where("email").ne("null").ne(""),
                Criteria.where("sentimentalAnalysisEnabled").is(true)
        ));*/

        return mongoTemplate.find(query, User.class);
    }

    public List<User> getUserForSentimentAnalysisForUserName()
    {
        Query query = new Query();

        query.addCriteria(Criteria.where("name").is("Yogi"));

        return mongoTemplate.find(query, User.class);
    }
}
