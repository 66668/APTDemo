package com.sjy.compiler;

import com.google.auto.service.AutoService;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;


/**
 * 说明：初次创建MyProcessor时，extends AbstractProcessor会报红出错，解决方案
 * <p>
 * 方案1
 * 运行注解处理器
 * 1、在 processors 库的 main 目录下新建 resources 资源文件夹；
 * 2、在 resources文件夹下建立 META-INF/services 目录文件夹；
 * 3、在 META-INF/services 目录文件夹下创建 javax.annotation.processing.Processor 文件；
 * 4、在 javax.annotation.processing.Processor 文件写入注解处理器的全称，包括包路径；
 * <p>
 * 方案2
 * 每一个注解处理器类都必须有一个空的构造函数，默认不写就行;
 */
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

    /**
     * 必须重写的方法
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return false;
    }
}
