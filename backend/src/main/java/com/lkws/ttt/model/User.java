package com.lkws.ttt.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
public class User implements UserDetails {

    // TODO: Use config value (e.g. key file)
    private static final double CASH_STARTING_VALUE = 50_000.0;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank
    @Column(name = "username")
    private String username;

    @NotBlank
    @Column(name = "password")
    private String password;

    // TODO: Don't use floating points for cash (rounding errors), use long/bigint and set . accordingly
    @Column(name = "cash")
    private double cash;

    /**
     * Users are currently assigned one authority. Since there are no different kinds of users yet, all users have the
     * same authority.
     * If instead of a hierarchy approach, a permission approach is to be used, a set of authorities should be used.
     */
    @Column(name = "authorities")
    private Authority authorities = Authority.USER_AUTHORITY;

    @Column(name = "enabled")
    private boolean enabled = true;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.cash = CASH_STARTING_VALUE;
    }

    public User(String username, String password, double cash) {
        this.username = username;
        this.password = password;
        this.cash = cash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(authorities);
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    // Equals tests for username and password equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
