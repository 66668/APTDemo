package com.sjy.compiler;

import com.google.auto.service.AutoService;
import com.sjy.annotation.MyBindView;
import com.sjy.compiler.mybindview_bean.MyAnnotationClass;
import com.sjy.compiler.mybindview_bean.MyBindViewField;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;


/**
 * 该类的处理方式都模仿butterknife
 *
 * <1>说明：初次创建MyProcessor时，extends AbstractProcessor会报红出错，解决方案（需要）
 * <p>
 * 方案1
 * 运行注解处理器
 * 1、在 processors 库的 main 目录下新建 resources 资源文件夹；
 * 2、在 resources文件夹下建立 META-INF/services 目录文件夹；
 * 3、在 META-INF/services 目录文件夹下创建 javax.annotation.processing.Processor 文件；
 * 4、在 javax.annotation.processing.Processor 文件写入注解处理器的全称，包括包路径；
 * <p>
 * 方案2
 * 使用google开源的auto，添加@AutoService(Processor.class)即可
 *
 * <2>需要重写四个方法
 */
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

    /**
     * Types是一个用来处理TypeMirror的工具
     */
    private Types typesUtils;
    /**
     * Elements是一个用来处理Element的工具
     */
    private Elements elements;
    /**
     * 生成java源码
     */
    private Filer filer;
    /**
     * Messager提供给注解处理器一个报告错误、警告以及提示信息的途径。
     * 它不是注解处理器开发者的日志工具，
     * 而是用来写一些信息给使用此注解器的第三方开发者的
     */
    private Messager messager;

    private Locale locale;
    private SourceVersion sourceVersion;
    private Map<String, String> optMap;


    //===============核心设置==================
    private Map<String, MyAnnotationClass> mAnnotatedClassMap;


    //================================================================================================
    //==========================================四个重写方法==========================================
    //================================================================================================

    /**
     * (1)
     * init()方法会被注解处理工具调用，并输入ProcessingEnviroment参数。
     * ProcessingEnviroment提供很多有用的工具类Elements, Types 和 Filer
     *
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        //===============核心设置==================
        typesUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();

        //ProcessingEnvironment可以获取的对象
        elements = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        locale = processingEnv.getLocale();
        sourceVersion = processingEnv.getSourceVersion();
        optMap = processingEnv.getOptions();
        //
        mAnnotatedClassMap = new TreeMap<>();
    }

    /**
     * (2)必须重写的方法
     * <p>
     * 这相当于每个处理器的主函数main()，你在这里写你的扫描、评估和处理注解的代码，以及生成Java文件。
     *
     * @param annotations ：请求处理的注解集合(在（3）中自定义添加进去了)
     * @param roundEnv    可以访问到这个RoundEnvironment下的每一个Element
     * @return true，则这些注解已声明，且不要求后续Processor处理它们；
     * false，则这些注解未声明，且可能要求后续Processor处理它们
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mAnnotatedClassMap.clear();

        //创建自定义注解处理类
        try {
            processMyBindView(roundEnv);
        } catch (Exception e) {
            System.out.println("异常:" + e.toString());
        }

        //将自定义注解处理类，写入文件
        for (MyAnnotationClass annotationClass : mAnnotatedClassMap.values()) {
            try {
                annotationClass.generateFiler().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * (3)必须重写的方法，
     * <p>
     * 处理器想要处理的自定义注解
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    /**
     * (4)必须重写的方法:
     * <p>
     * 指定使用的Java版本，通常这里返回SourceVersion.latestSupported()，默认返回SourceVersion.RELEASE_6
     *
     * @return 使用的Java版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    //================================================================================================
    //==========================================自定义代码处理==========================================
    //================================================================================================

    /**
     * 创建 MyBindView的处理类,处理自定义注解
     *
     * @param roundEnv
     */
    private void processMyBindView(RoundEnvironment roundEnv) {

        for (Element element : roundEnv.getElementsAnnotatedWith(MyBindView.class)) {
            MyAnnotationClass annotationClass = createMyAnnotationClass(element);
            MyBindViewField bindViewField = new MyBindViewField(element);
            annotationClass.addField(bindViewField);

            System.out.println("processBindView annotatedClass: " + annotationClass);
            System.out.println("processBindView bindViewField: " + bindViewField);
        }

    }

    /**
     * 根据 MyBindView，获取每一个Element的处理类,并将生成的处理类保存到map中
     *
     * @param element
     * @return
     */
    private MyAnnotationClass createMyAnnotationClass(Element element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String fullName = typeElement.getQualifiedName().toString();
        System.out.println("getAnnotatedClass typeElement: " + typeElement);
        MyAnnotationClass annotationClass = mAnnotatedClassMap.get(fullName);
        //如果集合中不存在，则添加到集合中
        if (annotationClass == null) {
            //创建注解处理类
            annotationClass = new MyAnnotationClass(typeElement, elements);
            mAnnotatedClassMap.put(fullName, annotationClass);
        }
        return annotationClass;
    }

    /**
     * <p>
     * 将自定义的注解添加到set列表中
     * <p>
     * 模仿butterKnife的写法,给（3）getSupportedAnnotationTypes使用
     *
     * @return
     */
    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(MyBindView.class);
//        annotations.add(MyBindAct.class);
        return annotations;
    }


}
