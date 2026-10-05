package ba.asco.prijava;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PrijavaApiTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    @Test
    void kreirajDodajSlikuPromijeniStatusIObrisi() throws Exception {
        String body = """
                {"proizvod":"SD compact – usipni koš","vrstaGreske":"EKSTERNA","dobavljac":"Metal d.o.o.",
                 "opis":"Rupe pomjerene 3 mm","uzroci":["Dobavljač","Mjerenje"],"ponavljajuca":true,
                 "uoceno":"MJERENJE","izmjereneVrijednosti":"traženo 10, izmjereno 13","prijavio":"Siniša"}
                """;
        String odgovor = mvc.perform(post("/api/prijave").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.status").value("NOVA"))
                .andExpect(jsonPath("$.kolicinaSerija").value(1))
                .andExpect(jsonPath("$.uzroci.length()").value(2))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(odgovor).get("id").asLong();

        byte[] jpeg = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 1, 2, 3};
        String saSlikom = mvc.perform(multipart("/api/prijave/{id}/slike", id)
                        .file(new MockMultipartFile("slike", "a.jpg", "image/jpeg", jpeg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slike.length()").value(1))
                .andReturn().getResponse().getContentAsString();
        String slikaUrl = json.readTree(saSlikom).get("slike").get(0).asText();

        mvc.perform(get(slikaUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(jpeg));

        mvc.perform(patch("/api/prijave/{id}/status", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"U_OBRADI\"}"))
                .andExpect(jsonPath("$.status").value("U_OBRADI"));

        mvc.perform(get("/api/prijave").param("status", "U_OBRADI"))
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());

        mvc.perform(delete("/api/prijave/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/prijave/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void obaveznaPoljaVracaju400() throws Exception {
        String odgovor = mvc.perform(post("/api/prijave").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertThat(json.readTree(odgovor).get("detail").asText())
                .contains("Proizvod je obavezan").contains("Opis je obavezan");
    }

    @Test
    void uploadNeSlikeVraca400() throws Exception {
        String odgovor = mvc.perform(post("/api/prijave").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"proizvod\":\"X\",\"vrstaGreske\":\"INTERNA\",\"opis\":\"Y\"}"))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(odgovor).get("id").asLong();
        mvc.perform(multipart("/api/prijave/{id}/slike", id)
                        .file(new MockMultipartFile("slike", "a.txt", "text/plain", "x".getBytes())))
                .andExpect(status().isBadRequest());
    }
}
