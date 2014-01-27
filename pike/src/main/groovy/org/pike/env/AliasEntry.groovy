package org.pike.env

import org.pike.model.operatingsystem.Operatingsystem
import org.pike.os.WindowsProvider

/**
 * Created with IntelliJ IDEA.
 * User: OleyMa
 * Date: 03.09.13
 * Time: 12:45
 * To change this template use File | Settings | File Templates.
 */
class AliasEntry implements IEnvEntry{

        String aliasFrom
        String aliasTo

        public AliasEntry (final String aliasFrom, final String aliasTo) {
            this.aliasFrom = aliasFrom
            this.aliasTo = aliasTo
        }

        @Override
        void serialize (Operatingsystem operatingsystem, Collection<String> serializedValue, final boolean global) {
            if (operatingsystem.provider instanceof WindowsProvider)
                throw new IllegalStateException("Alias is not yet implemented in windows")
            serializedValue.add("alias $aliasFrom='$aliasTo'")
        }

        @Override
        String getKey() {
            return "ALIAS" + aliasFrom
        }


}
