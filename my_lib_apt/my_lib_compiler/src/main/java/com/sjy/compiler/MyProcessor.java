package com.sjy.compiler;

import com.google.auto.service.AutoService;
import com.sjy.annotation.MyBindAct;
import com.sjy.annotation.MyBindView;
import com.sjy.compiler.mybindview_bean.BindingSet;
import com.sjy.compiler.mybindview_bean.Id;
import com.sun.source.util.Trees;
import com.sun.tools.javac.util.Log;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
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
 * <1>说明：初次创建MyProcessor时，extends AbstractProcessor会报红出错，解决方案
 * <p>
 * 方案1
 * 运行注解处理器
 * 1、在 processors 库的 main 目录下新建 resources 资源文件夹；
 * 2、在 resources文件夹下建立 META-INF/services 目录文件夹；
 * 3、在 META-INF/services 目录文件夹下创建 javax.annotation.processing.Processor 文件；
 * 4、在 javax.annotation.processing.Processor 文件写入注解处理器的全称，包括包路径；
 * <p>
 * 方案2
 * 每一个注解处理器类都必须有一个空的构造函数，默认不写就行（不懂～～，我是用第一个方案解决的）;
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

    private Map<String, AnnotatedClass> mAnnotatedClassMap;
    //===============核心设置==================
    private @Nullable
    Trees trees;

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
        try {
            trees = Trees.instance(processingEnv);
        } catch (IllegalArgumentException ignored) {
        }

        //ProcessingEnvironment可以获取的对象
        elements = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        locale = processingEnv.getLocale();
        sourceVersion = processingEnv.getSourceVersion();
        optMap = processingEnv.getOptions();
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
        Map<TypeElement, BindingSet> bindingMap = findAndParseTargets(roundEnv);

        return false;
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
     * 模仿butterKnife的写法,给（3）getSupportedAnnotationTypes使用
     * <p>
     * 将自定义的注解添加到set列表中
     *
     * @return
     */
    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(MyBindView.class);
        annotations.add(MyBindAct.class);
        return annotations;
    }

    /**
     * 自定义注解的获取并处理，封装到具体的类中，自定义处理
     */
    private void findAndParseTargets(RoundEnvironment roundEnv) {
        Map<TypeElement, BindingSet.Builder> builderMap = new LinkedHashMap<>();
        Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();

        //处理自定义注解 MyBindView
        for (Element element : roundEnv.getElementsAnnotatedWith(MyBindAct.class)) {
            try {
                parseMyBindView(element, builderMap, erasedTargetNames);
            } catch (Exception e) {
                //简化butterKnife对应的处理
                System.out.println("e=" + e.getMessage());
            }
        }
        //如果还自定义其他注解，仿照for循环，处理即可

    }

    /**
     * 该处是处理MyBindView
     *
     * @param element
     * @param builderMap
     * @param erasedTargetNames
     */
    private void parseMyBindView(Element element, Map<TypeElement, BindingSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {
        boolean hasError = false;
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Assemble information on the field.
        String name = element.getSimpleName().toString();
        int id = element.getAnnotation(MyBindView.class).value();//拿到绑定的控件id
        Id resourceId = elementToId(element, MyBindView.class, id);
        BindingSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
        builder.addResource(
                new FieldResourceBinding(resourceId, name, FieldResourceBinding.Type.STRING));

        erasedTargetNames.add(enclosingElement);
    }

}
