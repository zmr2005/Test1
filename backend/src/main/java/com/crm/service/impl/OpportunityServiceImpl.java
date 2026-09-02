package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.BusinessException;
import com.crm.entity.Opportunity;
import com.crm.entity.OpportunityStageLog;
import com.crm.mapper.OpportunityMapper;
import com.crm.mapper.OpportunityStageLogMapper;
import com.crm.service.OpportunityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OpportunityServiceImpl extends ServiceImpl<OpportunityMapper, Opportunity>
        implements OpportunityService {

    private static final List<String> STAGES =
            List.of("contact", "quotation", "negotiation", "won", "lost");

    @Resource
    private OpportunityStageLogMapper stageLogMapper;

    @Override
    @Transactional
    public void changeStage(Long opportunityId, String stage, String remark, Long operatorId) {
        Opportunity opp = getById(opportunityId);
        if (opp == null) {
            throw new BusinessException("商机不存在");
        }
        if (!STAGES.contains(stage)) {
            throw new BusinessException("非法商机阶段：" + stage);
        }
        String fromStage = opp.getStage();
        if (stage.equals(fromStage)) {
            throw new BusinessException("阶段未发生变化");
        }
        opp.setStage(stage);
        updateById(opp);

        OpportunityStageLog log = new OpportunityStageLog();
        log.setOpportunityId(opportunityId);
        log.setFromStage(fromStage);
        log.setToStage(stage);
        log.setRemark(remark);
        log.setOperatorId(operatorId);
        stageLogMapper.insert(log);
    }
}
