package edu.colorado.cires.argonaut.processor.report.jpa;


import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage.Operation;
import edu.colorado.cires.argonaut.processor.report.jpa.entity.SubmissionRecordEntity;
import edu.colorado.cires.argonaut.processor.report.jpa.entity.SubmissionRecordValidationErrorEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JpaFileSubmissionReportProcessorTest {

  private EntityManagerFactory emf;
  //  private EntityManager em;
  private JpaFileSubmissionReportProcessor processor;

  @BeforeEach
  public void setup() throws Exception {
    Map<String, String> override = new HashMap<>();
    override.put("jakarta.persistence.jdbc.driver", "org.h2.Driver");
    override.put("jakarta.persistence.jdbc.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    override.put("jakarta.persistence.jdbc.user", "sa");
    override.put("jakarta.persistence.jdbc.password", "");
    override.put("hibernate.hbm2ddl.auto", "update");
    override.put("hibernate.show_sql", "true");
    emf = Persistence.createEntityManagerFactory("argonaut-report", override);
//    em = emf.createEntityManager();
    processor = new JpaFileSubmissionReportProcessor();
    processor.setEntityManagerFactory(emf);
  }


  @Test
  public void test() throws Exception {
    processor.appendReport(NcSubmissionMessage.builder()
        .withFileName("R1902264_173.nc")
        .withFloatId("1")
        .withDac("aoml")
        .withProfile(false)
        .withOperation(Operation.ADD)
        .withNumberOfFilesInSubmission(100)
        .withValidationErrors(Arrays.asList("error 1", "error 2"))
        .withTimestamp(Instant.parse("2025-04-03T12:34:56.000Z"))
        .build());
    processor.appendReport(NcSubmissionMessage.builder()
        .withFileName("R4903218_229.nc")
        .withFloatId("2")
        .withDac("foo")
        .withProfile(true)
        .withOperation(Operation.REMOVE)
        .withNumberOfFilesInSubmission(10)
        .withTimestamp(Instant.parse("2025-04-03T12:34:57.000Z"))
        .build());
    processor.appendReport(NcSubmissionMessage.builder()
        .withFileName("R4903353_302.nc")
        .withFloatId("3")
        .withDac("bar")
        .withProfile(false)
        .withOperation(Operation.FLOAT_MERGE)
        .withNumberOfFilesInSubmission(11)
        .withTimestamp(Instant.parse("2025-04-03T12:34:58.000Z"))
        .build());
    try (EntityManager em = emf.createEntityManager()) {
      List<SubmissionRecordEntity> records = em.createQuery("select r from SubmissionRecordEntity r order by r.floatId asc").getResultList();
      List<SubmissionRecordEntity> expectedRecords = new ArrayList<>(3);
      SubmissionRecordEntity entity = new SubmissionRecordEntity();
      entity.setId(records.get(0).getId());
      entity.setTimestamp(Instant.parse("2025-04-03T12:34:56.000Z").atOffset(ZoneOffset.UTC).toZonedDateTime());
      entity.setDac("aoml");
      entity.setFloatId("1");
      entity.setNumberOfFilesInSubmission(100);
      entity.setFileName("R1902264_173.nc");
      entity.setOperation("ADD");
      List<SubmissionRecordValidationErrorEntity> errors = new ArrayList<>(2);
      SubmissionRecordValidationErrorEntity errorEntity = new SubmissionRecordValidationErrorEntity();
      errorEntity.setSubmissionRecord(entity);
      errorEntity.setId(records.get(0).getValidationErrors().get(0).getId());
      errorEntity.setMessage("error 1");
      errors.add(errorEntity);
      errorEntity = new SubmissionRecordValidationErrorEntity();
      errorEntity.setSubmissionRecord(entity);
      errorEntity.setId(records.get(0).getValidationErrors().get(1).getId());
      errorEntity.setMessage("error 2");
      errors.add(errorEntity);
      entity.setValidationErrors(errors);
      entity.setSuccess(false);
      expectedRecords.add(entity);

      entity = new SubmissionRecordEntity();
      entity.setId(records.get(1).getId());
      entity.setTimestamp(Instant.parse("2025-04-03T12:34:57.000Z").atOffset(ZoneOffset.UTC).toZonedDateTime());
      entity.setDac("foo");
      entity.setFloatId("2");
      entity.setNumberOfFilesInSubmission(10);
      entity.setFileName("R4903218_229.nc");
      entity.setOperation("REMOVE");
      entity.setSuccess(true);
      expectedRecords.add(entity);

      entity = new SubmissionRecordEntity();
      entity.setId(records.get(2).getId());
      entity.setTimestamp(Instant.parse("2025-04-03T12:34:58.000Z").atOffset(ZoneOffset.UTC).toZonedDateTime());
      entity.setDac("bar");
      entity.setFloatId("3");
      entity.setNumberOfFilesInSubmission(11);
      entity.setFileName("R4903353_302.nc");
      entity.setOperation("FLOAT_MERGE");
      entity.setSuccess(true);
      expectedRecords.add(entity);

      assertEquals(expectedRecords, records);
    }

  }
}