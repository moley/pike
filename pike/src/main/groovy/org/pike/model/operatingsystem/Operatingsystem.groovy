package org.pike.model.operatingsystem

import org.gradle.internal.reflect.Instantiator
import org.pike.autoinitializer.NoAutoInitializing
import org.pike.common.NamedElement
import org.pike.os.IOperatingsystemProvider

/**
 * Class that configures a concrete operatingsystem instance
 */
public class Operatingsystem extends NamedElement{

    String homedir
    String appdir
    String programdir
    String pikedir
    String servicedir

    /**
     * jre that is used for pike
     */
    String pikejre32
    String pikejre64

    String appconfigfile
    String userconfigfile
    String globalconfigfile

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
    public Operatingsystem (String name, Instantiator instantiator = null) {
        super (name, instantiator)
        configureProvider(name)
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
        objectAsString += "    * pike jre 32 bit         : $pikejre32 $NEWLINE"
        objectAsString += "    * pike jre 64 bit         : $pikejre64 $NEWLINE"

        objectAsString += "    * appconfigfile           : $appconfigfile $NEWLINE"
        objectAsString += "    * userconfigfile          : $userconfigfile $NEWLINE"
        objectAsString += "    * globalconfigfile        : $globalconfigfile $NEWLINE$NEWLINE"


        return objectAsString + NEWLINE

    }

}
