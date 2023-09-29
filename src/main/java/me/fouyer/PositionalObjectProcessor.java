package me.fouyer;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.ElementKind.*;
@SupportedAnnotationTypes("me.fouyer.PositionalObject")
@SupportedSourceVersion(SourceVersion.RELEASE_20)
@AutoService(Processor.class)
public class PositionalObjectProcessor extends AbstractProcessor {
    private boolean valid = true;
    private boolean isValid(ElementKind kind) {
        return kind.equals(CLASS) || kind.equals(RECORD);
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            if (!annotations.isEmpty()) {
                TypeElement positionalObjectAnnotation = new ArrayList<>(annotations).get(0);
                Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(positionalObjectAnnotation);

                for (Element element : annotatedElements) {

                    if (isValid(element.getKind())) {
                        List<? extends Element> attributes = element.getEnclosedElements().stream().filter(a -> a.getKind().equals(FIELD)).toList();
                        Field fields = this.createConstructorSubstrings(attributes);
                        String className = element.toString();

                        try {
                            this.writeFile(className, fields);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        processingEnv.getMessager()
                                .printMessage(Diagnostic.Kind.ERROR, "@PositionalObject must be applied to class or record only", element);
                        throw new RuntimeException("@PositionalObject must be applied to class or record only");
                    }
                }
            }
            return true;
        } catch (IndexOutOfBoundsException e) {
            this.processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "IndexOutOfBoundsException on process. %s".formatted(e));
            throw new IndexOutOfBoundsException(String.valueOf(e));
        }
    }
    private Field createConstructorSubstrings(List<? extends Element> attributes) {
        int currentIndex = 0;
        int startIndex = 0;
        List<String> fields = new ArrayList<>();
        StringBuilder arguments = new StringBuilder("(");

        for (Element attr : attributes) {
            Positional annotation = attr.getAnnotation(Positional.class);

            if (annotation == null) {
                processingEnv.getMessager()
                        .printMessage(Diagnostic.Kind.ERROR, "All @PositionalObject attributes must have a @Positional annotation. The attribute %s is not annotated".formatted(attr.toString()), attr);
                throw new RuntimeException("@PositionalObject must be applied to class or record only");

            } else {
                if (this.valid)  { this.valid = annotation.position() == currentIndex; }
                int endIndex = startIndex + annotation.length();
                if (annotation.strip()) {
                    fields.add("        String %s = strip(str.substring(%s, %s));".formatted(attr.getSimpleName().toString(), startIndex, endIndex));
                } else {
                    fields.add("        String %s = str.substring(%s, %s);".formatted(attr.getSimpleName().toString(), startIndex, endIndex));
                }
                arguments.append("%s, ".formatted(attr.getSimpleName().toString()));
                startIndex = endIndex;
                if (!this.valid) {
                    this.processingEnv.getMessager()
                            .printMessage(Diagnostic.Kind.ERROR, "@PositionalObject must be placed in sequence and all positions must be filled. The position %s is missing".formatted(currentIndex), attr);
                    throw new RuntimeException("@PositionalObject must be applied to class or record only");
                }
                currentIndex++;
            }
        }
        String args = arguments.toString();
        args = args.substring(0, args.length() - 2).concat(");");
        return new Field(fields, args);
    }
    private void writeFile(String className, Field fields) throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) { packageName = className.substring(0, lastDot); }
        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Positional";
        String builderSimpleClassName = builderClassName.substring(lastDot + 1);
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) { out.println("package %s;".formatted(packageName)); }
            out.println("public class %s {".formatted(builderSimpleClassName));
            out.println("    static public %s build(String str) {".formatted(simpleClassName));
            for (String sub : fields.getFields()) { out.println(sub); }
            out.println("       return new %s %s".formatted(simpleClassName, fields.getArguments()));
            out.println("    }");
            out.println("    static private String strip (String str) { return str.strip().trim(); } ");
            out.println("}");
        } catch (IndexOutOfBoundsException e) {
            this.processingEnv.getMessager()
                    .printMessage(Diagnostic.Kind.ERROR, "IndexOutOfBoundsException on writeFile. %s".formatted(e));
            throw new IndexOutOfBoundsException(String.valueOf(e));
        }
    }
    private class Field {
        private List<String> fields;
        private String arguments;
        public List<String> getFields() { return fields; }
        public String getArguments() { return arguments; }
        public Field(List<String> fields, String arguments) {
            this.fields = fields;
            this.arguments = arguments;
        }
    }
}
