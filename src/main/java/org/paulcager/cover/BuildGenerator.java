package org.paulcager.cover;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.time.temporal.Temporal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Lazy code by and for lazy programmers.
 *
 * Given an instance of a class that has been annotated with @{@link lombok.Builder}, BuildGenerator
 * will generate Java source code to build an equivalent object. It works Just Well Enough™ for my
 * purposes, but may well generate "TODO" comments for types not currently supported.
 */
public class BuildGenerator {
    private final Object obj;

    public BuildGenerator(Object obj) {
        this.obj = obj;
    }

    public void generate(PrintWriter w) throws ReflectiveOperationException {
        generate(w, obj);
        w.flush();
    }

    @SuppressWarnings("unchecked")
    private void generate(PrintWriter w, Object o) throws ReflectiveOperationException {
        if (o == null) {
            w.print("null");
            return;
        }

        Class<?> cls = o.getClass();
        // Note: don't try to use cls.getAnnotation(Builder.class). Most
        // Lombok annotations are not retained after compilation.

        Method builder = getMethod(cls, "builder");

        if (cls.isPrimitive()) {
            generatePrimitive(w, o, cls);
        }
        else if (cls == Character.class) {
            w.print("'" + o + "'");
        }
        else if (CharSequence.class.isAssignableFrom(cls)) {
            generateCharSequence(w, (CharSequence) o, cls);
        }
        else if (isWrapper(cls)) {
            w.print(o);
        }
        else if (builder != null) {
            generateBuilderCalls(w, o, builder, cls);
        }
        else if (Temporal.class.isAssignableFrom(cls)) {
            generateTemporal(w, (Temporal)o, cls);
        }
        else if (Iterable.class.isAssignableFrom(cls)) {
            generateIterable(w, (List<Object>) o, cls);
        }
        else if (Map.class.isAssignableFrom(cls)) {
            generateMap(w, (Map<Object, Object>) o, cls);
        }
        else {
            generateValue(w, o, cls);
        }
    }

    private boolean isWrapper(Class cls) {
        return Number.class.isAssignableFrom(cls) ||
                cls == Boolean.class;
    }

    private void generateCharSequence(PrintWriter w, CharSequence o, Class<?> cls) {
        w.append('"').append(StringEscapeUtils.escapeJava(o.toString())).append('"');
    }

    private void generateValue(PrintWriter w, Object o, Class<?> cls) {
        w.print("/* TODO: " + cls + " - " + o + "*/");
    }

    private void generateIterable(PrintWriter w, Iterable<?> o, Class<?> cls) throws ReflectiveOperationException {
        if (o instanceof List) {
            w.print("List.of(");
        }
        else if (o instanceof Set) {
            w.print("Set.of(");
        }
        else {
            w.print("/* TODO " + cls.getName() + " */");
            return;
        }

        var it = o.iterator();
        while (it.hasNext()) {
            generate(w, it.next());
            if (it.hasNext()) {
                w.print(", ");
            }
        }

        w.print(")");
    }

    private void generateMap(PrintWriter w, Map<Object, Object> m, Class<?> cls) throws ReflectiveOperationException {
        w.print("Map.of(");

        Iterator<Map.Entry<Object, Object>> it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> next = it.next();
            generate(w, next.getKey());
            w.print(", ");
            generate(w, next.getValue());

            if (it.hasNext()) {
                w.print(", ");
            }
        }

        w.print(")");
    }

    private void generatePrimitive(PrintWriter w, Object o, Class<?> cls) {
        if (o instanceof Character) {
            w.append('\'').append((Character) o).append('\'');
        }
        else {
            w.append(o.toString());
        }
    }

    private void generateTemporal(PrintWriter w, Temporal o, Class<?> cls) {
        w.print(cls.getName() + ".parse(\"" + o.toString() + "\")");
    }

    private void generateBuilderCalls(PrintWriter w, Object o, Method builder, Class<?> cls) throws ReflectiveOperationException {
        w.println(cls.getName() + ".builder()");

        Class<?> builderClass = builder.getReturnType();
        Method[] builderMethods = builderClass.getDeclaredMethods();

        for (Method m : builderMethods) {
            generateBuilderCall(w, o, m, builderClass);
        }

        w.println(".build()");
    }

    private void generateBuilderCall(PrintWriter w, Object o, Method builderMethod, Class<?> builderClass) throws ReflectiveOperationException {
        switch (builderMethod.getName()) {
            case "toString":
            case "build":
                return;
        }

        if (builderMethod.getName().equals("toString")) {
            return;
        }

        Method getter = getGetter(o.getClass(), builderMethod.getName());
        if (getter == null) {
            w.println("/* TODO - no getter " + builderMethod.getName() + " */");
            return;
        }

        Object value = getter.invoke(o);
        w.print("\t." + builderMethod.getName() + "(");
        generate(w, value);
        w.println(")");
    }

    private Method getMethod(Class<?> cls, String name) {
        try {
            return cls.getMethod(name);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    private Method getGetter(Class<?> cls, String name) {
        Method m = getMethod(cls,name);
        if (m != null) {
            return m;
        }

        String camelCase = Character.toUpperCase(name.charAt(0)) + name.substring(1);

        m = getMethod(cls, "get" + camelCase);
        if (m != null) {
            return m;
        }

        m = getMethod(cls, "is" + camelCase);

        return m;
    }
}

