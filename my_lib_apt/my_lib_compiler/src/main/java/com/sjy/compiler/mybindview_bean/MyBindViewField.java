package com.sjy.compiler.mybindview_bean;

import com.sjy.annotation.MyBindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * 自定义注解MyBindView处理，获取注解使用位置的包名，注解绑定控件的id
 */
public class MyBindViewField {

    private VariableElement mVariableElement;
    private int mResId;


    public MyBindViewField(Element element) {
        //排错
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(String.format("Only fields can be annotated with @%s",
                    MyBindView.class.getSimpleName()));
        }
        //
        mVariableElement = (VariableElement) element;
        System.out.println("BindViewField mVariableElement: " + mVariableElement);
        //获取自定义注解
        MyBindView bindView = mVariableElement.getAnnotation(MyBindView.class);
        //拿到控件的id
        mResId = bindView.value();
        System.out.println("BindViewField mResId: " + mResId);
        if (mResId < 0) {
            throw new IllegalArgumentException(
                    String.format("value() in %s for field %s is not valid !", MyBindView.class.getSimpleName(),
                            mVariableElement.getSimpleName()));
        }
    }

    public Name getFieldName() {
        return mVariableElement.getSimpleName();
    }

    public int getmResId() {
        return mResId;
    }

    /**
     * 获取变量类型
     *
     * @return
     */
    TypeMirror getFieldType() {
        return mVariableElement.asType();
    }
}
