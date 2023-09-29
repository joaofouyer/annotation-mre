# annotation-mre
A minimal reproducible example of code generation in Java with Annotation and Gradle
In this project I am using Java annotations to generate Code (kinda like Lombok or Dagger2). 

The annotations are Positional and PositionalObject. 

The idea is to create an object from a String. All of its attributes are defined in a ordered position and with fixed length of this String.

The class Person is an example of how these annotations can be used.

When you run `./gradlew build` for the first time you can notice that the build folder is created, but `build/generated/sources/annotationProcessor/java` is empty.

When you run for the second time, the PersonPositional is created.

And that's a weird behaviour.

Then you can remove the comments on the Main class and build and run. I want to be able to build it for the first time, 
mainly for CI/CD but also if someday I need to run `./gradlew clean`, then I'd need to comment all of my code that is
dependent of the generated code to avoid compiling issues.

