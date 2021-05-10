package org.paulcager.cover;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Coverage calls all getters, setters etc to increase code coverage statistics.
 * A typical call would be:
 * <pre>
 *     new Coverage(new MyObject()).cover();
 * </pre>
 */
public class Coverage {

    private final Object obj;
    private boolean catchErrors = true;

    /**
     * @param obj The object to add code coverage for
     */
    public Coverage(Object obj) {
        this.obj = obj;
    }

    public Coverage catchErrors(boolean catchErrors) {
        this.catchErrors = catchErrors;
        return this;
    }

    /**
     * Attempt to call all getters, setters, toString, equals and hashCode.
     */
    public void cover() {
        gettersSetters().equalsHashcodeString();
    }

    public Coverage gettersSetters() {
        Collection<Method> getters = getGetters();
        for (Method g : getters) {
            getAndSet(g);
        }

        return this;
    }


    public Coverage equalsHashcodeString(Object other) {
        try {
            obj.equals(other);
            obj.hashCode();
            obj.toString();
        } catch (RuntimeException e) {
            if (!catchErrors) {
                throw e;
            }
        }

        return this;
    }

    public Coverage equalsHashcodeString() {
        return equalsHashcodeString(obj);
    }

    private Collection<Method> getGetters() {
        Class<? extends Object> cls = obj.getClass();
        // getMethods will also include superclass's methods, while getDeclaredMethods will
        // also include private and package-private. Therefore we need them both.
        return Stream.concat(Arrays.stream(cls.getMethods()), Arrays.stream(cls.getDeclaredMethods()))
                .filter(m -> m.getName().startsWith("get"))
                .filter(m -> m.getParameterCount() == 0)
                .collect(Collectors.toSet());
    }

    private void getAndSet(Method getter) {
        try {
            getter.setAccessible(true);
            Object value = getter.invoke(obj);

            String setterName = "set" + getter.getName().substring(3);
            findSetter(setterName, getter.getReturnType()).ifPresent(setter -> {
                setter.setAccessible(true);
                try {
                    setter.invoke(obj, value);
                } catch (ReflectiveOperationException e) {
                    if (!catchErrors) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (ReflectiveOperationException e) {
            if (!catchErrors) {
                throw new RuntimeException(e);
            }
        }
    }

    private Optional<Method> findSetter(String setterName, Class<?> Type) {
        try {
            return Optional.of(obj.getClass().getDeclaredMethod(setterName, Type));
        }
        catch (NoSuchMethodException e) {
            try {
                return Optional.of(obj.getClass().getMethod(setterName, Type));
            } catch (NoSuchMethodException noSuchMethodException) {
                return Optional.empty();
            }
        }
    }
}
