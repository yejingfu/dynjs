package org.dynjs.runtime.modules;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.dynjs.runtime.DynJS;
import org.dynjs.runtime.ExecutionContext;

public class ClasspathModuleProvider extends ModuleProvider {

    @Override
    public boolean load(DynJS runtime, ExecutionContext context, String moduleId) {
        ClassLoader classLoader = context.getGlobalObject().getConfig().getClassLoader();
        try {
            InputStream is = classLoader.getResourceAsStream(moduleId);
            if (is == null) {
                throw new FileNotFoundException("Cannot find module: " + moduleId);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            context.getGlobalObject().getRuntime().newRunner().withContext(context).withSource(reader).execute();
            try {
                is.close();
            } catch (IOException ignore) {
            }
            return true;
        }
        catch(Exception e) {
            System.err.println("Module not found: " + moduleId);
        }
        return false;
    }

    @Override
    String generateModuleID(ExecutionContext context, String moduleName) {
        ClassLoader classLoader = context.getGlobalObject().getConfig().getClassLoader();
        String name = normalizeName(moduleName);
        URL moduleURL = classLoader.getResource(name);
        if (moduleURL != null) {
            return name;
        } else {
            moduleURL = classLoader.getResource(moduleName + "/index.js");
            if (moduleURL != null) {
                return moduleName + "/index.js";
            }
        }
        // couldn't find the module in our classpath
        return null;
    }
    
}