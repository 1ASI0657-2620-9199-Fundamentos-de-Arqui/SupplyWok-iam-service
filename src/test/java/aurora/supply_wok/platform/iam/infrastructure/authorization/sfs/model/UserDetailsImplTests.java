package aurora.supply_wok.platform.iam.infrastructure.authorization.sfs.model;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTests {

    @Test
    void build_whenUserProvided_constructsUserDetailsWithAuthorities() {
        // Arrange
        var user = new User("chef@wok.pe", "pwd", Roles.RESTAURANT);
        user.setId(7L);

        // Act
        var details = UserDetailsImpl.build(user);

        // Assert
        assertThat(details.getId()).isEqualTo(7L);
        assertThat(details.getEmail()).isEqualTo("chef@wok.pe");
        assertThat(details.getUsername()).isEqualTo("chef@wok.pe");
        assertThat(details.getPassword()).isEqualTo("pwd");
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_RESTAURANT");
    }

    @Test
    void equalsAndHashCode_whenIdenticalInstances_areEqual() {
        // Arrange
        var auth = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        var user1 = new UserDetailsImpl(1L, "a@b.com", "pass", auth);
        var user2 = new UserDetailsImpl(1L, "a@b.com", "pass", auth);

        // Act & Assert
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}
