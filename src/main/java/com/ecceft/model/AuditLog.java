package com.ecceft.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.ecceft.service.AuditService;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private String actorAccountId;

    private String transactionId;

    @Lob
    private String eventDetail;

    @Lob
    private String signatureR;

    @Lob
    private String signatureS;

    private LocalDateTime createdAt;
    

}
