package ba.asco.prava.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Ovdje "živi" matrica prava.
 *
 * Svaki korisnik dobija značke (authorities) dvije vrste:
 *   F_...  funkcije koje smije koristiti
 *   D_...  vrste podataka koje smije dirati
 *
 *              | Pregled | Unos | Izvoz | Prijave | Narudžbe | Ponude
 *   sef        |    ✓    |      |       |    ✓    |    ✓     |   ✓
 *   nabavka    |    ✓    |  ✓   |       |         |    ✓     |   ✓
 *   revizor    |         |      |   ✓   |    ✓    |    ✓     |   ✓
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // uključuje @PreAuthorize na metodama kontrolera
public class SecurityConfig {

    @Bean
    UserDetailsService korisnici(PasswordEncoder enc) {
        // Korisnici su u memoriji da bi primjer bio kratak.
        // U pravoj aplikaciji bi bili u bazi (vidi 7. predavanje).
        return new InMemoryUserDetailsManager(
                User.withUsername("sef")
                        .password(enc.encode("sef123"))
                        .authorities("F_PREGLED",
                                "D_PRIJAVE", "D_NARUDZBE", "D_PONUDE")
                        .build(),

                User.withUsername("nabavka")
                        .password(enc.encode("nab123"))
                        .authorities("F_PREGLED", "F_UNOS",
                                "D_NARUDZBE", "D_PONUDE")
                        .build(),

                User.withUsername("revizor")
                        .password(enc.encode("rev123"))
                        .authorities("F_IZVOZ",
                                "D_PRIJAVE", "D_NARUDZBE", "D_PONUDE")
                        .build()
        );
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Sve traži prijavu. Ko smije ŠTA, provjerava @PreAuthorize u kontroleru.
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                // Login stranica za browser (Spring je sam napravi)
                .formLogin(Customizer.withDefaults())
                // Basic auth samo da bi radili curl primjeri iz README-a
                .httpBasic(Customizer.withDefaults())
                .logout(Customizer.withDefaults());
        // CSRF zaštita ostaje UKLJUČENA: frontend šalje token uz svaki POST.
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
