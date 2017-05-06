package by.cs.web;


import org.eclipse.jetty.webapp.WebAppContext;

import java.util.Map;

/**
 * @author Dmitriy V.Yefremov
 */
public class AliasAppContext extends WebAppContext {

    public AliasAppContext() {

    }

    public AliasAppContext(String webApp, String contextPath) {
        super(webApp, contextPath);
    }

    @Override
    public String getResourceAlias(String alias) {

        @SuppressWarnings("unchecked")
        Map<String, String> resourceAliases =  getResourceAliases();

        if (resourceAliases == null) {
            return null;
        }

        for (Map.Entry<String, String> oneAlias :
                resourceAliases.entrySet()) {

            if (alias.startsWith(oneAlias.getKey())) {
                return alias.replace(oneAlias.getKey(), oneAlias.getValue());
            }
        }

        return null;
    }

}
