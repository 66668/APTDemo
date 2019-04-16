package com.sjy.compiler.mybindview_bean;

import com.oracle.tools.packager.Log;
import com.sjy.annotation.MyBindAct;
import com.sjy.annotation.MyBindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * 从RoundEnvironment.getElementsAnnotatedWith(MyBindAct.class)方法拿到Element元素，在该类中处理
 */
public class BindingSet {
    private VariableElement mVariableElement;
    private int mResId;//处理控件的id

    BindingSet(Element element) throws IllegalArgumentException {
        //排错（相对于butterKnife的处理，该处缩减）
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(String.format("Only fields can be annotated with @%s",
                    MyBindView.class.getSimpleName()));
        }
        mVariableElement = (VariableElement) element;

        //butterKnife的处理核心方式：
        MyBindView bindView = mVariableElement.getAnnotation(MyBindView.class);
        mResId = bindView.value();
        //检测
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

    public void setmResId(int mResId) {
        this.mResId = mResId;
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
