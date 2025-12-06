package br.com.bytebank.api.domain.account;

import br.com.bytebank.api.domain.base.Auditable;
import br.com.bytebank.api.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Audited
@Table(name = "accounts")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class Account extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    /*
     * LAZY informa ao JPA para só carregar os dados do
     * User associado quando explicitamente solicitados.
     *
     * Campo user é do tipo User (não Long) porque JPA é OOP,
     * então crio um link direto para o objeto User inteiro.
     */

    @Column(name = "agency_number")
    private String agencyNumber;

    @Column(name = "account_number", unique = true)
    private String accountNumber;

    private BigDecimal balance; // BigDecimal ⇾ + precisão

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;

    @Column(name = "is_active")
    private boolean isActive;
}
