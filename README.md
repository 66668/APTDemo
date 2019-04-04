# APT 编译器注解处理技术（自定义ButterKnife注解）

APT(annotation processing tool) 是一种注解处理工具，它对源码文件进行检测，找出其中的annotation,根据注解自动生成代码

apt的处理要素：

注解处理器（abstractProcess）+代码处理（javaPoet，最好不要自己处理）+处理器注册（AutoService）+apt(AnnotationProcessor)


abstractProcess:用于在编译时扫描和处理注解的类

自定义注解所需要的依赖：

1：app/build.gradle/dependencies: 

    implementation 'com.squareup:javapoet:1.11.1'

2: app/build.gradle/dependencies:
    
    implementation 'com.google.auto.service:auto-service:1.0-rc5'
    //implementation 'com.google.auto:auto-common:0.10'
    //implementation 'com.google.auto.factory:auto-factory:1.0-beta6'
    //implementation 'com.google.auto.value:auto-value:1.6.3'