package edu.colorado.cires.argonaut.processor.core;

import edu.colorado.cires.argonaut.messaging.core.databind.NcSubmissionMessage;
import org.junit.jupiter.api.Test;

public class DefaultFloatMergeProcessorTest {
//  static {
//    System.setProperty("camel.threads.virtual.enabled", "true");
//  }
//
//  @EndpointInject("mock:" + QueueConsts.UPDATE_INDEX)
//  private MockEndpoint updateIndexAgg;
//
//  @Autowired
//  private ProducerTemplate producerTemplate;
//
//  @Autowired
//  private ServiceProperties serviceProperties;
//
//  @Autowired
//  private ObjectMapper objectMapper;

  @Test
  public void testFloatMergeAggregator() throws Exception {
//    String[] files = new String[]{"badfile_00.nc", "D2901615_001.nc", "D2901615_002.nc", "D2901615_003.nc", "D2901615_004.nc"};
//    String timestamp = Instant.now().toString();
//    String floatId = "2901616";
//    String dac = "aoml";
//
//    NcSubmissionMessage message = NcSubmissionMessage.builder()
//        .withFloatId(floatId)
//        .withDac(dac)
//        .withTimestamp(timestamp)
//        .withProfile(true)
//        .build();
//
//    Path outputProfileDir = ArgonautFileUtils.getOutputProfileDir(serviceProperties, dac, floatId, true);
//    FileTestUtils.emptyDirectory(outputProfileDir);
//
//    Path resourceDir = Paths.get("src/test/resources/float_merge/nmdis/2901615/profiles");
//
//    for (String fileName : files) {
//      Path testFile = resourceDir.resolve(fileName);
//      Path outPutFile = outputProfileDir.resolve(fileName);
//      ArgonautFileUtils.copy(testFile, outPutFile);
//    }
//    updateIndexAgg.expectedMessageCount(1);
//
//    producerTemplate.sendBody(QueueConsts.FLOAT_MERGE, objectMapper.writeValueAsString(message));
//
//    updateIndexAgg.assertIsSatisfied();
//
//    NcSubmissionMessage actual = updateIndexAgg.getExchanges().stream()
//        .map(e -> e.getIn().getBody(String.class))
//        .map(body -> {
//          try {
//            return objectMapper.readValue(body, NcSubmissionMessage.class);
//          } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//          }
//        })
//        .findFirst().orElse(null);
//    assertEquals(message, actual);

  }
}