package dev.mednikov.accounting.journals.models;

import dev.mednikov.accounting.organizations.models.Organization;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
name = "journals_journal",
uniqueConstraints = {@UniqueConstraint(columnNames = {"organization_id", "name"})}
)
public class Journal {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organization organization;

    @Column(nullable = false)
    private String name;
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Journal journal)) return false;

        return active == journal.active
                && organization.equals(journal.organization)
                && name.equals(journal.name);
    }

    @Override
    public int hashCode() {
        int result = organization.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + Boolean.hashCode(active);
        return result;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
