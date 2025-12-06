package br.com.bytebank.api.domain.transaction;

import br.com.bytebank.api.domain.base.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "transactions")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Transaction extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "source_account_id")
    private Long sourceAccountId;

    @Column(name = "destination_account_id")
    private Long destinationAccountId;

    private String description;
}
