package it.gov.pagopa.pu.organization;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
@TestPropertySource(properties = {
  "spring.datasource.driver-class-name=org.h2.Driver",
  "spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1",
  "spring.datasource.username=sa",
  "spring.datasource.password=sa",

  "logging.level.org.springdoc.core.utils.SpringDocAnnotationsUtils=OFF"
})
class OpenApiGeneratorTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void generateAndVerifyCommit() throws Exception {
    MvcResult result = mockMvc.perform(
      get("/v3/api-docs")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk())
      .andReturn();

    String openApiResult = result.getResponse().getContentAsString();
    Assertions.assertTrue(openApiResult.startsWith("{\n  \"openapi\" : \"3.0."));

    Path openApiGeneratedPath = Path.of("openapi/generated.openapi.json");
    boolean toStore=true;
    if(Files.exists(openApiGeneratedPath)){
      String storedOpenApi = Files.readString(openApiGeneratedPath);
      try {
        content().json(storedOpenApi, JsonCompareMode.STRICT).match(result);
        toStore=false;
      } catch (Throwable e){
        //Do Nothing
      }
    }
    if(toStore){
      Files.writeString(openApiGeneratedPath, openApiResult, StandardOpenOption.TRUNCATE_EXISTING);
    }

    String gitStatus = execCmd("git", "status");
    Assertions.assertFalse(gitStatus.contains("openapi/generated.openapi.json"), "Generated OpenApi not committed");
  }

  public static String execCmd(String... cmd) throws java.io.IOException {
    java.util.Scanner s = new java.util.Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}
