# Proto4j-Objection

![Module](https://img.shields.io:/static/v1?label=Module&message=objection&color=9cf)
![Build](https://img.shields.io:/static/v1?label=Build&message=passing&color=green)

This repository contains the source code for the `Objection` module from `Proto4j`. It is considered to be a development repository where changes can be made and features can be requested. The source code is heavily documented as there will be no external documentation. A simple usage of this module is presented below.


### Basic Usage

---

There are two possibilities on how to write code that will be serializable by this module:

1. Use standard `Java` directives 
2. Use pre-defined `Annotations` on declared classes and fields.
further 
This small overview tries to show both versions as detailed as possible. Nested serializable types will be automatically detected. Yet, there are some standard types defined to be serialized and de-serialized - they can be found in the following table:

| Class      | Serializable | Serializer-Class                               |
|------------|--------------|------------------------------------------------|
| String     | `true`       | `StringSerializer`                             |
| Number     | `true`       | located as inner class in `NumberSerializer`   |
| Collection | `true`       | located as inner class in `SequenceSerializer` |
| Map        | `true`       | located as inner class in `SequenceSerializer` |
| Array      | `true`       | located as inner class in `SequenceSerializer` |
| MultiArray | `false`      | ---                                            |

If you use a class that is not included in that table, you can write your own `ObjectSerializer` implementation and integrate that into the serialization process.

#### UseCase 1: Basic class declaration rules

```java
// Traditional way with Java directives
class Car implements Serializable {
    // All fields excluding compiler-generated, static and transient 
    // fields are going to be serialized.
    private int    id;
    private String name;

    // Use the transient keyword to prevent a field from being serialized
    private transient String ignoredField;
}

// Annotated way: Use the @Version annotation on a class to set a global
// version. Here, the field address is ignored, because version 0 is used.
@Serialize
class Person {
    // You can use a version flag to exclude fields from newer versions
    // and make the code backwards compatible. No version points to the 
    // initial version, which is 0.
    private String name;
    private Car car;
    @Version(1) private String address;
    
}
```

#### UseCase 2: Serialization process

```java
public static void main(String[] args) {
    // At first, create a new Marshaller instance for the target type.
    Marshaller<Car> marshaller = Objection.createMarshaller();
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    
    // Secondly, create the instance that will be serialized
    Car car = new Car(1, "Mustang"); // constructor (int, String) has to be defined
    DataOutput = new DataOutputStream(output);
    
    // Thirdly, serialize the object
    OSharedConfiguration config = marshaller.marshall(car, output);
    
    // With the returned configuration the output can be de-serialized back
    // into a Car instance.
    DataInput input = new DataInputStream(new ByteArrayInputStream(output.toByteArray()));
    Car car2 = marshaller.unmarshall(input, config);
}
```

#### UseCase 3: Custom ObjectSerializer implementations

```java
// Example class that can read/write the previous defined Car.class which is used 
// in the Person class.
class CarSerializer extends BasicObjectSerializer {
    @Override
    public boolean accept(Class<?> type) {
        // return type == Car.class, or even better:
        return Car.class.isAssignableFrom(type);
    }

    @Override
    public void writeObject(DataOutput dataOutput, Object writableObject, OSerializationContext ctx)
            throws IOException {
        Car car = (Car) writableObject;
        dataOutput.writeInt(car.getId());
        dataOutput.writeInt(car.getName().length());
        dataOutput.writeBytes(car.getName());
    }

    @Override
    public Object getInstance(Class<?> type, DataInput dataInput, OSerializationContext ctx) 
            throws IOException {
        int id = dataInput.readInt();
        int name_length = dataInput.readInt();
        byte[] name = new byte[name_length];
        
        dataInput.readFully(name);
        return new Car(id, new String(name));
    }
}

```