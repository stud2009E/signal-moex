package pab.ta.handler.moex.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import pab.ta.handler.base.asset.RuleIdentity;
import pab.ta.handler.base.component.task.BaseSignal;
import pab.ta.handler.base.component.task.BaseSignalStore;
import pab.ta.handler.base.task.Signal;
import pab.ta.handler.moex.entity.SignalEntity;

import java.time.LocalDateTime;

@Component
public class SignalService extends BaseSignalStore {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public boolean put(RuleIdentity ruleIdentity) {

        boolean isAdded = super.put(ruleIdentity);

        Signal signal = BaseSignal
                .builder()
                .ticker(ruleIdentity.seriesIdentity().info().ticker())
                .interval(ruleIdentity.seriesIdentity().tf().interval())
                .ruleId(ruleIdentity.id())
                .direction(ruleIdentity.direction())
                .type(ruleIdentity.seriesIdentity().info().type())
                .ruleGroup(ruleIdentity.group())
                .createdAt(LocalDateTime.now())
                .build();


        if (isAdded) {
            SignalEntity entity = new SignalEntity();
            entity.setTicker(signal.getTicker());
            entity.setInterval(signal.getInterval());
            entity.setRuleId(signal.getRuleId());
            entity.setDirection(signal.getDirection());
            entity.setType(signal.getType());
            entity.setRuleGroup(signal.getRuleGroup());
            entity.setCreatedAt(signal.getCreatedAt());

            try {
                em.merge(entity);
            } catch (EntityExistsException e) {
                e.printStackTrace();
            }
            em.merge(entity);
        }

        return isAdded;
    }
}