package com.ecceft.service;

import com.ecceft.model.AuditLog;
import com.ecceft.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void logEvent(
            String eventType,
            String actorAccountId,
            String transactionId,
            String eventDetail,
            String signatureR,
            String signatureS
    ) {

        AuditLog log =
                AuditLog.builder()
                        .eventType(eventType)
                        .actorAccountId(actorAccountId)
                        .transactionId(transactionId)
                        .eventDetail(eventDetail)
                        .signatureR(signatureR)
                        .signatureS(signatureS)
                        .createdAt(LocalDateTime.now())
                        .build();

        auditLogRepository.save(log);
    }
    
}