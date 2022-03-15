package com.hicore.annotationsprocesser;

import com.google.auto.service.AutoService;
import com.hicore.UIItem;
import com.sun.tools.javac.code.Symbol;

import java.io.IOException;
import java.io.Writer;
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
@SupportedAnnotationTypes({"com.hicore.UIItem"})
public class UiFixItem extends AbstractProcessor {
    private Filer filer;
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();

    }
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (TypeElement element : set){
            String clzData = "package com.hicore.qtool.XposedInit.ItemLoader;\n" +
                    "import java.util.HashSet;\n" +
                    "\n" +
                    "public class UiItemInfo{\n" +
                    "\tpublic static HashSet<HookLoader.UiInfo> UiInfos = new HashSet<>();\n" +
                    "\tprivate static void addUiInfo(int type,String UiName,String UiDesc,int Pos,String ClassName){\n" +
                    "\t\tHookLoader.UiInfo NewInfo = new HookLoader.UiInfo();\n" +
                    "\t\tNewInfo.type = type;\n" +
                    "\t\tNewInfo.title = UiName;\n" +
                    "\t\tNewInfo.desc = UiDesc;\n" +
                    "\t\tNewInfo.Position = Pos;\n" +
                    "\t\tNewInfo.ClzName = ClassName;\n" +
                    "\t\tUiInfos.add(NewInfo);\n" +
                    "\t}\n" +
                    "\tpublic static HashSet<HookLoader.UiInfo> getUiInfos(){\n" +
                    "\t\tUiInfos.clear();\n" +
                    "\t\t!!!这里存放初始化代码!!!\n" +
                    "\t\treturn UiInfos;\n" +
                    "\t}\n"+
                    "}";
            StringBuilder clzName = new StringBuilder();
            for (Element anno :roundEnvironment.getElementsAnnotatedWith(element)){
                Symbol.ClassSymbol symbol = (Symbol.ClassSymbol) anno;
                System.out.println("New UI Item Controller:"+symbol.flatName());

                UIItem item = anno.getAnnotation(UIItem.class);
                clzName.append("addUiInfo(").append(item.itemType())
                        .append(",\"").append(item.itemName()).append("\",\"").append(item.itemDesc())
                        .append("\",").append(item.mainItemID()).append(",\"")
                        .append(symbol.flatName()).append("\");").append("\n");

            }

            JavaFileObject sourceFile = null;
            try {
                sourceFile = filer.createSourceFile("com.hicore.qtool.XposedInit.ItemLoader.UiItemInfo");
                Writer writer = sourceFile.openWriter();
                writer.write(clzData.replace("!!!这里存放初始化代码!!!",clzName.toString()));
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
