package edu.colorado.cires.argonaut.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "ocean")
public class OceanEntity {

  @Id
  @Column(name = "name", unique = true, nullable = false, length = 40)
  private String name;

  @Column(name = "shape", nullable = false)
  private Geometry shape;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Geometry getShape() {
    return shape;
  }

  public void setShape(Geometry shape) {
    this.shape = shape;
  }
}
