package dev.mednikov.accounting.currencies.models;

import java.util.UUID;

import dev.mednikov.accounting.organizations.models.Organization;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "currencies_currency", uniqueConstraints = {@UniqueConstraint(columnNames = {"organization_id", "currency_code"})})
public class Currency {

    @Id @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organization organization;

    @Column(name = "currency_name", nullable = false)
    private String name;

    @Column(name = "currency_code", nullable = false)
    private String code;

    @Column(name = "is_primary", nullable = false)
    private boolean primary;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
