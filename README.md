# About avikohn-util
This repository contains some useful utility methods and classes
for both testing and production code.

# How to use
To use a single class, you can just copy that class but without the
package declaration.

To include from the entire package, add this to your pom.xml (the ellipses represent other 
dependencies or repositories and serve to show where to put the data, only copy the repository and dependency tag):

```
<repositories>
    ...
    <repository>
      <id>avikohn-util-mvn-repo</id>
      <url>https://github.com/AviKohn7/avikohn-util/raw/mvn-repo/</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
      </snapshots>
    </repository>
    ...
</repositories>
...

<dependencies>
  ...
  <dependency>
    <groupId>dev.avikohn.util</groupId>
    <artifactId>avikohn-util</artifactId>
    <version>1.1-alpha</version> <!-- or most recent version -->
    <scope>test</scope>
  </dependency>
  ...
</dependencies>  
```
