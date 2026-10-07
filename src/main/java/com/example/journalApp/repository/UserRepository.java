package com.example.journalApp.repository;
import com.example.journalApp.entity.UserEntity;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, ObjectId> {
    // This interface will automatically inherit methods for CRUD operations from MongoRepository.
    // You can also define custom query methods here if needed.
    UserEntity findByUserName(String userName);

    void deleteByUserName(@NonNull String userName);
}
