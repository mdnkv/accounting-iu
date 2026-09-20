package dev.mednikov.accounting.authorities.models;

import dev.mednikov.accounting.organizations.models.Organization;
import dev.mednikov.accounting.roles.models.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "authorities_authority",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "organization_id"})}
)
public class Authority {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organization organization;

    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles = new HashSet<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Authority() {}

    public Authority(String name, Organization organization) {
        this.name = name;
        this.organization = organization;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Authority authority)) return false;

        return name.equals(authority.name) && organization.equals(authority.organization);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + organization.hashCode();
        return result;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
