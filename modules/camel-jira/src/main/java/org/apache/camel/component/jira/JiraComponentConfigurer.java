//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.apache.camel.component.jira;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.GeneratedPropertyConfigurer;
import org.apache.camel.spi.PropertyConfigurerGetter;
import org.apache.camel.support.component.PropertyConfigurerSupport;

public class JiraComponentConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {
    public JiraComponentConfigurer() {
    }

    private JiraConfiguration getOrCreateConfiguration(JiraComponent target) {
        if (target.getConfiguration() == null) {
            target.setConfiguration(new JiraConfiguration());
        }

        return target.getConfiguration();
    }

    public boolean configure(CamelContext camelContext, Object obj, String name, Object value, boolean ignoreCase) {
        JiraComponent target = (JiraComponent)obj;
        String var7 = ignoreCase ? name.toLowerCase() : name;
        byte var8 = -1;
        switch(var7.hashCode()) {
            case -1876070948:
                if (var7.equals("privateKey")) {
                    var8 = 16;
                }
                break;
            case -1876040196:
                if (var7.equals("privatekey")) {
                    var8 = 15;
                }
                break;
            case -1730917813:
                if (var7.equals("bridgeerrorhandler")) {
                    var8 = 4;
                }
                break;
            case -1594540319:
                if (var7.equals("jiraUrl")) {
                    var8 = 11;
                }
                break;
            case -1594509567:
                if (var7.equals("jiraurl")) {
                    var8 = 10;
                }
                break;
            case -1042689291:
                if (var7.equals("accessToken")) {
                    var8 = 1;
                }
                break;
            case -1013136619:
                if (var7.equals("accesstoken")) {
                    var8 = 0;
                }
                break;
            case -858417527:
                if (var7.equals("consumerKey")) {
                    var8 = 8;
                }
                break;
            case -858386775:
                if (var7.equals("consumerkey")) {
                    var8 = 7;
                }
                break;
            case -785459983:
                if (var7.equals("autowiredEnabled")) {
                    var8 = 3;
                }
                break;
            case -600261888:
                if (var7.equals("lazystartproducer")) {
                    var8 = 12;
                }
                break;
            case -265713450:
                if (var7.equals("username")) {
                    var8 = 17;
                }
                break;
            case 95467907:
                if (var7.equals("delay")) {
                    var8 = 9;
                }
                break;
            case 98055755:
                if (var7.equals("bridgeErrorHandler")) {
                    var8 = 5;
                }
                break;
            case 263343872:
                if (var7.equals("lazyStartProducer")) {
                    var8 = 13;
                }
                break;
            case 642216616:
                if (var7.equals("verificationCode")) {
                    var8 = 19;
                }
                break;
            case 643169928:
                if (var7.equals("verificationcode")) {
                    var8 = 18;
                }
                break;
            case 1216985755:
                if (var7.equals("password")) {
                    var8 = 14;
                }
                break;
            case 1844854033:
                if (var7.equals("autowiredenabled")) {
                    var8 = 2;
                }
                break;
            case 1932752118:
                if (var7.equals("configuration")) {
                    var8 = 6;
                }
        }

        switch(var8) {
            case 0:
            case 1:
                this.getOrCreateConfiguration(target).setAccessToken((String)property(camelContext, String.class, value));
                return true;
            case 2:
            case 3:
                target.setAutowiredEnabled((Boolean)property(camelContext, Boolean.TYPE, value));
                return true;
            case 4:
            case 5:
                target.setBridgeErrorHandler((Boolean)property(camelContext, Boolean.TYPE, value));
                return true;
            case 6:
                target.setConfiguration((JiraConfiguration)property(camelContext, JiraConfiguration.class, value));
                return true;
            case 7:
            case 8:
                this.getOrCreateConfiguration(target).setConsumerKey((String)property(camelContext, String.class, value));
                return true;
            case 9:
                this.getOrCreateConfiguration(target).setDelay((Integer)property(camelContext, Integer.class, value));
                return true;
            case 10:
            case 11:
                this.getOrCreateConfiguration(target).setJiraUrl((String)property(camelContext, String.class, value));
                return true;
            case 12:
            case 13:
                target.setLazyStartProducer((Boolean)property(camelContext, Boolean.TYPE, value));
                return true;
            case 14:
                this.getOrCreateConfiguration(target).setPassword((String)property(camelContext, String.class, value));
                return true;
            case 15:
            case 16:
                this.getOrCreateConfiguration(target).setPrivateKey((String)property(camelContext, String.class, value));
                return true;
            case 17:
                this.getOrCreateConfiguration(target).setUsername((String)property(camelContext, String.class, value));
                return true;
            case 18:
            case 19:
                this.getOrCreateConfiguration(target).setVerificationCode((String)property(camelContext, String.class, value));
                return true;
            default:
                return false;
        }
    }

    public Class<?> getOptionType(String name, boolean ignoreCase) {
        String var3 = ignoreCase ? name.toLowerCase() : name;
        byte var4 = -1;
        switch(var3.hashCode()) {
            case -1876070948:
                if (var3.equals("privateKey")) {
                    var4 = 16;
                }
                break;
            case -1876040196:
                if (var3.equals("privatekey")) {
                    var4 = 15;
                }
                break;
            case -1730917813:
                if (var3.equals("bridgeerrorhandler")) {
                    var4 = 4;
                }
                break;
            case -1594540319:
                if (var3.equals("jiraUrl")) {
                    var4 = 11;
                }
                break;
            case -1594509567:
                if (var3.equals("jiraurl")) {
                    var4 = 10;
                }
                break;
            case -1042689291:
                if (var3.equals("accessToken")) {
                    var4 = 1;
                }
                break;
            case -1013136619:
                if (var3.equals("accesstoken")) {
                    var4 = 0;
                }
                break;
            case -858417527:
                if (var3.equals("consumerKey")) {
                    var4 = 8;
                }
                break;
            case -858386775:
                if (var3.equals("consumerkey")) {
                    var4 = 7;
                }
                break;
            case -785459983:
                if (var3.equals("autowiredEnabled")) {
                    var4 = 3;
                }
                break;
            case -600261888:
                if (var3.equals("lazystartproducer")) {
                    var4 = 12;
                }
                break;
            case -265713450:
                if (var3.equals("username")) {
                    var4 = 17;
                }
                break;
            case 95467907:
                if (var3.equals("delay")) {
                    var4 = 9;
                }
                break;
            case 98055755:
                if (var3.equals("bridgeErrorHandler")) {
                    var4 = 5;
                }
                break;
            case 263343872:
                if (var3.equals("lazyStartProducer")) {
                    var4 = 13;
                }
                break;
            case 642216616:
                if (var3.equals("verificationCode")) {
                    var4 = 19;
                }
                break;
            case 643169928:
                if (var3.equals("verificationcode")) {
                    var4 = 18;
                }
                break;
            case 1216985755:
                if (var3.equals("password")) {
                    var4 = 14;
                }
                break;
            case 1844854033:
                if (var3.equals("autowiredenabled")) {
                    var4 = 2;
                }
                break;
            case 1932752118:
                if (var3.equals("configuration")) {
                    var4 = 6;
                }
        }

        switch(var4) {
            case 0:
            case 1:
                return String.class;
            case 2:
            case 3:
                return Boolean.TYPE;
            case 4:
            case 5:
                return Boolean.TYPE;
            case 6:
                return JiraConfiguration.class;
            case 7:
            case 8:
                return String.class;
            case 9:
                return Integer.class;
            case 10:
            case 11:
                return String.class;
            case 12:
            case 13:
                return Boolean.TYPE;
            case 14:
                return String.class;
            case 15:
            case 16:
                return String.class;
            case 17:
                return String.class;
            case 18:
            case 19:
                return String.class;
            default:
                return null;
        }
    }

    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        JiraComponent target = (JiraComponent)obj;
        String var5 = ignoreCase ? name.toLowerCase() : name;
        byte var6 = -1;
        switch(var5.hashCode()) {
            case -1876070948:
                if (var5.equals("privateKey")) {
                    var6 = 16;
                }
                break;
            case -1876040196:
                if (var5.equals("privatekey")) {
                    var6 = 15;
                }
                break;
            case -1730917813:
                if (var5.equals("bridgeerrorhandler")) {
                    var6 = 4;
                }
                break;
            case -1594540319:
                if (var5.equals("jiraUrl")) {
                    var6 = 11;
                }
                break;
            case -1594509567:
                if (var5.equals("jiraurl")) {
                    var6 = 10;
                }
                break;
            case -1042689291:
                if (var5.equals("accessToken")) {
                    var6 = 1;
                }
                break;
            case -1013136619:
                if (var5.equals("accesstoken")) {
                    var6 = 0;
                }
                break;
            case -858417527:
                if (var5.equals("consumerKey")) {
                    var6 = 8;
                }
                break;
            case -858386775:
                if (var5.equals("consumerkey")) {
                    var6 = 7;
                }
                break;
            case -785459983:
                if (var5.equals("autowiredEnabled")) {
                    var6 = 3;
                }
                break;
            case -600261888:
                if (var5.equals("lazystartproducer")) {
                    var6 = 12;
                }
                break;
            case -265713450:
                if (var5.equals("username")) {
                    var6 = 17;
                }
                break;
            case 95467907:
                if (var5.equals("delay")) {
                    var6 = 9;
                }
                break;
            case 98055755:
                if (var5.equals("bridgeErrorHandler")) {
                    var6 = 5;
                }
                break;
            case 263343872:
                if (var5.equals("lazyStartProducer")) {
                    var6 = 13;
                }
                break;
            case 642216616:
                if (var5.equals("verificationCode")) {
                    var6 = 19;
                }
                break;
            case 643169928:
                if (var5.equals("verificationcode")) {
                    var6 = 18;
                }
                break;
            case 1216985755:
                if (var5.equals("password")) {
                    var6 = 14;
                }
                break;
            case 1844854033:
                if (var5.equals("autowiredenabled")) {
                    var6 = 2;
                }
                break;
            case 1932752118:
                if (var5.equals("configuration")) {
                    var6 = 6;
                }
        }

        switch(var6) {
            case 0:
            case 1:
                return this.getOrCreateConfiguration(target).getAccessToken();
            case 2:
            case 3:
                return target.isAutowiredEnabled();
            case 4:
            case 5:
                return target.isBridgeErrorHandler();
            case 6:
                return target.getConfiguration();
            case 7:
            case 8:
                return this.getOrCreateConfiguration(target).getConsumerKey();
            case 9:
                return this.getOrCreateConfiguration(target).getDelay();
            case 10:
            case 11:
                return this.getOrCreateConfiguration(target).getJiraUrl();
            case 12:
            case 13:
                return target.isLazyStartProducer();
            case 14:
                return this.getOrCreateConfiguration(target).getPassword();
            case 15:
            case 16:
                return this.getOrCreateConfiguration(target).getPrivateKey();
            case 17:
                return this.getOrCreateConfiguration(target).getUsername();
            case 18:
            case 19:
                return this.getOrCreateConfiguration(target).getVerificationCode();
            default:
                return null;
        }
    }
}
