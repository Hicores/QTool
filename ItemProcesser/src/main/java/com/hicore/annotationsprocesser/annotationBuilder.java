package com.hicore.annotationsprocesser;

import com.google.auto.service.AutoService;
import com.hicore.HookItem;
import com.sun.tools.javac.code.Symbol;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.hicore.HookItem"})
public class annotationBuilder extends AbstractProcessor {
    private Filer filer;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();

    }
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (TypeElement element : set){
            StringBuilder clzName = new StringBuilder();
            clzName.append(""+
                    "package com.hicore.qtool.XposedInit.ItemLoader;\n"+
                    "import java.util.ArrayList;\n"+
                    "public class MItemInfo{\n"+
                    "public static ArrayList<String> runOnAllProc = new ArrayList<>();\n"+
                    "public static ArrayList<String> runOnMainProc = new ArrayList<>();\n"+
                    "public static ArrayList<String> BasicInit = new ArrayList<>();\n"+
                    "public static ArrayList<String> DelayInit = new ArrayList<>();\n"+
                    "static {\n"
            );
            for (Element anno :roundEnvironment.getElementsAnnotatedWith(element)){
                Symbol.ClassSymbol symbol = (Symbol.ClassSymbol) anno;

                HookItem item = anno.getAnnotation(HookItem.class);
                if (item.isRunInAllProc()){
                    System.out.println("add class "+symbol.flatName()+" to all proc hook item list.");
                    clzName.append("runOnAllProc.add(\"").append(symbol.flatName()).append("\");\n");
                }else {
                    System.out.println("add class "+symbol.flatName()+" to main proc hook item list.");
                    clzName.append("runOnMainProc.add(\"").append(symbol.flatName()).append("\");\n");
                }

                if (item.isDelayInit()){
                    System.out.println("add class "+symbol.flatName()+" to delay hook item list.");
                    clzName.append("DelayInit.add(\"").append(symbol.flatName()).append("\");\n");
                }else {
                    System.out.println("add class "+symbol.flatName()+" to common hook item list.");
                    clzName.append("BasicInit.add(\"").append(symbol.flatName()).append("\");\n");
                }
            }
            clzName.append("\n}}");

            JavaFileObject sourceFile = null;
            try {
                sourceFile = filer.createSourceFile("com.hicore.qtool.XposedInit.ItemLoader.MItemInfo");
                Writer writer = sourceFile.openWriter();
                writer.write(clzName.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



        return true;
    }
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}