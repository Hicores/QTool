package cc.hicore.HookItemLoader;

import cc.hicore.HookItemLoader.Annotations.MethodScanner;
import cc.hicore.HookItemLoader.Annotations.XPExecutor;
import cc.hicore.HookItemLoader.Annotations.XPItem;
import cc.hicore.HookItemLoader.bridge.BaseXPExecutor;
import cc.hicore.HookItemLoader.bridge.MethodContainer;
import cc.hicore.HookItemLoader.bridge.QQVersion;

@XPItem(name = "ScannerTest",itemType = XPItem.ITEM_Hook)
public class TestScanner {
    @XPExecutor
    public BaseXPExecutor MethodExecuteTest(){
        return param -> {

        };
    }
    @MethodScanner(target = QQVersion.QQ_8_8_11,isStrict = true)
    public void MethodScannerTest(MethodContainer container) throws NoSuchMethodException {
        container.addMethod(Class.class.getMethod("getAnnotation", Class.class));
    }

    @MethodScanner(target = QQVersion.QQ_8_4_5)
    public void testOldMethodScanner(MethodContainer container){

    }
}
