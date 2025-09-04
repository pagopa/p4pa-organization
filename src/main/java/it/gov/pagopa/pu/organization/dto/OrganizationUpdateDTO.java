package it.gov.pagopa.pu.organization.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationCreateDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class OrganizationUpdateDTO extends OrganizationCreateDTO {
    @NotNull
    private Long organizationId;
    @NotNull
    private boolean flagTreasury;
    @Setter
    @NotNull
    private String iban;
    @Setter
    @NotNull
    private String segregationCode;

    @Override
    @Schema(name = "iban", requiredMode = Schema.RequiredMode.REQUIRED)
    public String getIban() {
        return this.iban;
    }

    @Override
    @Schema(name = "segregationCode", requiredMode = Schema.RequiredMode.REQUIRED)
    public String getSegregationCode() {
        return this.segregationCode;
    }
}
