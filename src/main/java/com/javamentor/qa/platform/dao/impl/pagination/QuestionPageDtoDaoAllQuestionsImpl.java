package com.javamentor.qa.platform.dao.impl.pagination;


import com.javamentor.qa.platform.dao.abstracts.pagination.PageDtoDao;
import com.javamentor.qa.platform.dao.impl.pagination.transformer.QuestionPageDtoResultTransformer;
import com.javamentor.qa.platform.models.dto.QuestionViewDto;
import com.javamentor.qa.platform.models.entity.pagination.PaginationData;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;

@Repository("QuestionPageDtoDaoAllQuestionsImpl")
public class QuestionPageDtoDaoAllQuestionsImpl implements PageDtoDao<QuestionViewDto> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<QuestionViewDto> getPaginationItems(PaginationData properties) {
        int itemsOnPage = properties.getItemsOnPage();
        int offset = (properties.getCurrentPage() - 1) * itemsOnPage;
        return (List<QuestionViewDto>) entityManager.createQuery("SELECT DISTINCT " +
                        "q.id, " +
                        "q.title, " +
                        "u.id, " +
                        "u.fullName, " +
                        "u.imageLink, " +
                        "q.description, " +
                        "q.persistDateTime," +
                        "q.lastUpdateDateTime, " +
                        "(SELECT SUM(r.count) from Reputation r WHERE r.author.id = q.user.id), " +
                        "(coalesce((select count(a.id) from Answer a where a.question.id = q.id),0)) as answerCounter, " +
                        "(coalesce((select sum(case when v.vote = 'UP_VOTE' then 1 else -1 end) from VoteQuestion v where v.question.id = q.id), 0))," +
                        "(select count(bm.id) from BookMarks bm where bm.question.id= q.id and bm.user.id=: userId ) " +
                        "from Question q JOIN User u on q.user.id = u.id " +
                        "where ((:trackedTags) IS NULL OR q.id IN (select q.id from Question q join q.tags t where t.id in (:trackedTags))) and " +
                        "((:ignoredTags) IS NULL OR q.id not IN (select q.id from Question q join q.tags t where t.id in (:ignoredTags)))")
                .setParameter("trackedTags", properties.getProps().get("trackedTags"))
                .setParameter("ignoredTags", properties.getProps().get("ignoredTags"))
                .setParameter("userId",properties.getProps().get("userId"))
                .setFirstResult(offset)
                .setMaxResults(itemsOnPage)
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(new QuestionPageDtoResultTransformer())
                .list();

    }
    @Override
    public Long getTotalResultCount(Map<String, Object> properties) {
        return (Long) entityManager.createQuery("select distinct count(distinct q.id) from Question q join q.tags t WHERE " +
                        "((:trackedTags) IS NULL OR t.id IN (:trackedTags)) AND" +
                        "((:ignoredTags) IS NULL OR q.id NOT IN (SELECT q.id FROM Question q JOIN q.tags t WHERE t.id IN (:ignoredTags)))")
                .setParameter("trackedTags", properties.get("trackedTags"))
                .setParameter("ignoredTags", properties.get("ignoredTags"))
                .getSingleResult();
    }
}
