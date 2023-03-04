package cc.hicore.DexFinder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.luckypray.dexkit.DexKitBridge;
import io.luckypray.dexkit.descriptor.member.DexMethodDescriptor;

public class DexKitFinder implements IDexFinder{
    private DexKitBridge bridge;
    private ClassLoader loader;
    @Override
    public void init(String apkPath, ClassLoader loader) {
        SoLoader.loadByName("libdexkit.so");
        bridge = DexKitBridge.create(apkPath);
        this.loader = loader;
    }

    @Override
    public Method[] findMethodByString(String str) {
        if (str ==null)return new Method[0];
        List<DexMethodDescriptor> desc = bridge.findMethodUsingString(str,false,"","","",null,false,null);

        ArrayList<Method> methods = new ArrayList<>();
        for (DexMethodDescriptor dexMethodDescriptor : desc) {
            try {
                if (dexMethodDescriptor.isMethod()){
                    methods.add(dexMethodDescriptor.getMethodInstance(loader));
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return methods.toArray(new Method[0]);
    }

    @Override
    public Method[] findMethodBeInvoked(Method beInvoked) {
        if (beInvoked == null)return new Method[0];
        List<DexMethodDescriptor> desc = bridge.findMethodCaller(new DexMethodDescriptor(beInvoked).getDescriptor(),
                "","","",null,"","","",null,false,null);
        ArrayList<Method> methods = new ArrayList<>();
        for (DexMethodDescriptor dexMethodDescriptor : desc) {
            try {
                if (dexMethodDescriptor.isMethod()){
                    methods.add(dexMethodDescriptor.getMethodInstance(loader));
                }

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return methods.toArray(new Method[0]);
    }

    @Override
    public Method[] findMethodInvoking(Method beInvoked) {
        if (beInvoked == null)return new Method[0];
        Map<DexMethodDescriptor,List<DexMethodDescriptor>> desc = bridge.findMethodInvoking(new DexMethodDescriptor(beInvoked).getDescriptor(),
                "","","",null,"","","",null,false,null);
        ArrayList<Method> methods = new ArrayList<>();
        for (DexMethodDescriptor dexMethodDescriptor : desc.keySet()) {
            try {
                for (DexMethodDescriptor dexMethodDescriptor2 : desc.get(dexMethodDescriptor)) {
                    if (dexMethodDescriptor2.isMethod()){
                        methods.add(dexMethodDescriptor2.getMethodInstance(loader));
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return methods.toArray(new Method[0]);
    }
}
