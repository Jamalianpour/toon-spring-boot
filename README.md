# Spring Boot TOON Converter

![Language](https://img.shields.io/badge/language-Java-blue)
[![codecov](https://codecov.io/gh/jamalianpour/toon-spring-boot/branch/main/graph/badge.svg)](https://codecov.io/gh/jamalianpour/persian-utils)
![License](https://img.shields.io/github/license/jamalianpour/toon-spring-boot)

A Spring Boot library for converting Java objects to TOON (Token-Oriented Object Notation) format. TOON is optimized for Large Language Model (LLM) interactions, reducing token consumption by 30-60% compared to JSON.

## What is TOON?

Token-Oriented Object Notation is a compact, human-readable encoding of the JSON data model that minimizes tokens and makes structure easy for models to follow. It's intended for LLM input as a drop-in, lossless representation of your existing JSON.

TOON combines YAML's indentation-based structure for nested objects with a CSV-style tabular layout for uniform arrays. TOON's sweet spot is uniform arrays of objects (multiple fields per row, same structure across items), achieving CSV-like compactness while adding explicit structure that helps LLMs parse and validate data reliably. For deeply nested or non-uniform data, JSON may be more efficient.

The similarity to CSV is intentional: CSV is simple and ubiquitous, and TOON aims to keep that familiarity while remaining a lossless, drop-in representation of JSON for Large Language Models.

Think of it as a translation layer: use JSON programmatically, and encode it as TOON for LLM input.

## Features

- 🚀 **30-60% Token Reduction**: Significantly reduce LLM API costs
- 📝 **Annotation-Based**: Simple annotations for controlling serialization
- 🔧 **Spring Boot Integration**: Auto-configuration and Spring components
- 📊 **Smart Array Handling**: Automatic tabular format for uniform objects
- 🎯 **Type-Safe**: Full support for Java type system
- ⚙️ **Highly Configurable**: Extensive configuration options
- 🧪 **Well Tested**: Comprehensive test coverage

## Installation

Add the dependency to your Maven `pom.xml`:

```xml
<dependency>
    <groupId>io.github.jamalianpour</groupId>
    <artifactId>toon-spring-boot</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Quick Start

### 1. Enable TOON Converter in Your Application

```java
@SpringBootApplication
@EnableToonConverter
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

### 2. Annotate Your Models

```java
@ToonSerializable
public class User {
    @ToonField(order = 1)
    private int id;
    
    @ToonField(order = 2)
    private String name;
    
    @ToonField(order = 3)
    private String email;
    
    @ToonIgnore
    private String password; // Won't be included in TOON output
    
    // getters and setters
}
```

### 3. Convert to TOON

```java
@RestController
public class MyController {
    
    @Autowired
    private ToonConverterService toonService;
    
    @GetMapping("/users")
    public String getUsers() {
        List<User> users = Arrays.asList(
            new User(1, "Alice", "alice@example.com"),
            new User(2, "Bob", "bob@example.com")
        );
        
        return toonService.convert(users);
    }
}
```

**Output:**
```toon
[2]{id,name,email}:
  1,Alice,alice@example.com
  2,Bob,bob@example.com
```

## Annotations

### @ToonSerializable
Marks a class as TOON serializable.

```java
@ToonSerializable(
    value = "users",        // Custom root name (optional)
    includeNulls = false,   // Include null values
    compactArrays = true    // Use compact format for arrays
)
public class User { ... }
```

### @ToonField
Customizes field serialization.

```java
@ToonField(
    value = "user_id",      // Custom field name
    order = 1,              // Field order in tabular arrays
    include = true,         // Include in output
    format = "yyyy-MM-dd"   // Format pattern for dates
)
private Date createdAt;
```

### @ToonArray
Configures array serialization.

```java
@ToonArray(
    inline = true,          // Use inline format
    delimiter = ",",        // Element delimiter
    tabular = true          // Use tabular for objects
)
private List<String> tags;
```

### @ToonIgnore
Excludes a field from serialization.

```java
@ToonIgnore
private String internalId;
```

## Configuration

Configure via `application.yml`:

```yaml
toon:
  converter:
    indent: "  "                              # Indentation for nested objects
    include-nulls: false                      # Include null values
    compact-arrays: true                      # Use compact array format
    array-delimiter: ","                      # Array element delimiter
    tabular-arrays: true                      # Use tabular for uniform arrays
    max-depth: 100                           # Maximum nesting depth
    default-date-format: "yyyy-MM-dd"        # Default date format
```

## Usage Examples

### Simple Object Conversion

```java
// Using the service
@Autowired
private ToonConverterService toonService;

Product product = new Product("Laptop", 999.99);
String toon = toonService.convert(product);
```

### Static Utility Method

```java
// Quick conversion without Spring
String toon = ToonUtils.toToon(product);
```

### Custom Configuration

```java
ToonConverter converter = ToonUtils.builder()
    .withIndent("    ")
    .includeNulls()
    .withDateFormat("dd/MM/yyyy")
    .build();

String toon = converter.toToon(data);
```

### File Output

```java
// Write to file
toonService.convertToFile(data, new File("output.toon"));

// Write to Path
toonService.convertToPath(data, Paths.get("output.toon"));

// Write to stream
try (OutputStream os = new FileOutputStream("output.toon")) {
    toonService.convertToStream(data, os);
}
```

### Token Reduction Estimation

```java
double reduction = toonService.estimateTokenReduction(data);
System.out.println("Token reduction: " + reduction + "%");
```

## TOON Format Examples

### Nested Objects
```toon
user:
  name: Alice
  age: 30
  address:
    street: 123 Main St
    city: Boston
```

### Inline Arrays (Primitives)
```toon
tags[3]: java,spring,toon
numbers[5]: 1,2,3,4,5
```

### Tabular Arrays (Uniform Objects)
```toon
employees[3]{id,name,department,salary}:
  101,Alice,Engineering,75000
  102,Bob,Marketing,65000
  103,Charlie,Sales,70000
```

### Mixed Arrays
```toon
items[3]:
  - type: book
    title: Java Guide
  - type: video
    duration: 120
  - simple string
```

## Best Practices

1. **Use @ToonSerializable**: Always annotate your models for better control
2. **Order Fields**: Use `@ToonField(order=n)` for consistent tabular output
3. **Exclude Sensitive Data**: Use `@ToonIgnore` for passwords, tokens, etc.
4. **Uniform Collections**: Keep collections uniform for maximum token savings
5. **Configuration**: Tune settings based on your specific use case
6. **Important Note**: TOON is not good for large nested objects

## Performance Comparison

| Format | Tokens | Characters | Reduction |
|--------|--------|------------|-----------|
| JSON   | 89     | 177        | -         |
| TOON   | 45     | 85         | ~50%      |

## Advanced Features

### Custom Field Formatting
```java
@ToonField(format = "#,##0.00")
private BigDecimal amount;

@ToonField(format = "yyyy-MM-dd")
private Date birthDate;
```

### Batch Conversion
```java
Map<Object, String> results = toonService.batchConvert(objects);
```

### Validation
```java
if (toonService.canConvert(object)) {
    String toon = toonService.convert(object);
}
```

## Integration with LLM APIs

```java
// Example with OpenAI
String prompt = "Analyze this data:\n" + toonService.convert(data);
// Send to LLM API with significantly reduced tokens
```

## License

This project is licensed under the Apache License - see the [LICENSE](https://github.com/Jamalianpour/toon-spring-boot/blob/master/LICENSE) file for details.

## Author

**Mohammad Jamalianpour**
- Email: jamalian.mjp@gmail.com
- GitHub: [jamalianpour](https://github.com/jamalianpour)

## Support

If you find this library helpful, please give it a ⭐️ on GitHub!

For bug reports and feature requests, please use the [GitHub Issues](https://github.com/jamalianpour/toon-spring-boot/issues) page.