package edu.colorado.cires.argonaut.metadata.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "float")
public class FloatEntity {

  @Id
  @Column(name = "id", nullable = false, length = 20)
  private String id;

  @Column(name = "float_id", nullable = false, length = 10)
  private String floatId;

  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  @JoinColumn(name = "dac", nullable = false)
  private DacEntity dac;

  @OneToMany(mappedBy = "floatId", cascade = {CascadeType.DETACH, CascadeType.REFRESH})
  private List<CycleEntity> cycles = new ArrayList<>();

  @OneToOne(mappedBy = "floatId")
  private MetadataFileEntity metadata;

  @OneToOne(mappedBy = "floatId")
  private ProfileMergeFileEntity profileMerge;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getFloatId() {
    return floatId;
  }

  public void setFloatId(String floatId) {
    this.floatId = floatId;
  }

  public DacEntity getDac() {
    return dac;
  }

  public void setDac(DacEntity dac) {
    this.dac = dac;
  }

  public MetadataFileEntity getMetadata() {
    return metadata;
  }

  public void setMetadata(MetadataFileEntity metadata) {
    this.metadata = metadata;
  }

  public List<CycleEntity> getCycles() {
    return cycles;
  }

  public ProfileMergeFileEntity getProfileMerge() {
    return profileMerge;
  }

  public void setProfileMerge(ProfileMergeFileEntity profileMerge) {
    this.profileMerge = profileMerge;
  }
}
