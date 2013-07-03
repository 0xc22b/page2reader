package com.wit;

import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflector {

    @SuppressWarnings("rawtypes")
    public static Object invokePrivateMethod(Class targetClass,
            String methodName, Class[] argClasses, Object object,
            Object[] argObjects){
        try {
            @SuppressWarnings("unchecked")
            Method method = targetClass.getDeclaredMethod(methodName, argClasses);
            method.setAccessible(true);
            return method.invoke(object, argObjects);
        } catch (NoSuchMethodException e) {
            fail("Error: " + e.toString() + "at invokePrivateMethod");
        } catch (IllegalAccessException e) {
            fail("Error: " + e.toString() + "at invokePrivateMethod");
        } catch (IllegalArgumentException e) {
            fail("Error: " + e.toString() + "at invokePrivateMethod");
        } catch (InvocationTargetException e) {
            fail("Error: " + e.toString() + "at invokePrivateMethod");
        }
        return null;
    }
}
