package ba.asco.prava.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Podaci koje frontend treba da zna šta da prikaže. */
@RestController
public class KorisnikController {

    public record Ja(String korisnik, List<String> prava) {}

    /** Ko sam ja i koje značke imam? Frontend po ovome sakriva dugmad. */
    @GetMapping("/api/ja")
    public Ja ja(Authentication a) {
        List<String> prava = a.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();
        return new Ja(a.getName(), prava);
    }

    /** CSRF token koji frontend šalje uz svaki POST. */
    @GetMapping("/api/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
