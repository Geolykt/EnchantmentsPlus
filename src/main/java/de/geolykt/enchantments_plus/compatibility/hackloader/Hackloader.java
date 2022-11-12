/*
 * This file is part of EnchantmentsPlus, a bukkit plugin.
 * Copyright (c) 2015 - 2020 Zedly and Zenchantments contributors.
 * Copyright (c) 2020 - 2022 Geolykt and EnchantmentsPlus contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.geolykt.enchantments_plus.compatibility.hackloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.UnsafeValues;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.geolykt.starloader.deobf.DescString;

import sun.misc.Unsafe;

/**
 * The Hackloader is a tool to modify the bytecode of other plugins at runtime.
 *
 * @since 4.1.0
 */
public class Hackloader {

    public static void injectHackloader() throws HackloaderInjectionException {
        ClassNode unsafeItf;
        try {
            unsafeItf = nodeFromClass(UnsafeValues.class);
        } catch (IOException e) {
            throw new HackloaderInjectionException("Unable to get the bytecode of the UnsafeValues interface", e);
        }

        ClassNode hackedUnsafe = new ClassNode();
        hackedUnsafe.superName = "java/lang/Object";
        hackedUnsafe.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER;
        hackedUnsafe.version = Opcodes.V16;
        hackedUnsafe.name = "de/geolykt/enchantments_plus/compatibility/hackloader/HackedUnsafeValues";
        hackedUnsafe.interfaces.add(unsafeItf.name);

        MethodNode constructor = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "(L" + unsafeItf.name + ";)V", null, null);
        hackedUnsafe.methods.add(constructor);
        FieldNode pristineUnsafe = new FieldNode(Opcodes.ACC_PUBLIC, "pristineUnsafe", "L" + unsafeItf.name + ";", null, null);
        hackedUnsafe.fields.add(pristineUnsafe);

        constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // ALOAD this
        constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // ALOAD server
        constructor.instructions.add(new FieldInsnNode(Opcodes.PUTFIELD, hackedUnsafe.name, pristineUnsafe.name, pristineUnsafe.desc));
        constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0)); // ALOAD this
        constructor.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V"));
        constructor.instructions.add(new InsnNode(Opcodes.RETURN));

        for (MethodNode unsafeInterfaceMethod : unsafeItf.methods) {
            MethodNode implMethod = new MethodNode(Opcodes.ACC_PUBLIC, unsafeInterfaceMethod.name, unsafeInterfaceMethod.desc, unsafeInterfaceMethod.signature, unsafeInterfaceMethod.exceptions.toArray(new String[0]));

            implMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            implMethod.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, hackedUnsafe.name, pristineUnsafe.name, pristineUnsafe.desc));

            DescString descString = new DescString(unsafeInterfaceMethod.desc);

            int localIndex = 0;
            while (descString.hasNext()) {
                String type = descString.nextType();
                int opcode;
                boolean wide = false;
                switch (type.codePointAt(0)) {
                case 'L':
                case '[':
                    opcode = Opcodes.ALOAD;
                    break;
                case 'I':
                case 'Z':
                case 'S':
                case 'C':
                case 'B':
                    opcode = Opcodes.ILOAD;
                    break;
                case 'J':
                    opcode = Opcodes.LLOAD;
                    wide = true;
                    break;
                case 'F':
                    opcode = Opcodes.FLOAD;
                    break;
                case 'D':
                    opcode = Opcodes.DLOAD;
                    wide = true;
                    break;
                default:
                    throw new HackloaderInjectionException("Unknown type: " + type.codePointAt(0));
                }
                implMethod.instructions.add(new VarInsnNode(opcode, ++localIndex));
                if (wide) {
                    localIndex++;
                }
            }

            String returnType = unsafeInterfaceMethod.desc.substring(unsafeInterfaceMethod.desc.lastIndexOf(')') + 1);

            int returnOpcode;
            switch (returnType.codePointAt(0)) {
            case 'L':
            case '[':
                returnOpcode = Opcodes.ARETURN;
                break;
            case 'I':
            case 'Z':
            case 'S':
            case 'C':
            case 'B':
                returnOpcode = Opcodes.IRETURN;
                break;
            case 'J':
                returnOpcode = Opcodes.LRETURN;
                break;
            case 'F':
                returnOpcode = Opcodes.FRETURN;
                break;
            case 'D':
                returnOpcode = Opcodes.DRETURN;
                break;
            case 'V':
                returnOpcode = Opcodes.RETURN;
                break;
            default:
                throw new HackloaderInjectionException("Unknown return type: " + returnType);
            }

            implMethod.maxLocals = localIndex;
            implMethod.maxStack = localIndex;

            implMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, unsafeItf.name, unsafeInterfaceMethod.name, unsafeInterfaceMethod.desc));

            String pdfDesc = Type.getDescriptor(PluginDescriptionFile.class);
            if (implMethod.name.equals("processClass") && implMethod.desc.equals("(" + pdfDesc + "Ljava/lang/String;[B)[B")) {
                implMethod.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                implMethod.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, Type.getInternalName(Hackloader.class), "transform", "([BLjava/lang/String;)[B"));
            }

            implMethod.instructions.add(new InsnNode(returnOpcode));

            hackedUnsafe.methods.add(implMethod);
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        hackedUnsafe.accept(writer);
        byte[] hackedBytes = writer.toByteArray();
        Class<?> hackUnsafeClassInstance;

        try {
            hackUnsafeClassInstance = MethodHandles.lookup().defineClass(hackedBytes);
        } catch (Exception e) {
            throw new HackloaderInjectionException("Could not define the HackedUnsafeValues class!", e);
        }

        Object hackUnsafeInstance;
        try {
            hackUnsafeInstance = hackUnsafeClassInstance.getConstructor(UnsafeValues.class).newInstance(Bukkit.getUnsafe());
        } catch (Exception e) {
            throw new HackloaderInjectionException("Could not obtain an instance of the HackedUnsafeValues class!", e);
        }

        try {
            // Now we use the big Unsafe to set the little unsafe
            Field bukkitUnsafeInstanceField = Bukkit.getUnsafe().getClass().getDeclaredField("INSTANCE");
            Field jvmUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            jvmUnsafeField.setAccessible(true);
            Unsafe jvmUnsafe = (Unsafe) jvmUnsafeField.get(null);
            jvmUnsafeField.setAccessible(false);
            Object fieldBase = jvmUnsafe.staticFieldBase(bukkitUnsafeInstanceField);
            long fieldOffset = jvmUnsafe.staticFieldOffset(bukkitUnsafeInstanceField);
            jvmUnsafe.putObject(fieldBase, fieldOffset, hackUnsafeInstance);
        } catch (Exception e) {
            throw new HackloaderInjectionException("Couldn't set the HackedUnsafeValues as the UnsafeValues impl", e);
        }
    }

    // based on https://github.com/Geolykt/Slimefun4/commit/36992c82cba65bd3d52bdb706dadc99416ac4be0
    static byte[] transformAutoDisenchanter(byte[] source) {
        ClassNode node = new ClassNode();
        ClassReader reader = new ClassReader(source);
        reader.accept(node, 0);

        boolean foundDisenchatMethod = false;
        for (MethodNode method : node.methods) {

            if (method.name.equals("disenchant")) {
                if (foundDisenchatMethod) {
                    getLogger().error("[Hackloader] Unable to transform {} because multiple disenchant methods were found.", node.name);
                    return source; // Void all modifications
                }
                foundDisenchatMethod = true;
                AbstractInsnNode insn = method.instructions.getFirst();
                MethodInsnNode isEmptyCall = null;
                MethodInsnNode transferEnchantmentsCall = null;
                MethodInsnNode sizeCall = null;
                while (insn != null) {
                    if (insn.getOpcode() == Opcodes.INVOKEINTERFACE) {
                        MethodInsnNode methodInsn = (MethodInsnNode) insn;
                        if (methodInsn.owner.equals("java/util/Map") && methodInsn.desc.equals("()Z") && methodInsn.name.equals("isEmpty")) {
                            if (isEmptyCall != null) {
                                getLogger().error("[Hackloader] Unable to transform {} because multiple isEmpty method calls were found.", node.name);
                                return source;
                            }
                            isEmptyCall = methodInsn;
                        } else if (methodInsn.owner.equals("java/util/Map") && methodInsn.desc.equals("()I") && methodInsn.name.equals("size")) {
                            if (sizeCall != null) {
                                getLogger().error("[Hackloader] Unable to transform {} because multiple size method calls were found.", node.name);
                                return source;
                            }
                            sizeCall = methodInsn;
                        }
                    } else if (insn.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsn = (MethodInsnNode) insn;
                        if (methodInsn.owner.equals(node.name) && methodInsn.name.equals("transferEnchantments")) {
                            if (transferEnchantmentsCall != null) {
                                getLogger().error("[Hackloader] Unable to transform {} because multiple transferEnchantments method calls were found.", node.name);
                                return source;
                            }
                            transferEnchantmentsCall = methodInsn;
                        }
                    }
                    insn = insn.getNext();
                }

                if (isEmptyCall == null) {
                    getLogger().error("[Hackloader] Unable to transform {} because no isEmpty method calls were found.", node.name);
                    return source;
                }
                if (sizeCall == null) {
                    getLogger().error("[Hackloader] Unable to transform {} because no size method calls were found.", node.name);
                    return source;
                }
                if (transferEnchantmentsCall == null) {
                    getLogger().error("[Hackloader] Unable to transform {} because no transferEnchantments method calls were found.", node.name);
                    return source;
                }

                AbstractInsnNode aloadEnchs = isEmptyCall.getPrevious();
                if (aloadEnchs.getOpcode() != Opcodes.ALOAD) {
                    getLogger().error("[Hackloader] ALOAD enchantments not ALOAD");
                    return source;
                }
                int enchCountLocal = method.maxLocals++;
                method.instructions.insertBefore(aloadEnchs, new InsnNode(Opcodes.ICONST_0));
                method.instructions.insertBefore(aloadEnchs, new VarInsnNode(Opcodes.ISTORE, enchCountLocal));

                AbstractInsnNode jumpReturnNull = isEmptyCall.getNext();
                if (jumpReturnNull.getOpcode() != Opcodes.IFNE) {
                    getLogger().error("[Hackloader] IFNE not IFNE");
                    return source;
                }

                LabelNode doVanillaLabel = null;
                insn = jumpReturnNull.getNext();
                while (insn != null) {
                    if (insn instanceof LabelNode ln) {
                        doVanillaLabel = ln;
                        break;
                    }
                    insn = insn.getNext();
                }

                if (doVanillaLabel == null) {
                    getLogger().error("[Hackloader] doVanillaLabel not found as method ended prematurely");
                    return source;
                }

                JumpInsnNode replacementJump = new JumpInsnNode(Opcodes.IFEQ, doVanillaLabel);
                method.instructions.insert(jumpReturnNull, replacementJump);
                method.instructions.remove(jumpReturnNull);

                VarInsnNode firstDisenchatedItemStore = null;
                VarInsnNode firstEnchantedBookStore = null;

                for (insn = doVanillaLabel; insn != null; insn = insn.getNext()) {
                    if (insn instanceof MethodInsnNode methodInsn && methodInsn.owner.equals("org/bukkit/inventory/ItemStack") && methodInsn.name.equals("clone")) {
                        if (insn.getNext().getOpcode() != Opcodes.ASTORE) {
                            getLogger().error("[Hackloader] Cloned itemstack not stored.");
                            return source;
                        }
                        if (firstDisenchatedItemStore != null) {
                            getLogger().error("[Hackloader] Lone itemstack clone not alone.");
                            return source;
                        }
                        firstDisenchatedItemStore = (VarInsnNode) insn.getNext();
                    } else if (insn instanceof FieldInsnNode fieldInsn && fieldInsn.owner.equals(Type.getInternalName(Material.class)) && fieldInsn.name.equals("ENCHANTED_BOOK")) {
                        if (insn.getPrevious().getOpcode() != Opcodes.DUP || insn.getPrevious().getPrevious().getOpcode() != Opcodes.NEW
                                || insn.getNext().getOpcode() != Opcodes.INVOKESPECIAL || insn.getNext().getNext().getOpcode() != Opcodes.ASTORE) {
                            continue;
                        }
                        if (firstEnchantedBookStore != null) {
                            getLogger().error("[Hackloader] Lone enchanted book not alone.");
                            return source;
                        }
                        firstEnchantedBookStore = (VarInsnNode) insn.getNext().getNext();
                    }
                }

                if (firstDisenchatedItemStore == null) {
                    getLogger().error("[Hackloader] Itemstack not cloned");
                    return source;
                }
                if (firstEnchantedBookStore == null) {
                    getLogger().error("[Hackloader] No enchanted book itemstack");
                    return source;
                }

                // Rewrite/Deduplicate index of the disenchatedItem and enchatedBook
                int oldDisenchantedItemLocal = firstDisenchatedItemStore.var;
                int oldEnchantedBookItemLocal = firstEnchantedBookStore.var;
                int disenchantedItemLocal = method.maxLocals++;
                int enchantedBookItemLocal = method.maxLocals++;
                boolean beginEnchantedBookRewrite = false;
                boolean beginDisenchantedItemRewrite = false;

                for (insn = doVanillaLabel; insn != null; insn = insn.getNext()) {
                    if (insn.getOpcode() == Opcodes.ASTORE || insn.getOpcode() == Opcodes.ALOAD) {
                        if (insn == firstDisenchatedItemStore) {
                            beginDisenchantedItemRewrite = true;
                        } else if (insn == firstEnchantedBookStore) {
                            beginEnchantedBookRewrite = true;
                        }
                        if (((VarInsnNode) insn).var == oldDisenchantedItemLocal && beginDisenchantedItemRewrite) {
                            ((VarInsnNode) insn).var = disenchantedItemLocal;
                        } else if (((VarInsnNode) insn).var == oldEnchantedBookItemLocal && beginEnchantedBookRewrite) {
                            ((VarInsnNode) insn).var = enchantedBookItemLocal;
                        }
                    }
                }

                LabelNode firstLabel = null;
                LabelNode lastLabel = null;

                for (insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                    if (insn instanceof LabelNode label) {
                        if (firstLabel == null) {
                            firstLabel = label;
                        }
                        lastLabel = label;
                    }
                }

                if (firstLabel == null) {
                    getLogger().error("[Hackloader] Unable to obtain first label in method");
                    return source;
                }

                // Write deduplicated locals to debug
                method.localVariables.add(new LocalVariableNode("dedup_disenchantedItem", Type.getDescriptor(ItemStack.class), null, firstLabel, lastLabel, disenchantedItemLocal));
                method.localVariables.add(new LocalVariableNode("dedup_enchantedBook", Type.getDescriptor(ItemStack.class), null, firstLabel, lastLabel, enchantedBookItemLocal));

                // Write null to deduplicated locals in order to proof our provided ranges
                method.instructions.insert(firstLabel, new VarInsnNode(Opcodes.ASTORE, enchantedBookItemLocal));
                method.instructions.insert(firstLabel, new InsnNode(Opcodes.ACONST_NULL));
                method.instructions.insert(firstLabel, new VarInsnNode(Opcodes.ASTORE, disenchantedItemLocal));
                method.instructions.insert(firstLabel, new InsnNode(Opcodes.ACONST_NULL));

                LabelNode postTransferEnchs = new LabelNode();
                method.instructions.insert(transferEnchantmentsCall, postTransferEnchs);

                InsnList noVanilla = new InsnList();
                noVanilla.add(new VarInsnNode(Opcodes.ALOAD, 1)); // ALOAD menu
                noVanilla.add(new VarInsnNode(Opcodes.ALOAD, 2)); // ALOAD item
                noVanilla.add(new MethodInsnNode(Opcodes.INVOKESTATIC, SlimefunCallbacks.INTERNAL_NAME, "autoDisenchanter$noVanillaEnchantments", "(Lme/mrCookieSlime/Slimefun/api/inventory/BlockMenu;Lorg/bukkit/inventory/ItemStack;)[Ljava/lang/Object;"));

                // disenchatedItemResult
                noVanilla.add(new InsnNode(Opcodes.DUP));
                noVanilla.add(new InsnNode(Opcodes.ICONST_0));
                noVanilla.add(new InsnNode(Opcodes.AALOAD));
                noVanilla.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(ItemStack.class)));
                noVanilla.add(new VarInsnNode(Opcodes.ASTORE, disenchantedItemLocal));
                noVanilla.add(new VarInsnNode(Opcodes.ALOAD, disenchantedItemLocal));
                noVanilla.add(new JumpInsnNode(Opcodes.IFNULL, ((JumpInsnNode) jumpReturnNull).label));

                // enchatedBookResult
                noVanilla.add(new InsnNode(Opcodes.DUP));
                noVanilla.add(new InsnNode(Opcodes.ICONST_1));
                noVanilla.add(new InsnNode(Opcodes.AALOAD));
                noVanilla.add(new TypeInsnNode(Opcodes.CHECKCAST, Type.getInternalName(ItemStack.class)));
                noVanilla.add(new VarInsnNode(Opcodes.ASTORE, enchantedBookItemLocal));

                // enchantmentCountResult
                noVanilla.add(new InsnNode(Opcodes.ICONST_2));
                noVanilla.add(new InsnNode(Opcodes.AALOAD));
                noVanilla.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Integer"));
                noVanilla.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I"));
                noVanilla.add(new VarInsnNode(Opcodes.ISTORE, enchCountLocal));
                noVanilla.add(new JumpInsnNode(Opcodes.GOTO, postTransferEnchs));

                method.instructions.insert(replacementJump, noVanilla);

                method.instructions.insert(sizeCall, new InsnNode(Opcodes.IADD));
                method.instructions.insert(sizeCall, new VarInsnNode(Opcodes.ILOAD, enchCountLocal));

                InsnList vanilla = new InsnList();
                vanilla.add(new VarInsnNode(Opcodes.ALOAD, 1)); // ALOAD menu
                vanilla.add(new VarInsnNode(Opcodes.ALOAD, disenchantedItemLocal)); // ALOAD disenchantedItem
                vanilla.add(new VarInsnNode(Opcodes.ALOAD, enchantedBookItemLocal)); // ALOAD enchantedBook
                vanilla.add(new MethodInsnNode(Opcodes.INVOKESTATIC, SlimefunCallbacks.INTERNAL_NAME, "autoDisenchanter$vanillEnchs", "(Lme/mrCookieSlime/Slimefun/api/inventory/BlockMenu;Lorg/bukkit/inventory/ItemStack;Lorg/bukkit/inventory/ItemStack;)I"));
                vanilla.add(new VarInsnNode(Opcodes.ISTORE, enchCountLocal)); // ISTORE enchCount

                method.instructions.insert(transferEnchantmentsCall, vanilla);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        node.accept(writer);
        try (FileOutputStream fos = new FileOutputStream(new File("AutoDisenchanter.class"))) {
            fos.write(writer.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toByteArray();
    }

    static byte[] transform(byte[] bytes, String clazzName) {
        if (clazzName.equals("io/github/thebusybiscuit/slimefun4/implementation/items/electric/machines/enchanting/AutoDisenchanter.class")) {
            getLogger().info("[Hackloader] Transforming {}.", clazzName);
            return transformAutoDisenchanter(bytes);
        }
        return bytes;
    }

    private static ClassNode nodeFromClass(Class<?> cl) throws IOException {
        ClassNode node = new ClassNode();
        String fileName = cl.getName().replace('.', '/') + ".class";
        ClassLoader classlaoder = cl.getClassLoader();
        InputStream in = classlaoder.getResourceAsStream(fileName);
        if (in == null) {
            throw new IOException("Unable to get \"" + fileName + "\" from classloader \"" + classlaoder.getName() + "\"");
        }
        ClassReader reader = new ClassReader(in);
        reader.accept(node, 0);
        in.close();
        return node;
    }

    private static Logger getLogger() {
        return LoggerFactory.getLogger("Enchantments_plus");
    }
}
