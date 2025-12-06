package br.com.bytebank.api.domain.base;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // Indica que esta é uma superclasse e seus campos devem ser mapeados nas subclasses.
@EntityListeners(AuditingEntityListener.class) // Ativa o "ouvinte" de auditoria do JPA para esta classe.
public abstract class Auditable {
    @CreatedDate // Marca o campo para ser preenchido com a data de criação.
    private LocalDateTime createdAt;

    @LastModifiedDate // Marca o campo para ser preenchido com a data da última modificação.
    private LocalDateTime updatedAt;
}
