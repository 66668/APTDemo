package com.sjy.compiler;

import com.squareup.javapoet.JavaFile;

import java.util.ArrayList;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

/**
 * 自定义注解处理类
 */
public class MyAnnotationClass {
    private ArrayList<MyBindViewField> mFields;

    public MyAnnotationClass(TypeElement typeElement, Types typesUtils) {
        mFields = new ArrayList<>();
    }

    public void addField(MyBindViewField field) {
        mFields.add(field);
        System.out.println("mFields: " + mFields);
    }

    /**
     * 核心
     * 利用开源javaPoet生成对应的java代码
     *
     * @return
     */
    public JavaFile generateFiler() {

        return null;
    }


}
