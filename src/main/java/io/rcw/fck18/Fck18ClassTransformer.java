package io.rcw.fck18;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Fck18ClassTransformer implements IClassTransformer, Opcodes {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        boolean obfuscated = !name.equals(transformedName);

        // Using mappings stable_20/stable_22
        String methodDesc = obfuscated ? "(Lpr;)Z" : "(Lnet/minecraft/entity/EntityLivingBase;)Z";
        String methodName = obfuscated ? "a" : "canRenderName";

        if (transformedName.equals("net.minecraft.client.renderer.entity.RendererLivingEntity")) {
            ClassNode node = new ClassNode();
            ClassReader classReader = new ClassReader(basicClass);
            classReader.accept(node, 0);

            for (MethodNode methodNode : node.methods) {
                if (methodNode.name.equals(methodName) && methodNode.desc.equals(methodDesc)) {
                    /* clear the original instructions */
                    methodNode.instructions.clear(); // clear the instructions
                    /*
                       Replace with
                       `return Hooks.canRenderName(entity);`
                     */
                    methodNode.visitCode();
                    {
                        methodNode.visitVarInsn(ALOAD, 1);  // Load the entity reference from the stack
                        methodNode.visitMethodInsn(INVOKESTATIC, "io/rcw/fck18/Hooks", "canRenderName", methodDesc, false); // Hooks.canRenderName()
                        methodNode.visitInsn(IRETURN); // return Hooks.canRenderName(entity);
                        methodNode.visitMaxs(1, 1);
                    }
                    methodNode.visitEnd();
                }
            }

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            return writer.toByteArray();
        }
        return basicClass;
    }
}
