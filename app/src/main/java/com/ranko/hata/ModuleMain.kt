package com.ranko.hata

import android.annotation.SuppressLint
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.SystemServerLoadedParam
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import org.apache.commons.lang3.ClassUtils

private lateinit var module: ModuleMain

class ModuleMain(base: XposedInterface, param: ModuleLoadedParam) : XposedModule(base, param) {

    init {
        module = this
    }

    override fun onSystemServerLoaded(param: SystemServerLoadedParam) {
        super.onSystemServerLoaded(param)


    }

    @SuppressLint("PrivateApi")
    override fun onPackageLoaded(param: PackageLoadedParam) {
        super.onPackageLoaded(param)

        if (!param.isFirstPackage) return

        if (param.packageName == "com.android.settings") {
            val loadClass = ClassUtils.getClass(param.classLoader,"com.android.settings.MiuiSettings")
            val loadMethod = loadClass.getDeclaredMethod("updateHeaderList", List::class.java)
            loadMethod.isAccessible = true
            hook(loadMethod, ModuleHook.SettingsHooker::class.java)
        }

        /*
        if (param.packageName == "com.android.systemui") {
            val loadClass = ClassUtils.getClass(param.classLoader,"com.android.systemui.statusbar.policy.SecurityControllerImpl")
            val loadMethod = loadClass.getDeclaredMethod("hasCACertInCurrentUser", Boolean::class.java)
            loadMethod.isAccessible = true
            hook(loadMethod, ModuleHook.SystemUIHooker::class.java)
        }
         */
    }
}
