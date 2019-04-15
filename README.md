# APT/JavaPoet 编译期注解处理技术（自定义ButterKnife注解）

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

### apt处理annotation流程：
1. 定义注解
2. 定义注解处理器，自定义需要生成的代码
3. 使用处理器
4. apt自动完成剩下的操作


### apt的处理要素+概念理解：

**注解处理器**（abstractProcess）+**代码处理**（javaPoet，最好不要自己处理）+**处理器注册**（AutoService）+**apt(AnnotationProcessor)**



abstractProcess:用于在编译时（通常as点击build按钮后），扫描和处理自定义的注解的类，将自定义注解扫描出来，获取它的信息。

自定义注解处理器，需要重写4个必要的方法

1. process()方法：类似java的main()入口
2. init()方法：Types/Elements/Filer 获取工具


自定义注解所需要的依赖：

1：app/build.gradle/dependencies: 

    implementation 'com.squareup:javapoet:1.11.1'

2: app/build.gradle/dependencies:

    //开源：https://github.com/google/auto
    implementation 'com.google.auto.service:auto-service:1.0-rc5'
    //implementation 'com.google.auto:auto-common:0.10'
    //implementation 'com.google.auto.factory:auto-factory:1.0-beta6'
    //implementation 'com.google.auto.value:auto-value:1.6.3'
    
   说明：  
   
    auto-service作用：
        向系统注册processor(自定义注解处理器)，执行编译时使用processor进行处理
        
    javapoet作用：
        提供了一套生成java代码的api，利用这些api处理注解，生成新的代码或源文件。
    
### 具体代码实现步骤
1. 创建my_lib_annotations:用于存放 项目中要使用的注解
2. 
    
         