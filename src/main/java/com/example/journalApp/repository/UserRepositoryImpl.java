package com.example.journalApp.repository;
import com.example.journalApp.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserRepositoryImpl{
    @Autowired
    private MongoTemplate mongoTemplate;

    // This interface will automatically inherit methods for CRUD operations from MongoRepository.
    // You can also define custom query methods here if needed.

    // Criteria and Query
    public List<UserEntity> getUserForSentimentAnalysis()
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

        return mongoTemplate.find(query, UserEntity.class);
    }

    public List<UserEntity> getUserForSentimentAnalysisForUserName()
    {
        Query query = new Query();

        query.addCriteria(Criteria.where("name").is("Yogi"));

        return mongoTemplate.find(query, UserEntity.class);
    }

    public List<UserEntity> findUsersWhoHaveNotWrittenJournalForTheDay() {
        Date startOfDay = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date startOfTomorrow = Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        return getUserForSentimentAnalysis().stream()
                .filter(user -> user.getJournalEntries() == null || user.getJournalEntries().stream()
                        .noneMatch(entry -> entry.getDate() != null
                                && !entry.getDate().before(startOfDay)
                                && entry.getDate().before(startOfTomorrow)))
                .collect(Collectors.toList());
    }
}
