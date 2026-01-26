package com.backend.allreva.module.chat.domain.message;

import com.backend.allreva.module.chat.infra.mongodb.GroupMessageCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupMessageRepository extends MongoRepository<GroupMessage, String>, GroupMessageCustomRepository {
}
