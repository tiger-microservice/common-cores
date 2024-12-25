package com.tiger.cores.listeners;

import com.tiger.cores.constants.enums.Action;
import com.tiger.cores.utils.JsonUtil;
import com.tiger.cores.utils.SpringContextUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * @PrePersist -> (INSERT) -> @PostPersist
 * @PreUpdate -> (UPDATE) -> @PostUpdate
 * @PreRemove -> (DELETE) -> @PostRemove
 * @PostLoad được gọi sau khi thực thể được load.
 */
@Slf4j
public class AuditEntityListener {

    @PrePersist
    public void prePersist(Object target) { // Persistence logic
        perform(target, Action.INSERT);
    }
    @PreUpdate
    public void preUpdate(Object target) { //Updation logic }
        perform(target, Action.UPDATE);
    }
    @PreRemove
    public void preRemove(Object target) { //Removal logic }
        perform(target, Action.DELETE);
    }

    @Transactional(Transactional.TxType.MANDATORY)
    protected void perform(Object target, Action action) {
        EntityManager entityManager = SpringContextUtil.getBean(EntityManager.class);
        log.info("Action: {}", action);
        log.info("Value: {}", JsonUtil.castToString(target));
//        entityManager.persist();
    }
}
