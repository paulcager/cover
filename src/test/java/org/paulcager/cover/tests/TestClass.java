package org.paulcager.cover.tests;

import java.util.Date;
import java.util.Objects;

public class TestClass {
    private int i;
    private Object obj = "A String";
    private Object nullObj = null;
    private Date d;
    private int noSetter;
    private int packageOnly;
    private int privateField;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Object getNullObj() {
        return nullObj;
    }

    public void setNullObj(Object nullObj) {
        this.nullObj = nullObj;
    }

    public Date getD() {
        return d;
    }

    public void setD(Date d) {
        this.d = d;
    }

    public int getNoSetter() {
        return noSetter;
    }

    int getPackageOnly() {
        return packageOnly;
    }

    void setPackageOnly(int packageOnly) {
        this.packageOnly = packageOnly;
    }

    private int getPrivateField() {
        return privateField;
    }

    private void setPrivateField(int privateField) {
        this.privateField = privateField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestClass testClass = (TestClass) o;
        return i == testClass.i && noSetter == testClass.noSetter && packageOnly == testClass.packageOnly && privateField == testClass.privateField && Objects.equals(obj, testClass.obj) && Objects.equals(nullObj, testClass.nullObj) && Objects.equals(d, testClass.d);
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, obj, nullObj, d, noSetter, packageOnly, privateField);
    }

    @Override
    public String toString() {
        return "TestClass{" +
                "i=" + i +
                ", obj=" + obj +
                ", nullObj=" + nullObj +
                ", d=" + d +
                ", noSetter=" + noSetter +
                ", packageOnly=" + packageOnly +
                ", privateField=" + privateField +
                '}';
    }
}
