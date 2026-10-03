package ba.asco.prava;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Svaka ćelija matrice = jedan test.
 * 3 korisnika × 3 funkcije × 3 vrste podataka = 27 testova.
 *
 * Očekivanja su ovdje napisana NEZAVISNO od SecurityConfig-a,
 * tako da test hvata grešku ako neko pogrešno promijeni prava.
 */
@SpringBootTest
@AutoConfigureMockMvc
class MatricaPravaTest {

    record Korisnik(String ime, String lozinka, Set<String> funkcije, Set<String> podaci) {}

    static final Map<String, Korisnik> MATRICA = Map.of(
            "sef", new Korisnik("sef", "sef123",
                    Set.of("PREGLED"),
                    Set.of("PRIJAVE", "NARUDZBE", "PONUDE")),
            "nabavka", new Korisnik("nabavka", "nab123",
                    Set.of("PREGLED", "UNOS"),
                    Set.of("NARUDZBE", "PONUDE")),
            "revizor", new Korisnik("revizor", "rev123",
                    Set.of("IZVOZ"),
                    Set.of("PRIJAVE", "NARUDZBE", "PONUDE"))
    );

    @Autowired
    MockMvc mvc;

    static Stream<Arguments> sveKombinacije() {
        return MATRICA.keySet().stream().sorted().flatMap(k ->
                Stream.of("PREGLED", "UNOS", "IZVOZ").flatMap(f ->
                        Stream.of("PRIJAVE", "NARUDZBE", "PONUDE").map(d ->
                                Arguments.of(k, f, d))));
    }

    @ParameterizedTest(name = "{0} → {1} nad {2}")
    @MethodSource("sveKombinacije")
    void matrica(String ime, String funkcija, String podatak) throws Exception {
        Korisnik k = MATRICA.get(ime);
        boolean smije = k.funkcije().contains(funkcija) && k.podaci().contains(podatak);

        MockHttpServletRequestBuilder zahtjev = switch (funkcija) {
            case "PREGLED" -> get("/api/podaci/" + podatak);
            case "UNOS" -> post("/api/podaci/" + podatak)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"opis\":\"test\"}");
            case "IZVOZ" -> get("/api/podaci/" + podatak + "/izvoz");
            default -> throw new IllegalArgumentException(funkcija);
        };

        mvc.perform(zahtjev.with(httpBasic(k.ime(), k.lozinka())))
                .andExpect(smije ? status().isOk() : status().isForbidden());
    }

    @ParameterizedTest(name = "bez prijave → {0}")
    @MethodSource("tipovi")
    void bezPrijaveNemaPristupa(String podatak) throws Exception {
        mvc.perform(get("/api/podaci/" + podatak))
                .andExpect(status().is4xxClientError());
    }

    static Stream<String> tipovi() {
        return Stream.of("PRIJAVE", "NARUDZBE", "PONUDE");
    }
}
