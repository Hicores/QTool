package cc.hicore.annotationsprocesser;

import com.google.auto.service.AutoService;
import cc.hicore.HookItem;
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
@SupportedAnnotationTypes({"cc.hicore.HookItemLoader.Annotations.XPItem"})
public class XPItemScanner extends AbstractProcessor {
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
                    "package cc.hicore.HookItemLoader.bridge;\n"+
                    "import java.util.ArrayList;\n"+
                    "public class XPItems{\n"+
                    "public static ArrayList<String> XPItems = new ArrayList<>();\n"+
                    "static {\n"
            );
            for (Element anno :roundEnvironment.getElementsAnnotatedWith(element)){
                Symbol.ClassSymbol symbol = (Symbol.ClassSymbol) anno;
                System.out.println("Add new XPItem: "+symbol.flatName());
                clzName.append("XPItems.add(\"").append(symbol.flatName()).append("\");\n");
            }
            clzName.append("\n}}");

            JavaFileObject sourceFile;
            try {
                sourceFile = filer.createSourceFile("cc.hicore.HookItemLoader.bridge.XPItems");
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