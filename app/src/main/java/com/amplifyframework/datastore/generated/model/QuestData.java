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

/** This is an auto generated class representing the QuestData type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "QuestData", authRules = {
  @AuthRule(allow = AuthStrategy.OWNER, ownerField = "owner", identityClaim = "cognito:username", operations = { ModelOperation.CREATE, ModelOperation.DELETE })
})
public final class QuestData implements Model {
  public static final QueryField ID = field("QuestData", "id");
  public static final QueryField NAME = field("QuestData", "name");
  public static final QueryField STATUS = field("QuestData", "status");
  public static final QueryField COORDINATES = field("QuestData", "coordinates");
  public static final QueryField STORY = field("QuestData", "story");
  public static final QueryField REMARKS = field("QuestData", "remarks");
  public static final QueryField HINT = field("QuestData", "hint");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="QuestStatus", isRequired = true) QuestStatus status;
  private final @ModelField(targetType="Coordinates", isRequired = true) Coordinates coordinates;
  private final @ModelField(targetType="String", isRequired = true) String story;
  private final @ModelField(targetType="String") String remarks;
  private final @ModelField(targetType="String") String hint;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public QuestStatus getStatus() {
      return status;
  }
  
  public Coordinates getCoordinates() {
      return coordinates;
  }
  
  public String getStory() {
      return story;
  }
  
  public String getRemarks() {
      return remarks;
  }
  
  public String getHint() {
      return hint;
  }
  
  private QuestData(String id, String name, QuestStatus status, Coordinates coordinates, String story, String remarks, String hint) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.coordinates = coordinates;
    this.story = story;
    this.remarks = remarks;
    this.hint = hint;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      QuestData questData = (QuestData) obj;
      return ObjectsCompat.equals(getId(), questData.getId()) &&
              ObjectsCompat.equals(getName(), questData.getName()) &&
              ObjectsCompat.equals(getStatus(), questData.getStatus()) &&
              ObjectsCompat.equals(getCoordinates(), questData.getCoordinates()) &&
              ObjectsCompat.equals(getStory(), questData.getStory()) &&
              ObjectsCompat.equals(getRemarks(), questData.getRemarks()) &&
              ObjectsCompat.equals(getHint(), questData.getHint());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getStatus())
      .append(getCoordinates())
      .append(getStory())
      .append(getRemarks())
      .append(getHint())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("QuestData {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("status=" + String.valueOf(getStatus()) + ", ")
      .append("coordinates=" + String.valueOf(getCoordinates()) + ", ")
      .append("story=" + String.valueOf(getStory()) + ", ")
      .append("remarks=" + String.valueOf(getRemarks()) + ", ")
      .append("hint=" + String.valueOf(getHint()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
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
  public static QuestData justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new QuestData(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      status,
      coordinates,
      story,
      remarks,
      hint);
  }
  public interface NameStep {
    StatusStep name(String name);
  }
  

  public interface StatusStep {
    CoordinatesStep status(QuestStatus status);
  }
  

  public interface CoordinatesStep {
    StoryStep coordinates(Coordinates coordinates);
  }
  

  public interface StoryStep {
    BuildStep story(String story);
  }
  

  public interface BuildStep {
    QuestData build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep remarks(String remarks);
    BuildStep hint(String hint);
  }
  

  public static class Builder implements NameStep, StatusStep, CoordinatesStep, StoryStep, BuildStep {
    private String id;
    private String name;
    private QuestStatus status;
    private Coordinates coordinates;
    private String story;
    private String remarks;
    private String hint;
    @Override
     public QuestData build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new QuestData(
          id,
          name,
          status,
          coordinates,
          story,
          remarks,
          hint);
    }
    
    @Override
     public StatusStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public CoordinatesStep status(QuestStatus status) {
        Objects.requireNonNull(status);
        this.status = status;
        return this;
    }
    
    @Override
     public StoryStep coordinates(Coordinates coordinates) {
        Objects.requireNonNull(coordinates);
        this.coordinates = coordinates;
        return this;
    }
    
    @Override
     public BuildStep story(String story) {
        Objects.requireNonNull(story);
        this.story = story;
        return this;
    }
    
    @Override
     public BuildStep remarks(String remarks) {
        this.remarks = remarks;
        return this;
    }
    
    @Override
     public BuildStep hint(String hint) {
        this.hint = hint;
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
    private CopyOfBuilder(String id, String name, QuestStatus status, Coordinates coordinates, String story, String remarks, String hint) {
      super.id(id);
      super.name(name)
        .status(status)
        .coordinates(coordinates)
        .story(story)
        .remarks(remarks)
        .hint(hint);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder status(QuestStatus status) {
      return (CopyOfBuilder) super.status(status);
    }
    
    @Override
     public CopyOfBuilder coordinates(Coordinates coordinates) {
      return (CopyOfBuilder) super.coordinates(coordinates);
    }
    
    @Override
     public CopyOfBuilder story(String story) {
      return (CopyOfBuilder) super.story(story);
    }
    
    @Override
     public CopyOfBuilder remarks(String remarks) {
      return (CopyOfBuilder) super.remarks(remarks);
    }
    
    @Override
     public CopyOfBuilder hint(String hint) {
      return (CopyOfBuilder) super.hint(hint);
    }
  }
  
}
