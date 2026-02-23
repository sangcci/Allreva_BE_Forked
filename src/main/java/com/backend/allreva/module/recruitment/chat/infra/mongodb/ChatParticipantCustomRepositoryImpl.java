package com.backend.allreva.module.recruitment.chat.infra.mongodb;

import com.backend.allreva.module.recruitment.chat.domain.participant.ChatParticipant;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ChatParticipantCustomRepositoryImpl implements ChatParticipantCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Long findLastReadMessageNumber(
            final Long memberId,
            final Long chatId,
            final ChatType chatType
    ) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(memberId));

        Criteria ByChatIdAndType = Criteria.where("chatId").is(chatId)
                .and("chatType").is(chatType);

        query.addCriteria(
                Criteria.where("chatSummaries")
                        .elemMatch(ByChatIdAndType)
        );
        query.fields().include("chatSummaries.$");
        ChatParticipant result = mongoTemplate
                .findOne(query, ChatParticipant.class);

        if (result != null && result.getChatSummaries() != null) {
            return result.getChatSummaries()
                    .first()
                    .getLastReadMessageNumber();
        }
        return 0L;
    }
}
