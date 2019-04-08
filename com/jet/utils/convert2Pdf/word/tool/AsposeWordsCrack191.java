import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class AsposeWordsCrack191 {

    public static void changeMethod() throws Exception {
        System.out.println("开始破解！");
        ClassPool.getDefault().insertClassPath("d:\\aspose-words-19.1-jdk16.jar");

        CtClass c2 = ClassPool.getDefault()
                .getCtClass("com.aspose.words.zzZLJ");
        CtMethod[] ms = c2.getDeclaredMethods();
        for (CtMethod c : ms) {
            System.out.println("method name:" + c.getName() + "() ,Parameter:");
            CtClass[] ps = c.getParameterTypes();
            for (CtClass cx : ps) {
                System.out.println("\t" + cx.getName());
            }

            if (c.getName().equals("zzZ") && ps.length == 3
                    && ps[0].getName().equals("org.w3c.dom.Node")
                    && ps[1].getName().equals("org.w3c.dom.Node")
                    && ps[2].getName().equals("java.lang.String")) {
                System.err.println("find it!!!!!!!!!!!!!!!!!");
                c.insertBefore("{return;}");
            }

            if (c.getName().equals("zzZI1")) {
                System.err.println("find it!!!!!!!!!!!!!!!!!" + c.getName());
                c.insertBefore("{return 1;}");
            }

            if (c.getName().equals("zzZI0")) {
                System.err.println("find it!!!!!!!!!!!!!!!!!" + c.getName());
                c.insertBefore("{return 1;}");
            }

        }
        //输出到当前目录下
        c2.writeFile();
    }

    public static void main(String[] args) {
        try {
            AsposeWordsCrack191.changeMethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
