package org.example.canonicalize;

import java.lang.classfile.AccessFlags;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassElement;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassFileVersion;
import java.lang.classfile.ClassModel;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.CustomAttribute;
import java.lang.classfile.FieldModel;
import java.lang.classfile.Interfaces;
import java.lang.classfile.MethodModel;
import java.lang.classfile.Superclass;
import java.lang.classfile.attribute.CompilationIDAttribute;
import java.lang.classfile.attribute.DeprecatedAttribute;
import java.lang.classfile.attribute.EnclosingMethodAttribute;
import java.lang.classfile.attribute.InnerClassesAttribute;
import java.lang.classfile.attribute.ModuleAttribute;
import java.lang.classfile.attribute.ModuleHashesAttribute;
import java.lang.classfile.attribute.ModuleMainClassAttribute;
import java.lang.classfile.attribute.ModulePackagesAttribute;
import java.lang.classfile.attribute.ModuleResolutionAttribute;
import java.lang.classfile.attribute.ModuleTargetAttribute;
import java.lang.classfile.attribute.NestHostAttribute;
import java.lang.classfile.attribute.NestMembersAttribute;
import java.lang.classfile.attribute.PermittedSubclassesAttribute;
import java.lang.classfile.attribute.RecordAttribute;
import java.lang.classfile.attribute.RuntimeInvisibleAnnotationsAttribute;
import java.lang.classfile.attribute.RuntimeInvisibleTypeAnnotationsAttribute;
import java.lang.classfile.attribute.RuntimeVisibleAnnotationsAttribute;
import java.lang.classfile.attribute.RuntimeVisibleTypeAnnotationsAttribute;
import java.lang.classfile.attribute.SignatureAttribute;
import java.lang.classfile.attribute.SourceDebugExtensionAttribute;
import java.lang.classfile.attribute.SourceFileAttribute;
import java.lang.classfile.attribute.SourceIDAttribute;
import java.lang.classfile.attribute.SyntheticAttribute;
import java.lang.classfile.attribute.UnknownAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConstantPoolTransform {

    private static List<Integer> correctOrder = new ArrayList<>();
    private static Map<Integer, Integer> oldIndexToNewIndex = new HashMap<>();

    public static ClassModel transform(ClassModel classModel) {
        // this_class gets index 0 (1 in constant pool)
        correctOrder.add(classModel.thisClass().index());
        establishCorrectOrder(classModel);

        return classModel;
    }

    private static void establishCorrectOrder(ClassModel classModel) {
        ClassFile.of().transform(classModel, new ClassTransform() {

            @Override
            public void accept(ClassBuilder builder, ClassElement element) {
                switch (element) {
                    case AccessFlags accessFlags -> {
                        // Access flags are not in the constant pool
                    }
                    case ClassFileVersion classFileVersion -> {
                        // Class file version is not in the constant pool
                    }
                    case CustomAttribute customAttribute -> {}
                    case FieldModel fieldModel -> {
                        correctOrder.add(fieldModel.fieldName().index());
                        correctOrder.add(fieldModel.fieldType().index());
                    }
                    case Interfaces interfaces -> {
                        correctOrder.addAll(interfaces.interfaces().stream()
                                .map(i -> i.index())
                                .collect(Collectors.toUnmodifiableList()));
                    }
                    case MethodModel methodModel -> {
                        correctOrder.add(methodModel.methodName().index());
                        correctOrder.add(methodModel.methodType().index());

                        //                        methodModel.code().ifPresent(code -> {
                        //                            correctOrder.add(code.elementList());
                        //                        });

                    }
                    case Superclass superclass -> {
                        correctOrder.add(superclass.superclassEntry().index());
                    }
                    case CompilationIDAttribute compilationIDAttribute -> {}
                    case DeprecatedAttribute deprecatedAttribute -> {}
                    case EnclosingMethodAttribute enclosingMethodAttribute -> {}
                    case InnerClassesAttribute innerClassesAttribute -> {}
                    case ModuleAttribute moduleAttribute -> {}
                    case ModuleHashesAttribute moduleHashesAttribute -> {}
                    case ModuleMainClassAttribute moduleMainClassAttribute -> {}
                    case ModulePackagesAttribute modulePackagesAttribute -> {}
                    case ModuleResolutionAttribute moduleResolutionAttribute -> {}
                    case ModuleTargetAttribute moduleTargetAttribute -> {}
                    case NestHostAttribute nestHostAttribute -> {}
                    case NestMembersAttribute nestMembersAttribute -> {}
                    case PermittedSubclassesAttribute permittedSubclassesAttribute -> {}
                    case RecordAttribute recordAttribute -> {}
                    case RuntimeInvisibleAnnotationsAttribute runtimeInvisibleAnnotationsAttribute -> {}
                    case RuntimeInvisibleTypeAnnotationsAttribute runtimeInvisibleTypeAnnotationsAttribute -> {}
                    case RuntimeVisibleAnnotationsAttribute runtimeVisibleAnnotationsAttribute -> {}
                    case RuntimeVisibleTypeAnnotationsAttribute runtimeVisibleTypeAnnotationsAttribute -> {}
                    case SignatureAttribute signatureAttribute -> {}
                    case SourceDebugExtensionAttribute sourceDebugExtensionAttribute -> {}
                    case SourceFileAttribute sourceFileAttribute -> {
                        correctOrder.add(sourceFileAttribute.sourceFile().index());
                    }
                    case SourceIDAttribute sourceIDAttribute -> {}
                    case SyntheticAttribute syntheticAttribute -> {}
                    case UnknownAttribute unknownAttribute -> {}
                }
            }
        });
    }
}
