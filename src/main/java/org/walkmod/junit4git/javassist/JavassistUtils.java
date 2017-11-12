package org.walkmod.junit4git.javassist;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import org.walkmod.junit4git.core.reports.TestMethodReport;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JavassistUtils {

  public void annotateMethod(Class<?> annotationClass,
                             String md, CtClass clazz, ConstPool constpool) {
    try {
      CtMethod method = clazz.getDeclaredMethod(md);

      AnnotationsAttribute attr = (AnnotationsAttribute) method.getMethodInfo()
              .getAttribute(AnnotationsAttribute.visibleTag);

      attr.addAnnotation(new Annotation(annotationClass.getName(), constpool));
      method.getMethodInfo().addAttribute(attr);
    } catch (NotFoundException e) {
      //the method has been removed
    } catch (Exception e) {
      throw new RuntimeException("Error adding @" + annotationClass.getName() + " annotations", e);
    }
  }

  public void annotateMethods(Class<?> annotationClass, Instrumentation inst, Map<String,
          List<TestMethodReport>> testsToMap)
          throws IOException, CannotCompileException, UnmodifiableClassException {
    annotateMethods(annotationClass, inst, ClassPool.getDefault(), testsToMap);
  }

  public void annotateMethods(Class<?> annotationClass, Instrumentation inst,
                              ClassPool pool, Map<String, List<TestMethodReport>> testsToMap)
          throws IOException, CannotCompileException, UnmodifiableClassException {

    Iterator<String> it = testsToMap.keySet().iterator();
    while (it.hasNext()) {
      String className = it.next();
      try {
        CtClass clazz = pool.get(className);
        ClassFile ccFile = clazz.getClassFile();
        ConstPool constpool = ccFile.getConstPool();
        testsToMap.get(className).stream()
                .map(TestMethodReport::getTestMethod)
                .forEach(md -> annotateMethod(annotationClass, md, clazz, constpool));
        inst.redefineClasses(new ClassDefinition(Class.forName(className), clazz.toBytecode()));
      } catch (NotFoundException | ClassNotFoundException e) {
        //the class has been removed
      }
    }
  }
}
