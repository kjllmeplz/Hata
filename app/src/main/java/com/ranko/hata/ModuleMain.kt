package com.ranko.hata

import android.annotation.SuppressLint
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface.ModuleLoadedParam
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam
import io.github.libxposed.api.XposedModuleInterface.SystemServerLoadedParam
import org.apache.commons.lang3.ClassUtils
import java.lang.reflect.Method

internal lateinit var module: ModuleMain

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

        if (param.packageName == ModuleConst.SettingsPackage) {
            hook(getMethod(param, "${ModuleConst.SettingsPackage}.MiuiSettings", "updateHeaderList", List::class.java),
                ModuleHook.SettingsHooker::class.java)
        }
    }

    private fun getMethod(param: PackageLoadedParam, className: String, methodName: String, parameterTypes: Class<*>?): Method {
        val loadClass = ClassUtils.getClass(param.classLoader, className)
        val loadMethod: Method = if (parameterTypes != null) {
            loadClass.getMethod(methodName, parameterTypes)
        } else {
            loadClass.getMethod(methodName)
        }
        loadMethod.isAccessible = true
        return loadMethod
    }
}
