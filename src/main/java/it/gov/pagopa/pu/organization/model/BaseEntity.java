package it.gov.pagopa.pu.organization.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {
  @Column(updatable = false)
  @CreatedDate
  @JsonSerialize(using = LocalDateTimeToOffsetDateTimeSerializer.class)
  private LocalDateTime creationDate;
  @LastModifiedDate
  @JsonSerialize(using = LocalDateTimeToOffsetDateTimeSerializer.class)
  private LocalDateTime updateDate;
  @LastModifiedBy
  private String updateOperatorExternalId;

  public static class LocalDateTimeToOffsetDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      if (value != null) {
        OffsetDateTime offsetDateTime = value.atOffset(ZoneOffset.UTC);
        gen.writeString(offsetDateTime.toString());
      }
    }
  }
}


