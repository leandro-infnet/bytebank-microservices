package br.com.bytebank.api.domain.user;

import br.com.bytebank.api.domain.base.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "document_number", unique = true)
    private String documentNumber;
}
