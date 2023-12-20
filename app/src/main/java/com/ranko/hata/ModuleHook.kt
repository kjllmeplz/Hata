package com.ranko.hata

import android.annotation.SuppressLint
import android.app.Activity
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.annotations.AfterInvocation
import io.github.libxposed.api.annotations.XposedHooker

class ModuleHook {
    @XposedHooker
    class SettingsHooker : XposedInterface.Hooker {
        companion object {
            @SuppressLint("DiscouragedApi", "ResourceType")
            @Suppress("UNCHECKED_CAST")
            @JvmStatic
            @AfterInvocation
            fun after(param: XposedInterface.AfterHookCallback) {
                val mContext = (param.thisObject as Activity).baseContext

                //Build remove menu lists
                val removeIds = ArrayList<Int>()
                for (id in ModuleConst.MenuRemoveLists) {
                    removeIds.add(mContext.resources.getIdentifier(id, "id", mContext.packageName))
                }

                //Build move menu lists
                val moveIds = ArrayList<Int>()
                for (id in ModuleConst.MenuMoveLists) {
                    moveIds.add(mContext.resources.getIdentifier(id, "id", mContext.packageName))
                }

                val headers = param.args[0] as MutableList<Any>
                val removeIDs = ArrayList<Int>()
                val moveMenus = ArrayList<Any?>()

                for ((i, head) in headers.withIndex()) {
                    //Get list ID remove
                    val fieldID = head.javaClass.getDeclaredField("id")
                    for (id in removeIds) {
                        if (fieldID.getLong(head) == id.toLong()) {
                            removeIDs.add(i)
                            break
                        }
                    }

                    //Get list ID move
                    for (id in moveIds) {
                        if (fieldID.getLong(head) == id.toLong()) {
                            removeIDs.add(i)
                            moveMenus.add(head)
                            break
                        }
                    }
                }

                //Remove menu
                if (removeIDs.isNotEmpty()) {
                    removeIDs.sortDescending()
                    for (removeID in removeIDs) headers.removeAt(removeID)
                }

                //Re-add others
                for (menuID in moveMenus) headers.add(menuID!!)

                module.log("Rearrange menu success!")
            }
        }
    }
}