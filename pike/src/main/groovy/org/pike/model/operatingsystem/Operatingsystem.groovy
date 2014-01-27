package org.pike.model.operatingsystem

import org.pike.common.NamedElement
import org.pike.os.IOperatingsystemProvider

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 17.04.13
 * Time: 00:20
 * To change this template use File | Settings | File Templates.
 */
public class Operatingsystem extends NamedElement{

    String homedir
    String appdir
    String programdir
    String pikedir
    String servicedir
    String cachedir
    String tmpdir
    String pikejre

    String appconfigfile
    String userconfigfile
    String globalconfigfile
    String appdesc   //lin32, lin64, win32, win64, mac32, mac64 to describe download repository position

    String scriptSuffix



    Operatingsystem parent

    IOperatingsystemProvider provider


    private void configureProvider (final String name) {
        String classname = "org.pike.os." + name[0].toUpperCase() + name.substring(1) + "Provider"

        try {
          Class clazz = getClass().getClassLoader().loadClass(classname)
          Object object = clazz.newInstance()

          if (object instanceof IOperatingsystemProvider)
              provider = object as IOperatingsystemProvider
          else
              throw new IllegalStateException("Operatingsystem provider " + classname + " must implement " + IOperatingsystemProvider.class.name)
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("In operatingsystem with name " + name + " a providerclass " + classname + " could not be found")
        } catch (Exception e) {
            throw new IllegalStateException("Operatingsystem provider " + classname + " could not be instantiated. Check if it has a public default constructor")
        }
    }

    /**
     *
     * @param name
     */
    public Operatingsystem (String name) {
        super (name)
        configureProvider(name)
    }

    /**
     * getter
     * @return script suffix ^
     */
    public String getScriptSuffix () {
        if (scriptSuffix != null)
            return scriptSuffix
        else
            return provider.scriptSuffix
    }

    /**
     * getter
     * @return
     */
    public String getSetEnvPrefix() {
        return provider.setEnvPrefix
    }

    /**
     * {@inheritDoc}
     */
    public String toString () {
        String objectAsString = "Operatingsystem <" + name + ">$NEWLINE"
        if (parent != null)
          objectAsString += "    * parent os               : $parent.name $NEWLINE"
        objectAsString += "    * home directory          : $homedir $NEWLINE"
        objectAsString += "    * application directory   : $appdir $NEWLINE"
        objectAsString += "    * program directory       : $programdir $NEWLINE"
        objectAsString += "    * service directory       : $servicedir $NEWLINE"

        objectAsString += "    * pike directory          : $pikedir $NEWLINE"
        objectAsString += "    * pike jre                : $pikejre $NEWLINE"
        objectAsString += "    * cache directory         : $cachedir $NEWLINE"
        objectAsString += "    * app descriptor          : $appdesc $NEWLINE"

        objectAsString += "    * appconfigfile           : $appconfigfile $NEWLINE"
        objectAsString += "    * userconfigfile          : $userconfigfile $NEWLINE"
        objectAsString += "    * globalconfigfile        : $globalconfigfile $NEWLINE$NEWLINE"


        return objectAsString + NEWLINE

    }

}
