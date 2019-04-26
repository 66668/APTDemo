# APT/JavaPoet 编译期注解处理技术（自定义ButterKnife注解）

APT（Annotation Processing Tool）
## 注解简介：
运行时注解：因为性能问题被诟病

编译期注解：依赖apt

注解的分类：
1. source
2. class(编译期注解)
3. runtime(运行期注解)反射

本章讲解编译期注解

## apt简介：

APT(annotation processing tool) 是一种注解处理工具，它对源码文件进行检测，找出其中的annotation,根据注解自动生成代码


### apt的处理要素+概念理解：

**注解处理器**（abstractProcess）+**代码处理**（最好不要自己处理,使用开源javaPoet）+**处理器注册**（AutoService，google开源的auto）+**apt(AnnotationProcessor)**



abstractProcess:用于在编译时（通常as点击build按钮后），扫描和处理自定义的注解的类，将自定义注解扫描出来，获取它的信息。

自定义注解处理器，需要重写4个必要的方法

1. process()方法：类似java的main()入口
2. init()方法：Types/Elements/Filer 获取工具
3. getSupportedAnnotationTypes:处理器想要处理的自定义注解
4. getSupportedSourceVersion： 指定使用的Java版本

    
### 具体代码实现步骤
1. 创建my_lib_annotations:用于存放 项目中要使用的注解
2. 创建my_lib_compiler：库主要是应用apt技术处理注解，生成相关代码或者相关源文件，是核心所在

my_lib_compiler需要的依赖：

1：my_lib_compiler/app/build.gradle/dependencies: 

    //添加自定义注解的库
    implementation project(':my_lib_apt:my_lib_annotations')
    implementation 'com.squareup:javapoet:1.11.1'
    //开源：https://github.com/google/auto
    implementation 'com.google.auto.service:auto-service:1.0-rc5'
    implementation 'com.google.auto:auto-common:0.10'
    
   说明：  
   
    auto-service作用：
        向系统注册processor(自定义注解处理器)，执行编译时使用processor进行处理
        
    javapoet作用：
        提供了一套生成java代码的api，利用这些api处理注解，生成新的代码或源文件。用于注解之后，进行的代码处理框架（比手动写效率高）
  
### apt处理annotation流程：

1. 定义注解
2. 定义注解处理器，自定义需要生成的代码
3. 使用处理器
4. apt自动完成剩下的操作
  
**步骤**：：

javapoet使用简介：

            //第一步 生成main函数
            MethodSpec main = MethodSpec.methodBuilder("main")//生成方法名
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)//修饰符
                    .returns(void.class)//返回类型
                    .addParameter(String[].class, "args")//参数
                    .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")//打印：System.out.printlin("Hello,JavaPoet");
                    .build();
            //第二步 生成类
            TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")//生成类
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//类修饰
                    .addMethod(main)//添加方法
                    .build();
            //第三步 生成java文件对象
            JavaFile javaFile = JavaFile.builder("com.sjy.demo", helloWorld).build();//（包名，对象）
            //第四步 输出到文件
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
利用JavaPoet生成java代码

剩下的就是参考butterKnife实现bindView的功能即可，理解上述讲解，apt技术就会了。

         