package com.sjy.compiler.mybindview_bean;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * 自定义注解处理类：使用javapoet生成.java文件
 */
public class MyAnnotationClass {

    private ArrayList<MyBindViewField> mFields;//自定义注解的处理集合
    private TypeElement mTypeElement;
    private Elements mElements;

    public MyAnnotationClass(TypeElement typeElement, Elements elements) {
        mFields = new ArrayList<>();
        this.mTypeElement = typeElement;
        this.mElements = elements;
    }

    /**
     * 保存有自定义注解的处理
     *
     * @param field
     */
    public void addField(MyBindViewField field) {
        mFields.add(field);
    }

    /**
     * 核心
     * 利用开源javaPoet生成对应的.java代码
     *
     * @return
     */
    public JavaFile generateFiler() {

        //（1）生成java方法bindView：
        MethodSpec.Builder bindViewBuidler = MethodSpec.methodBuilder("bindView")
                .addModifiers(Modifier.PUBLIC)//public
                .addAnnotation(Override.class)//接口的复写方法
                .addParameter(TypeName.get(mTypeElement.asType()), "host")//参数
                .addParameter(TypeName.OBJECT, "source")//参数 obj
                .addParameter(TypeUtil.PROVIDER, "finder");//添加参数，id

        //添加bindView方法的处理解析
        for (MyBindViewField field : mFields) {
            //三个参数
            bindViewBuidler.addStatement("host.$N = ($T)(finder.findView(source, $L))", field.getFieldName());
        }

        //（2）生成java方法unbindView：
        MethodSpec.Builder unbindViewBuilder = MethodSpec.methodBuilder("unbindView")
                .addModifiers(Modifier.PUBLIC)//public
                .addAnnotation(Override.class)//接口的复写方法
                .addParameter(TypeName.get(mTypeElement.asType()), "host");//添加参数

        //添加unbindView方法的处理解析
        for (MyBindViewField field : mFields) {
            unbindViewBuilder.addStatement("host.$N = null", field.getFieldName());
        }

        //（3）生成java的类文件（.java的文件）
        TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + "$MyViewBinder")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.BINDER, TypeName.get(mTypeElement.asType())))//类实现的接口名
                .addMethod(bindViewBuidler.build())//添加bindView方法
                .addMethod(unbindViewBuilder.build())//添加unbindView方法
                .build();
        //添加包名
        String packageName = mElements.getPackageOf(mTypeElement).getQualifiedName().toString();
        //JavaFile
        JavaFile result = JavaFile.builder(packageName, injectClass).build();

        //将打印也写入
        try {
            result.writeTo(System.out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 反射调用对应的接口，需要的接口位置：my_butterknife_api，包名：com.sjy.butterknife
     */
    private static class TypeUtil {
        /**
         * 调用要处理的接口，写入java文件中
         */
        static final ClassName BINDER = ClassName.get("com.sjy.butterknife", "MyViewBinder");
        /**
         * bindView方法参数使用
         */
        static final ClassName PROVIDER = ClassName.get("com.sjy.butterknife", "MyViewFinder");

    }


}
