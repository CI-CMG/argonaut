package edu.colorado.cires.argonaut.messaging.cameljpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "json_message_queue")
public class JsonMessageEntity {

  @Id
  private UUID id;

  @Column(name = "queue_time", nullable = false)
  private ZonedDateTime queueTime;

  @Lob
  @Column(name = "json_message", nullable = false)
  private String jsonMessage;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ZonedDateTime getQueueTime() {
    return queueTime;
  }

  public void setQueueTime(ZonedDateTime queueTime) {
    this.queueTime = queueTime;
  }

  public String getJsonMessage() {
    return jsonMessage;
  }

  public void setJsonMessage(String jsonMessage) {
    this.jsonMessage = jsonMessage;
  }
}
