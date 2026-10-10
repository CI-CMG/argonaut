package edu.colorado.cires.argonaut.messaging.cameljpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "argonaut_message", indexes = @Index(name = "argonaut_message_queue_idx", columnList = "queue"))
@NamedQuery(name = "dequeue", query = "select x from ArgonautMessageEntity x where x.queue = :queue order by x.queueTime")
public class ArgonautMessageEntity {

  @Id
  @Column(name = "id", length = 36, nullable = false)
  private String id;

  @Column(name = "queue_time", nullable = false)
  private ZonedDateTime queueTime;

  @Column(name = "queue", nullable = false, length = 50)
  private String queue;

  @Lob
  @Column(name = "json_message", nullable = false)
  private String jsonMessage;

  public UUID getId() {
    return id == null ? null : UUID.fromString(id);
  }

  public void setId(UUID id) {
    this.id = id == null ? null : id.toString();
  }

  public String getQueue() {
    return queue;
  }

  public void setQueue(String queue) {
    this.queue = queue;
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

  @Override
  public String toString() {
    return "ArgonautMessageEntity{" +
        "id='" + id + '\'' +
        ", queueTime=" + queueTime +
        ", queue='" + queue + '\'' +
        ", jsonMessage='" + jsonMessage + '\'' +
        '}';
  }
}
