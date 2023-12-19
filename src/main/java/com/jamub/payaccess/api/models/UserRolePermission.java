package com.jamub.payaccess.api.models;

import com.jamub.payaccess.api.enums.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_role_permissions")
public class UserRolePermission implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(nullable= false)
    @Enumerated(EnumType.STRING)
    private Permission permission;

    @Column(nullable = false)
    LocalDateTime createdAt;
    @Column(nullable = true)
    LocalDateTime deletedAt;
    @Column(nullable = false)
    LocalDateTime updatedAt;
}
