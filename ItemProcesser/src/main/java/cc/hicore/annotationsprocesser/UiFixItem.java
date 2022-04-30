package cc.hicore.annotationsprocesser;

import com.google.auto.service.AutoService;
import cc.hicore.UIItem;
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
@SupportedAnnotationTypes({"cc.hicore.UIItem"})
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
            String clzData = "package cc.hicore.qtool.XposedInit.ItemLoader;\n" +
                    "import java.util.LinkedHashSet;\n" +
                    "\n" +
                    "public class UiItemInfo{\n" +
                    "    public static LinkedHashSet<HookLoader.UiInfo> UiInfos = new LinkedHashSet<>();\n" +
                    "    private static void addUiInfo(int targetID,String groupName,int type,String name,String desc,String id,boolean defCheck,String clzName){\n" +
                    "        HookLoader.UiInfo NewInfo = new HookLoader.UiInfo();\n" +
                    "        NewInfo.targetID = targetID;\n" +
                    "        NewInfo.groupName = groupName;\n" +
                    "        NewInfo.type = type;\n" +
                    "        NewInfo.name = name;\n" +
                    "        NewInfo.desc = desc;\n" +
                    "        NewInfo.id = id;\n" +
                    "        NewInfo.defCheck = defCheck;\n" +
                    "\t\tNewInfo.clzName = clzName;\n" +
                    "        UiInfos.add(NewInfo);\n" +
                    "    }\n" +
                    "    public static LinkedHashSet<HookLoader.UiInfo> getUiInfos(){\n" +
                    "        UiInfos.clear();\n" +
                    "        !!!这里存放初始化代码!!!\n" +
                    "        return UiInfos;\n" +
                    "    }\n" +
                    "}";
            StringBuilder clzName = new StringBuilder();
            for (Element anno :roundEnvironment.getElementsAnnotatedWith(element)){
                Symbol.ClassSymbol symbol = (Symbol.ClassSymbol) anno;
                System.out.println("New UI Item Controller:"+symbol.flatName());

                UIItem item = anno.getAnnotation(UIItem.class);
                clzName.append("addUiInfo(")
                        .append(item.targetID()).append(",")
                        .append("\"").append(item.groupName()).append("\",")
                        .append(item.type()).append(",")
                        .append("\"").append(item.name()).append("\",")
                        .append("\"").append(item.desc()).append("\",")
                        .append("\"").append(item.id()).append("\",")
                        .append(item.defCheck()).append(",")
                        .append("\"").append(symbol.flatName()).append("\");\n");
            }

            JavaFileObject sourceFile = null;
            try {
                sourceFile = filer.createSourceFile("cc.hicore.qtool.XposedInit.ItemLoader.UiItemInfo");
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
