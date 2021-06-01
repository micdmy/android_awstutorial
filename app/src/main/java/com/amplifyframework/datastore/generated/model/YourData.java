package com.amplifyframework.datastore.generated.model;


import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the YourData type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "YourData", authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class YourData implements Model {
  public static final QueryField ID = field("YourData", "id");
  public static final QueryField EXPERIENCE = field("YourData", "experience");
  public static final QueryField OWNER = field("YourData", "owner");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="Int", isRequired = true) Integer experience;
  private final @ModelField(targetType="String") String owner;
  public String getId() {
      return id;
  }
  
  public Integer getExperience() {
      return experience;
  }
  
  public String getOwner() {
      return owner;
  }
  
  private YourData(String id, Integer experience, String owner) {
    this.id = id;
    this.experience = experience;
    this.owner = owner;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      YourData yourData = (YourData) obj;
      return ObjectsCompat.equals(getId(), yourData.getId()) &&
              ObjectsCompat.equals(getExperience(), yourData.getExperience()) &&
              ObjectsCompat.equals(getOwner(), yourData.getOwner());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getExperience())
      .append(getOwner())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("YourData {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("experience=" + String.valueOf(getExperience()) + ", ")
      .append("owner=" + String.valueOf(getOwner()))
      .append("}")
      .toString();
  }
  
  public static ExperienceStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static YourData justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new YourData(
      id,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      experience,
      owner);
  }
  public interface ExperienceStep {
    BuildStep experience(Integer experience);
  }
  

  public interface BuildStep {
    YourData build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep owner(String owner);
  }
  

  public static class Builder implements ExperienceStep, BuildStep {
    private String id;
    private Integer experience;
    private String owner;
    @Override
     public YourData build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new YourData(
          id,
          experience,
          owner);
    }
    
    @Override
     public BuildStep experience(Integer experience) {
        Objects.requireNonNull(experience);
        this.experience = experience;
        return this;
    }
    
    @Override
     public BuildStep owner(String owner) {
        this.owner = owner;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, Integer experience, String owner) {
      super.id(id);
      super.experience(experience)
        .owner(owner);
    }
    
    @Override
     public CopyOfBuilder experience(Integer experience) {
      return (CopyOfBuilder) super.experience(experience);
    }
    
    @Override
     public CopyOfBuilder owner(String owner) {
      return (CopyOfBuilder) super.owner(owner);
    }
  }
  
}
