package ba.asco.prijava.dto;

import ba.asco.prijava.model.Status;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(@NotNull Status status) {
}
