package com.amplifyframework.datastore.generated.model;


import androidx.core.util.ObjectsCompat;

import java.util.Objects;
import java.util.List;

/** This is an auto generated class representing the Coordinates type in your schema. */
public final class Coordinates {
  private final Double latitude;
  private final Double longitude;
  public Double getLatitude() {
      return latitude;
  }
  
  public Double getLongitude() {
      return longitude;
  }
  
  private Coordinates(Double latitude, Double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Coordinates coordinates = (Coordinates) obj;
      return ObjectsCompat.equals(getLatitude(), coordinates.getLatitude()) &&
              ObjectsCompat.equals(getLongitude(), coordinates.getLongitude());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getLatitude())
      .append(getLongitude())
      .toString()
      .hashCode();
  }
  
  public static LatitudeStep builder() {
      return new Builder();
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(latitude,
      longitude);
  }
  public interface LatitudeStep {
    LongitudeStep latitude(Double latitude);
  }
  

  public interface LongitudeStep {
    BuildStep longitude(Double longitude);
  }
  

  public interface BuildStep {
    Coordinates build();
  }
  

  public static class Builder implements LatitudeStep, LongitudeStep, BuildStep {
    private Double latitude;
    private Double longitude;
    @Override
     public Coordinates build() {
        
        return new Coordinates(
          latitude,
          longitude);
    }
    
    @Override
     public LongitudeStep latitude(Double latitude) {
        Objects.requireNonNull(latitude);
        this.latitude = latitude;
        return this;
    }
    
    @Override
     public BuildStep longitude(Double longitude) {
        Objects.requireNonNull(longitude);
        this.longitude = longitude;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(Double latitude, Double longitude) {
      super.latitude(latitude)
        .longitude(longitude);
    }
    
    @Override
     public CopyOfBuilder latitude(Double latitude) {
      return (CopyOfBuilder) super.latitude(latitude);
    }
    
    @Override
     public CopyOfBuilder longitude(Double longitude) {
      return (CopyOfBuilder) super.longitude(longitude);
    }
  }
  
}
